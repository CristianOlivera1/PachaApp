package com.example.pachaapp.Dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoActivity {
    private String idActividad;
    private String idUsuario;
    private String descripcion;
    private String lugar;
    private String estado;
    private Timestamp fechaActividad;
    private Timestamp fechaRegistro;
    private Timestamp fechaActualizacion;
    
    // Información adicional del usuario (para consultas)
    private String nombreUsuario;
    private String apellidoUsuario;
    private String emailUsuario;
}
