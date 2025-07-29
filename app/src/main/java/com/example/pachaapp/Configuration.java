package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Configuration extends AppCompatActivity {
    private TextView tvNombreCompleto, tvEmail;
    ImageView ivAvatar;
    private Button btnCerrarSesion;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_configuration);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvEmail = findViewById(R.id.tvEmail);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        cargarDatosUsuario();

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
    }
    private void cargarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);

        // Debug: Mostrar todas las claves guardadas
        for (String key : prefs.getAll().keySet()) {
            Log.d("CONFIG", "- " + key + ": " + prefs.getString(key, "null"));
        }

        // Obtener datos (con valores por defecto para debug)
        String userName = prefs.getString("user_name", "Usuario no encontrado");
        String userEmail = prefs.getString("user_email", "Email no encontrado");
        String avatarUrl = prefs.getString("user_avatar", null);

        // Actualizar UI
        tvNombreCompleto.setText(userName);
        tvEmail.setText(userEmail);

        // Cargar avatar con Glide
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.error_avatar)
                    .circleCrop()
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.default_avatar);
        }
    }

    private void cerrarSesion() {

        // Mostrar diálogo de confirmación
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    ejecutarCierreSesion();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void ejecutarCierreSesion() {
        try {
            if (mAuth.getCurrentUser() != null) {
                mAuth.signOut();
            }

            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                limpiarDatosLocales();
                irALogin();
            });

        } catch (Exception e) {
            limpiarDatosLocales();
            irALogin();
        }
    }

    private void limpiarDatosLocales() {
        // Limpiar SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Elimina todos los datos
        editor.apply();
    }

    private void irALogin() {
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();

        // Ir a la actividad de MainActivity
        Intent intent = new Intent(Configuration.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}