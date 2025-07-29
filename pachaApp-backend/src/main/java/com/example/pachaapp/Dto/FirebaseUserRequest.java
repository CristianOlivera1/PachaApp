package com.example.pachaapp.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseUserRequest {
    private String firebaseToken;
    private String email;
    private String apellido;
    private String nombre;
    private String photoUrl;
    private String uid;

    public FirebaseUserRequest() {}

    @Override
    public String toString() {
        return "FirebaseUserRequest{" +
                "email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                   ", nombre='" + apellido + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", uid='" + uid + '\'' +
                ", tokenPresent=" + (firebaseToken != null) +
                '}';
    }
}