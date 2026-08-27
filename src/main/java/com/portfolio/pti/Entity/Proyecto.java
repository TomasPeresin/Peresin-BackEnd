package com.portfolio.pti.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    @Size(min = 1, max = 100, message = "no cumple la longitud")
    private String nombre;
    
    @NotNull
    @Size(min = 1, max = 2000, message = "no cumple la longitud")
    @javax.persistence.Column(length = 2000)
    private String descripcion;
    
    @NotNull
    private Integer fecha;
    
    @NotNull
    @Size(min = 1, max = 255, message = "no cumple la longitud")
    private String link;
    
    @Size(max = 500, message = "no cumple la longitud")
    @javax.persistence.Column(length = 500)
    private String img;
    
    //Constructores

    public Proyecto() {
    }

    public Proyecto(String nombre, String descripcion, Integer fecha, String link) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.link = link;
    }

    public Proyecto(String nombre, String descripcion, Integer fecha, String link, String img) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.link = link;
        this.img = img;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getFecha() {
        return fecha;
    }

    public void setFecha(Integer fecha) {
        this.fecha = fecha;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}