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
@Table(name = "busquedareciente")
public class TBusquedaReciente implements Serializable{

    @Id
    @Column(name = "idBusquedaReciente")
    private String idBusquedaReciente;

    @Column(name = "idUsuario")
    private String idUsuario;

    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private TUser usuario;

    @Column(name = "ciudad", length = 150)
    private String ciudad;

    @Column(name = "fechaRegistro")
    private Timestamp fechaRegistro;

}
