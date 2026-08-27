package com.portfolio.pti.Security.Dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class SolicitudRecuperacionDto {

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ser un email válido")
    private String email;

    public SolicitudRecuperacionDto() {
    }

    public SolicitudRecuperacionDto(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
