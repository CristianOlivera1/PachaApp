package com.example.pachaapp.Dto;
import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRegisterUser {
    private String idUsuario;
    private String nombre;
    private String apellido;
    private String email;
    private String avatar;
    private Timestamp fechaRegistro;
    private String jwtToken;
    private String firebaseUid;

    public DtoRegisterUser() {}

    public DtoRegisterUser(String email, String nombre,String apellido, String avatar, String jwtToken) {
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.avatar = avatar;
        this.jwtToken = jwtToken;
    }
}