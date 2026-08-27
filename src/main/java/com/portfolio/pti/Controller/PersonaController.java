package com.portfolio.pti.Controller;

import com.portfolio.pti.Entity.Persona;
import com.portfolio.pti.Interface.IPersonaService;
import com.portfolio.pti.Security.Controller.Mensaje;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {"https://frontendpti.web.app", "http://localhost:4200", "*"})
public class PersonaController {
    @Autowired IPersonaService ipersonaService;
    
    @GetMapping("/personas/traer")
    public List<Persona> getPersona(){
        return ipersonaService.getPersona();
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/personas/crear")
    public String createPersona(@RequestBody Persona persona){
        ipersonaService.savePersona(persona);
        return "La persona fue creada correctamente";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/personas/borrar/{id}")
    public String deletePersona(@PathVariable Integer id){
        ipersonaService.deletePersona(id);
        return "La persona fue eliminada correctamente";
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/personas/editar/{id}")
    public Persona editPersona(@PathVariable Integer id,
                               @RequestParam("nombre") String nuevoNombre,
                               @RequestParam("apellido") String nuevoApellido,
                               @RequestParam("img") String nuevoImg){
        Persona persona = ipersonaService.findPersona(id);
        if (persona == null) {
            persona = new Persona();
        }
        
        persona.setNombre(nuevoNombre);
        persona.setApellido(nuevoApellido);
        persona.setImg(nuevoImg);
        
        ipersonaService.savePersona(persona);
        return persona;
    }

    @PutMapping("/personas/update/{id}")
    public ResponseEntity<?> updatePersona(@PathVariable("id") Integer id, @RequestBody Persona personaDto) {
        Persona persona = ipersonaService.findPersona(id);
        if (persona == null) {
            List<Persona> list = ipersonaService.getPersona();
            if (list != null && !list.isEmpty()) {
                persona = list.get(0);
            } else {
                persona = new Persona();
            }
        }
        if (personaDto.getNombre() != null && !personaDto.getNombre().trim().isEmpty()) {
            persona.setNombre(personaDto.getNombre());
        }
        if (personaDto.getApellido() != null && !personaDto.getApellido().trim().isEmpty()) {
            persona.setApellido(personaDto.getApellido());
        }
        if (personaDto.getDescripcion() != null) {
            persona.setDescripcion(personaDto.getDescripcion());
        }
        if (personaDto.getImg() != null) {
            persona.setImg(personaDto.getImg());
        }
        if (personaDto.getBanner() != null) {
            persona.setBanner(personaDto.getBanner());
        }
        
        ipersonaService.savePersona(persona);
        return new ResponseEntity<>(persona, HttpStatus.OK);
    }
    
    @GetMapping("/personas/traer/perfil")
    public Persona findPersona(){
        List<Persona> list = ipersonaService.getPersona();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        Persona persona = ipersonaService.findPersona(1);
        if (persona == null) {
            persona = new Persona("Tomás", "Peresin", 
                "Especializado en diseño y construcción de arquitecturas backend robustas, modelado de bases de datos relacionales, relevamiento funcional y aseguramiento de calidad de software.", 
                "assets/foto-perfil.jpg", "");
            ipersonaService.savePersona(persona);
        }
        return persona;
    }
   
}
