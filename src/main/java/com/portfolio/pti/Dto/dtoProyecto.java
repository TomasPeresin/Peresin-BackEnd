
package com.portfolio.pti.Dto;

import java.time.LocalDate;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class dtoProyecto {
    @NotBlank
    private String nombre;
    
    @NotBlank
    private String descripcion;
    
    @NotBlank
    private Integer fecha;

    @NotBlank
    private String link;

    public dtoProyecto() {
    }

    public dtoProyecto(String nombre, String descripcion, Integer fecha, String link) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.link = link;
    }
    
}

