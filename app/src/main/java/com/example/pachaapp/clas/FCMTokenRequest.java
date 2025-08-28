package com.example.pachaapp.clas;

import com.google.gson.annotations.SerializedName;

public class FCMTokenRequest {
    @SerializedName("idUsuario")
    private String idUsuario;
    
    @SerializedName("fcmToken")
    private String fcmToken;

    public FCMTokenRequest() {}

    public FCMTokenRequest(String idUsuario, String fcmToken) {
        this.idUsuario = idUsuario;
        this.fcmToken = fcmToken;
    }

    // Getters y Setters
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
