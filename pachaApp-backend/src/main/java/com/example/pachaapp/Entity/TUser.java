package com.example.pachaapp.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "usuario")
public class TUser implements Serializable {
    @Id
    @Column(name = "idUsuario")
    private String idUsuario;
    
    @Column(name = "nombre")
    private String nombre;
        
    @Column(name = "apellido")
    private String apellido;

    @Column(name = "email")
    private String email;

    @Column(name = "foto")
    private String foto;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

    @Column(name = "firebaseUid") 
    private String firebaseUid;

    @Column(name = "fcmToken")
    private String fcmToken;

    @Column(name = "fechaActualizacionToken")
    private Timestamp fechaActualizacionToken;

}