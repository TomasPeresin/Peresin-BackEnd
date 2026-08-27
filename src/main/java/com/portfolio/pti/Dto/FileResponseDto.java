package com.portfolio.pti.Dto;

public class FileResponseDto {
    private String nombreArchivo;
    private String url;
    private String tipo;
    private long tamanio;
    private String mensaje;

    public FileResponseDto() {
    }

    public FileResponseDto(String nombreArchivo, String url, String tipo, long tamanio, String mensaje) {
        this.nombreArchivo = nombreArchivo;
        this.url = url;
        this.tipo = tipo;
        this.tamanio = tamanio;
        this.mensaje = mensaje;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public long getTamanio() {
        return tamanio;
    }

    public void setTamanio(long tamanio) {
        this.tamanio = tamanio;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
