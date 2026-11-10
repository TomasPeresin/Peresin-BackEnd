package com.portfolio.pti.Security.Controller;

import com.portfolio.pti.Security.Dto.CambiarPasswordDto;
import com.portfolio.pti.Security.Dto.JwtDto;
import com.portfolio.pti.Security.Dto.LoginUsuario;
import com.portfolio.pti.Security.Dto.NuevoUsuario;
import com.portfolio.pti.Security.Dto.SolicitudRecuperacionDto;
import com.portfolio.pti.Security.Entity.Rol;
import com.portfolio.pti.Security.Entity.Usuario;
import com.portfolio.pti.Security.Enums.RolNombre;
import com.portfolio.pti.Security.Service.EmailService;
import com.portfolio.pti.Security.Service.LoginAttemptService;
import com.portfolio.pti.Security.Service.RolService;
import com.portfolio.pti.Security.Service.UsuarioService;
import com.portfolio.pti.Security.jwt.JwtProvider;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"https://frontendpti.web.app", "http://localhost:4200", "http://localhost:3000", "*"})
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Value("${app.admin.secret:admin123}")
    private String adminSecret;

    @PostMapping("/nuevo")
    public ResponseEntity<?> nuevo(@Valid @RequestBody NuevoUsuario nuevoUsuario, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .findFirst()
                    .orElse("Campos inválidos en la solicitud");
            return new ResponseEntity<>(new Mensaje(errorMsg), HttpStatus.BAD_REQUEST);
        }

        if (usuarioService.existsByNombreUsuario(nuevoUsuario.getNombreUsuario().trim())) {
            return new ResponseEntity<>(new Mensaje("Ese nombre de usuario ya existe"), HttpStatus.BAD_REQUEST);
        }

        if (usuarioService.existsByEmail(nuevoUsuario.getEmail().trim())) {
            return new ResponseEntity<>(new Mensaje("Ese correo electrónico ya está registrado"), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = new Usuario(
                nuevoUsuario.getNombre().trim(),
                nuevoUsuario.getNombreUsuario().trim(),
                nuevoUsuario.getEmail().trim(),
                passwordEncoder.encode(nuevoUsuario.getPassword())
        );

        Set<Rol> roles = new HashSet<>();
        roles.add(rolService.getOrCreateRol(RolNombre.ROLE_USER));

        // Validación de Clave Secreta de Administrador
        if (nuevoUsuario.getCodigoAdmin() != null && !nuevoUsuario.getCodigoAdmin().trim().isEmpty()) {
            if (adminSecret.trim().equals(nuevoUsuario.getCodigoAdmin().trim())) {
                roles.add(rolService.getOrCreateRol(RolNombre.ROLE_ADMIN));
            } else {
                return new ResponseEntity<>(new Mensaje("La clave secreta de administrador es incorrecta"), HttpStatus.BAD_REQUEST);
            }
        } else if (nuevoUsuario.getRoles() != null && (nuevoUsuario.getRoles().contains("admin") || nuevoUsuario.getRoles().contains("ROLE_ADMIN"))) {
            // Si intenta pasar rol admin sin clave secreta
            return new ResponseEntity<>(new Mensaje("Para crear una cuenta de administrador debe ingresar la clave secreta"), HttpStatus.BAD_REQUEST);
        }

        usuario.setRoles(roles);
        usuarioService.save(usuario);

        return new ResponseEntity<>(new Mensaje("Usuario registrado exitosamente"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginUsuario loginUsuario,
            BindingResult bindingResult,
            HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Campos incompletos o incorrectos"), HttpStatus.BAD_REQUEST);
        }

        String clientKey = loginAttemptService.buildClientKey(request, loginUsuario.getNombreUsuario());
        String ipKey = "ip:" + loginAttemptService.extractClientIp(request);

        // Verificar si el usuario/IP ya se encuentra bloqueado temporalmente en caché
        if (loginAttemptService.isBlocked(clientKey) || loginAttemptService.isBlocked(ipKey)) {
            long lockSeconds = Math.max(
                    loginAttemptService.getRemainingLockSeconds(clientKey),
                    loginAttemptService.getRemainingLockSeconds(ipKey)
            );
            Map<String, Object> body = new HashMap<>();
            body.put("mensaje", "Demasiados intentos fallidos. Acceso bloqueado por seguridad durante "
                    + loginAttemptService.formatRemainingTime(lockSeconds) + ".");
            body.put("intentosRestantes", 0);
            body.put("bloqueadoSegundos", lockSeconds);
            return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUsuario.getNombreUsuario().trim(),
                            loginUsuario.getPassword()
                    )
            );

            // Login exitoso: limpiar caché de intentos fallidos
            loginAttemptService.resetAttempts(clientKey);
            loginAttemptService.resetAttempts(ipKey);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtProvider.generateToken(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            JwtDto jwtDto = new JwtDto(jwt, userDetails.getUsername(), userDetails.getAuthorities());
            return new ResponseEntity<>(jwtDto, HttpStatus.OK);
        } catch (Exception e) {
            int remainingUser = loginAttemptService.recordFailedAttempt(clientKey);
            int remainingIp = loginAttemptService.recordFailedAttempt(ipKey);
            int remaining = Math.min(remainingUser, remainingIp);

            Map<String, Object> body = new HashMap<>();
            body.put("intentosRestantes", remaining);

            if (remaining <= 0) {
                long lockSeconds = LoginAttemptService.LOCK_DURATION_MINUTES * 60L;
                body.put("mensaje", "Has superado el límite de " + LoginAttemptService.MAX_ATTEMPTS
                        + " intentos fallidos. Tu acceso ha sido bloqueado por "
                        + LoginAttemptService.LOCK_DURATION_MINUTES + " minutos.");
                body.put("bloqueadoSegundos", lockSeconds);
                return new ResponseEntity<>(body, HttpStatus.TOO_MANY_REQUESTS);
            }

            body.put("mensaje", "Nombre de usuario o contraseña incorrectos. Te quedan " + remaining + " intento(s).");
            return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/recuperar-password")
    public ResponseEntity<?> recuperarPassword(@Valid @RequestBody SolicitudRecuperacionDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(new Mensaje("Debe ingresar un correo electrónico válido"), HttpStatus.BAD_REQUEST);
        }

        Optional<Usuario> usuarioOpt = usuarioService.getByEmail(dto.getEmail().trim());
        if (!usuarioOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("No existe ningún usuario registrado con ese correo electrónico"), HttpStatus.NOT_FOUND);
        }

        Usuario usuario = usuarioOpt.get();
        String token = UUID.randomUUID().toString();
        usuario.setTokenPassword(token);
        usuario.setTokenPasswordExpiration(LocalDateTime.now().plusMinutes(30));
        usuarioService.save(usuario);

        emailService.sendPasswordResetEmail(usuario.getEmail(), usuario.getNombre(), token);

        return new ResponseEntity<>(new Mensaje("Se ha enviado un correo con las instrucciones para restablecer tu contraseña."), HttpStatus.OK);
    }

    @GetMapping("/verificar-token/{token}")
    public ResponseEntity<?> verificarToken(@PathVariable("token") String token) {
        Optional<Usuario> usuarioOpt = usuarioService.getByTokenPassword(token);
        if (!usuarioOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("El enlace de recuperación no es válido"), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getTokenPasswordExpiration() == null || usuario.getTokenPasswordExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new Mensaje("El enlace de recuperación ha expirado"), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(new Mensaje("Token válido"), HttpStatus.OK);
    }

    @PostMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(@Valid @RequestBody CambiarPasswordDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .findFirst()
                    .orElse("Datos inválidos");
            return new ResponseEntity<>(new Mensaje(errorMsg), HttpStatus.BAD_REQUEST);
        }

        Optional<Usuario> usuarioOpt = usuarioService.getByTokenPassword(dto.getToken().trim());
        if (!usuarioOpt.isPresent()) {
            return new ResponseEntity<>(new Mensaje("El token es inválido o ya ha sido utilizado"), HttpStatus.BAD_REQUEST);
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getTokenPasswordExpiration() == null || usuario.getTokenPasswordExpiration().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new Mensaje("El token ha expirado. Por favor solicita un nuevo enlace."), HttpStatus.BAD_REQUEST);
        }

        usuario.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        usuario.setTokenPassword(null);
        usuario.setTokenPasswordExpiration(null);
        usuarioService.save(usuario);

        return new ResponseEntity<>(new Mensaje("Tu contraseña ha sido actualizada correctamente."), HttpStatus.OK);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> listUsuarios() {
        List<Usuario> list = usuarioService.list();
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
