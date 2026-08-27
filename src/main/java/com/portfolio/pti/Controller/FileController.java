package com.portfolio.pti.Controller;

import com.portfolio.pti.Dto.FileResponseDto;
import com.portfolio.pti.Interface.IFileStorageService;
import com.portfolio.pti.Security.Controller.Mensaje;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/files")
@CrossOrigin(origins = {"https://frontendpti.web.app", "http://localhost:4200"})
public class FileController {

    @Autowired
    private IFileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        try {
            String nombreArchivo = fileStorageService.guardarImagen(file);

            // Construir URL pública directa
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(nombreArchivo)
                    .toUriString();

            FileResponseDto response = new FileResponseDto(
                    nombreArchivo,
                    fileDownloadUri,
                    file.getContentType(),
                    file.getSize(),
                    "Imagen subida exitosamente"
            );

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(new Mensaje(e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new Mensaje("Error al procesar la imagen: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename, HttpServletRequest request) {
        try {
            Resource resource = fileStorageService.cargarComoRecurso(filename);
            String contentType = null;
            try {
                contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            } catch (IOException ex) {
                // Fallback default
            }
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{filename:.+}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        boolean eliminado = fileStorageService.eliminarImagen(filename);
        if (eliminado) {
            return new ResponseEntity<>(new Mensaje("Imagen eliminada exitosamente"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new Mensaje("No se pudo encontrar o eliminar la imagen"), HttpStatus.NOT_FOUND);
        }
    }
}
