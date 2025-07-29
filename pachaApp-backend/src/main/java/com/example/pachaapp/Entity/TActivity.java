package com.example.pachaapp.Entity;

import java.io.Serializable;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "actividades")
public class TActivity implements Serializable {
    @Id
    @Column(name = "idActividad")
    private String idActividad;
    
    @Column(name = "idUsuario")
    private String idUsuario;

    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private TUser usuario;
        
    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "lugar", length = 200)
    private String lugar;

    @Column(name = "estado", length = 20)
    private String estado; // "iniciado" o "concluido"

    @Column(name = "fechaActividad")
    private Timestamp fechaActividad;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

    @Column(name = "fechaActualizacion")
    private Timestamp fechaActualizacion;

}
