package com.example.pachaapp.clas;


public class FirebaseUserRequest {
    private String firebaseToken;
    private String email;
    private String nombre;
    private String photoUrl;
    private String uid;

    public FirebaseUserRequest() {}

    public FirebaseUserRequest(String firebaseToken, String email, String nombre, String photoUrl, String uid) {
        this.firebaseToken = firebaseToken;
        this.email = email;
        this.nombre = nombre;
        this.photoUrl = photoUrl;
        this.uid = uid;
    }

    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    @Override
    public String toString() {
        return "FirebaseUserRequest{" +
                "email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", uid='" + uid + '\'' +
                ", tokenPresent=" + (firebaseToken != null) +
                '}';
    }
}