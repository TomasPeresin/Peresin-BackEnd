package com.portfolio.pti.Security.Dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class CambiarPasswordDto {

    @NotBlank(message = "El token es obligatorio")
    private String token;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String newPassword;

    public CambiarPasswordDto() {
    }

    public CambiarPasswordDto(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
