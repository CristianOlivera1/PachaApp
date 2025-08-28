package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiService;
import com.example.pachaapp.clas.FirebaseUserRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnRegisterGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnRegisterGoogle = findViewById(R.id.btnRegisterGoogle);
        btnRegisterGoogle.setOnClickListener(v -> signInWithGoogle());

    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Error en Google Sign-In: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            enviarDatosAlBackend(user);
                        }
                    } else {
                        Log.e("REGISTER", "Firebase authentication falló", task.getException());
                        Toast.makeText(this, "Error en autenticación con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void enviarDatosAlBackend(FirebaseUser user) {
        // Obtener token de Firebase para verificación en backend
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String firebaseToken = task.getResult().getToken();
                        // Crear objeto POJO en lugar de JsonObject
                        FirebaseUserRequest userData = new FirebaseUserRequest(
                                firebaseToken,
                                user.getEmail(),
                                user.getDisplayName(),
                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null,
                                user.getUid()
                        );

                        // Enviar al backend
                        ApiService apiService = ApiClient.getApiService();
                        Call<JsonObject> call = apiService.registerWithFirebase(userData);

                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    JsonObject result = response.body();

                                    String type = result.get("type").getAsString();
                                    if ("success".equals(type)) {
                                        // Guardar datos localmente
                                        guardarDatosLocalmente(result.getAsJsonObject("data"));

                                        Toast.makeText(Register.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                                        // Ir a la siguiente actividad
                                        Intent intent = new Intent(Register.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        String mensaje = result.has("listMessage") && result.getAsJsonArray("listMessage").size() > 0 ?
                                                result.getAsJsonArray("listMessage").get(0).getAsString() : "Error desconocido";
                                        Toast.makeText(Register.this, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin error body";
                                        Log.e("REGISTER", "Error body: " + errorBody);
                                    } catch (Exception e) {
                                        Log.e("REGISTER", "Error leyendo error body: " + e.getMessage());
                                    }
                                    Toast.makeText(Register.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Toast.makeText(Register.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        Toast.makeText(this, "Error obteniendo token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosLocalmente(JsonObject userData) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String nombre = userData.has("nombre") ? userData.get("nombre").getAsString() : "";
        String apellido = userData.has("apellido") ? userData.get("apellido").getAsString() : "";
        String nombreCompleto = (nombre + " " + apellido).trim();

        // Guardar datos individuales
        editor.putString("user_id", userData.get("idUsuario").getAsString());
        editor.putString("user_email", userData.get("email").getAsString());
        editor.putString("user_name", nombreCompleto);
        editor.putString("user_nombre", nombre);
        editor.putString("user_apellido", apellido);
        editor.putString("user_avatar", userData.get("avatar").getAsString());
        editor.putString("firebase_uid", userData.get("firebaseUid").getAsString());

        editor.apply();
    }
    public void iniciarSesion(View v){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}