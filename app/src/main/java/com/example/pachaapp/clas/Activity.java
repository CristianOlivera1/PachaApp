package com.example.pachaapp.clas;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Activity {
    @SerializedName("idActividad")
    private String idActividad;
    
    @SerializedName("idUsuario")
    private String idUsuario;
    
    @SerializedName("descripcion")
    private String descripcion;
    
    @SerializedName("lugar")
    private String lugar;
    
    @SerializedName("estado")
    private String estado;
    
    @SerializedName("fechaActividad")
    private long fechaActividad;
    
    @SerializedName("fechaRegistro")
    private long fechaRegistro;
    
    @SerializedName("fechaActualizacion")
    private long fechaActualizacion;
    
    // Información adicional del usuario
    @SerializedName("nombreUsuario")
    private String nombreUsuario;
    
    @SerializedName("apellidoUsuario")
    private String apellidoUsuario;
    
    @SerializedName("emailUsuario")
    private String emailUsuario;

    // Constructor vacío
    public Activity() {}

    // Constructor completo
    public Activity(String idActividad, String idUsuario, String descripcion, String lugar, 
                   String estado, long fechaActividad, long fechaRegistro, long fechaActualizacion) {
        this.idActividad = idActividad;
        this.idUsuario = idUsuario;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.estado = estado;
        this.fechaActividad = fechaActividad;
        this.fechaRegistro = fechaRegistro;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters
    public String getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public long getFechaActividad() {
        return fechaActividad;
    }

    public void setFechaActividad(long fechaActividad) {
        this.fechaActividad = fechaActividad;
    }

    public long getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public long getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(long fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    // Métodos de utilidad
    public Date getFechaActividadAsDate() {
        return new Date(fechaActividad);
    }

    public Date getFechaRegistroAsDate() {
        return new Date(fechaRegistro);
    }

    public boolean isCompleted() {
        return "concluido".equals(estado);
    }

    public boolean isActive() {
        return "iniciado".equals(estado);
    }
}
