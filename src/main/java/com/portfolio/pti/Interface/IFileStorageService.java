package com.portfolio.pti.Interface;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IFileStorageService {
    
    /**
     * Valida y guarda una imagen en el almacenamiento, generando un nombre único seguro.
     * @param archivo Archivo MultipartFile recibido
     * @return Nombre del archivo generado
     */
    String guardarImagen(MultipartFile archivo);
    
    /**
     * Carga un archivo previamente almacenado como recurso para ser descargado o servido.
     * @param nombreArchivo Nombre del archivo
     * @return Recurso Spring
     */
    Resource cargarComoRecurso(String nombreArchivo);
    
    /**
     * Elimina una imagen del almacenamiento.
     * @param nombreArchivo Nombre del archivo a eliminar
     * @return true si se eliminó exitosamente, false si no se encontró
     */
    boolean eliminarImagen(String nombreArchivo);
}
