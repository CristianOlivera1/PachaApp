package com.example.pachaapp;


import java.io.IOException;
import java.io.InputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@Configuration
public class FirebaseConfig {

    
    @PostConstruct
    public void initialize() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                
                // Cargar archivo de credenciales desde resources
                InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase-service-account.json");
                
                if (serviceAccount == null) {
                    System.err.println("ERROR: No se encontró firebase-service-account.json en resources/");
                    System.err.println("Descarga el archivo desde Firebase Console > Project Settings > Service accounts");
                    throw new IOException("Archivo firebase-service-account.json no encontrado");
                }

                FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

                FirebaseApp.initializeApp(options);
                
            } else {
                System.out.println("Firebase ya estaba inicializado");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error critical: No se pudo inicializar Firebase", e);
        }
    }
}