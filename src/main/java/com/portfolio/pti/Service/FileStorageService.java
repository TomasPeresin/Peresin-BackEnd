package com.portfolio.pti.Service;

import com.portfolio.pti.Interface.IFileStorageService;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService implements IFileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private Path rootLocation;

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp", "gif", "svg");
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/webp", "image/gif", "image/svg+xml"
    );

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta de almacenamiento en: " + this.rootLocation, e);
        }
    }

    @Override
    public String guardarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("No se puede guardar un archivo vacío.");
        }

        String contentType = archivo.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Tipo de archivo no permitido. Solo se aceptan imágenes (JPEG, PNG, WebP, GIF, SVG).");
        }

        String originalFilename = archivo.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "archivo.jpg";
        }

        // Sanitización para prevenir ataques de Path Traversal
        String cleanFilename = Paths.get(originalFilename).getFileName().toString();
        String extension = "";
        int dotIndex = cleanFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = cleanFilename.substring(dotIndex + 1).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("Extensión de archivo no permitida: ." + extension);
        }

        // Nombre único seguro con UUID
        String nuevoNombreArchivo = UUID.randomUUID().toString() + "." + extension;
        Path destino = this.rootLocation.resolve(nuevoNombreArchivo).normalize();

        // Validar que el archivo se guarde dentro del directorio previsto
        if (!destino.startsWith(this.rootLocation)) {
            throw new SecurityException("Operación no permitida: intento de almacenamiento fuera del directorio configurado.");
        }

        try (InputStream inputStream = archivo.getInputStream()) {
            Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            return nuevoNombreArchivo;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + nuevoNombreArchivo, e);
        }
    }

    @Override
    public Resource cargarComoRecurso(String nombreArchivo) {
        try {
            String cleanFilename = Paths.get(nombreArchivo).getFileName().toString();
            Path archivo = this.rootLocation.resolve(cleanFilename).normalize();

            if (!archivo.startsWith(this.rootLocation)) {
                throw new SecurityException("Acceso a ruta de archivo inválida.");
            }

            Resource recurso = new UrlResource(archivo.toUri());
            if (recurso.exists() && recurso.isReadable()) {
                return recurso;
            } else {
                throw new RuntimeException("No se pudo leer o no existe el archivo: " + nombreArchivo);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL de archivo mal formada: " + nombreArchivo, e);
        }
    }

    @Override
    public boolean eliminarImagen(String nombreArchivo) {
        if (StringUtils.isBlank(nombreArchivo)) {
            return false;
        }
        try {
            String cleanFilename = Paths.get(nombreArchivo).getFileName().toString();
            Path archivo = this.rootLocation.resolve(cleanFilename).normalize();
            if (!archivo.startsWith(this.rootLocation)) {
                return false;
            }
            return Files.deleteIfExists(archivo);
        } catch (IOException e) {
            return false;
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}
