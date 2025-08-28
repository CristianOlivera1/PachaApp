package com.example.pachaapp.Dto;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoBusquedaReciente {
    private String idBusquedaReciente;
    private String idUsuario;
    private String ciudad;
    private Timestamp fechaRegistro;

      // Datos del usuario (opcional para devolver al frontend)
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
}
