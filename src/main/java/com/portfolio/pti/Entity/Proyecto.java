
package com.portfolio.pti.Entity;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
public class Proyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple la longiutd")
    private String nombre;
    
    @NotNull
    @Size(min = 1, max = 255, message = "no cumple la longiutd")
    private String descripcion;
    
    @NotNull
    private Integer fecha;
    
    @NotNull
    @Size(min = 1, max = 50, message = "no cumple la longiutd")
    private String link;
    
    //Constructores

    public Proyecto() {
    }

    public Proyecto(String nombre, String descripcion, Integer fecha, String link) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.link = link;
    }
}