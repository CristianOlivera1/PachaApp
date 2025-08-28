package com.example.pachaapp.clas;

import com.google.gson.annotations.SerializedName;

public class Busqueda {

    @SerializedName("idBusquedaReciente")
    private String idBusquedaReciente;

    @SerializedName("idUsuario")
    private String idUsuario;

    @SerializedName("ciudad")
    private String ciudad;

    @SerializedName("fechaRegistro")
    private long fechaRegistro;


    // Información adicional del usuario
    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("apellidoUsuario")
    private String apellidoUsuario;

    @SerializedName("emailUsuario")
    private String emailUsuario;

    // Constructor vacío
    public Busqueda() {}

    public Busqueda(String apellidoUsuario, String nombreUsuario, String idUsuario,
                    String idBusquedaReciente, long fechaRegistro, String emailUsuario,
                    String ciudad) {
        this.apellidoUsuario = apellidoUsuario;
        this.nombreUsuario = nombreUsuario;
        this.idUsuario = idUsuario;
        this.idBusquedaReciente = idBusquedaReciente;
        this.fechaRegistro = fechaRegistro;
        this.emailUsuario = emailUsuario;
        this.ciudad = ciudad;
    }

    public String getApellidoUsuario() {
        return apellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        this.apellidoUsuario = apellidoUsuario;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public long getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(long fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getIdBusquedaReciente() {
        return idBusquedaReciente;
    }

    public void setIdBusquedaReciente(String idBusquedaReciente) {
        this.idBusquedaReciente = idBusquedaReciente;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}
