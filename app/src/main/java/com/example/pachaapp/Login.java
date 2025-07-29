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

public class Login extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private Button btnLoginGoogle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        btnLoginGoogle = findViewById(R.id.btnLoginGoogle);
        btnLoginGoogle.setOnClickListener(v -> signInWithGoogle());

    }

    public void registrarse(View v){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
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
                Log.d("LOGIN", "Google Sign-In exitoso: " + account.getEmail());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.e("LOGIN", "Google Sign-In falló: " + e.getStatusCode(), e);
                Toast.makeText(this, "Error en Google Sign-In: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("LOGIN", "Firebase authentication exitoso");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            enviarDatosLogin(user);
                        }
                    } else {
                        Log.e("LOGIN", "Firebase authentication falló", task.getException());
                        Toast.makeText(this, "Error en autenticación con Firebase", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void enviarDatosLogin(FirebaseUser user) {
        // Obtener token de Firebase
        user.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String firebaseToken = task.getResult().getToken();

                        // Crear objeto para login
                        FirebaseUserRequest userData = new FirebaseUserRequest(
                                firebaseToken,
                                user.getEmail(),
                                user.getDisplayName(),
                                user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null,
                                user.getUid()
                        );

                        // Enviar al backend para LOGIN
                        ApiService apiService = ApiClient.getApiService();
                        Call<JsonObject> call = apiService.loginWithFirebase(userData); // ⬅️ LOGIN, no register

                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                Log.d("LOGIN", "Respuesta del backend: " + response.code());

                                if (response.isSuccessful() && response.body() != null) {
                                    JsonObject result = response.body();
                                    Log.d("LOGIN", "Respuesta: " + result.toString());

                                    String type = result.get("type").getAsString();
                                    if ("success".equals(type)) {
                                        // Guardar datos localmente
                                        guardarDatosLocalmente(result.getAsJsonObject("data"));

                                        Toast.makeText(Login.this, "Login exitoso", Toast.LENGTH_SHORT).show();

                                        // Ir a Configuration
                                        Intent intent = new Intent(Login.this, Configuration.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        String mensaje = result.has("listMessage") && result.getAsJsonArray("listMessage").size() > 0 ?
                                                result.getAsJsonArray("listMessage").get(0).getAsString() : "Error desconocido";

                                        if (mensaje.contains("no registrado")) {
                                            // Usuario no existe, redirigir a registro
                                            Toast.makeText(Login.this, "Usuario no registrado. Redirigiendo al registro...", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(Login.this, Register.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(Login.this, "Error: " + mensaje, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } else {
                                    try {
                                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin error body";
                                        Log.e("LOGIN", "Error body: " + errorBody);
                                    } catch (Exception e) {
                                        Log.e("LOGIN", "Error leyendo error body: " + e.getMessage());
                                    }
                                    Toast.makeText(Login.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                Log.e("LOGIN", "Error de conexión", t);
                                Toast.makeText(Login.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        Log.e("LOGIN", "Error obteniendo token de Firebase", task.getException());
                        Toast.makeText(this, "Error obteniendo token", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarDatosLocalmente(JsonObject userData) {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // Obtener nombre y apellido por separado
        String nombre = userData.has("nombre") ? userData.get("nombre").getAsString() : "";
        String apellido = userData.has("apellido") ? userData.get("apellido").getAsString() : "";
        String nombreCompleto = (nombre + " " + apellido).trim();

        editor.putString("user_id", userData.get("idUsuario").getAsString());
        editor.putString("user_email", userData.get("email").getAsString());
        editor.putString("user_name", nombreCompleto);
        editor.putString("user_avatar", userData.get("avatar").getAsString());
        editor.putString("firebase_uid", userData.get("firebaseUid").getAsString());

        editor.apply();
    }
}