package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void listarActividades(View v) {
        if (isUserLoggedIn()) {
            Intent intent = new Intent(this, listActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    public void configuracion(View v) {
        if (isUserLoggedIn()) {
            Intent intent = new Intent(this, Configuration.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        String userId = prefs.getString("user_id", null);
        String userEmail = prefs.getString("user_email", null);
        String firebaseUid = prefs.getString("firebase_uid", null);

        // Usuario está logueado si tiene datos básicos
        boolean isLoggedIn = (userId != null && !userId.isEmpty()) &&
                (userEmail != null && !userEmail.isEmpty()) &&
                (firebaseUid != null && !firebaseUid.isEmpty());
        return isLoggedIn;
    }
}