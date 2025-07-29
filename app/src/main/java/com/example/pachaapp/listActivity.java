package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pachaapp.clas.Activity;
import com.example.pachaapp.clas.ActivityAdapter;
import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiResponse;
import com.example.pachaapp.clas.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class listActivity extends AppCompatActivity {

    private RecyclerView recyclerViewActividades;
    private ActivityAdapter activityAdapter;
    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private List<Activity> activitiesList;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        getUserId();
        setupRecyclerView();
        loadActivities();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar actividades cuando se regrese a esta pantalla
        loadActivities();
    }

    private void initializeViews() {
        recyclerViewActividades = findViewById(R.id.recyclerViewActividades);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        activitiesList = new ArrayList<>();
    }

    private void getUserId() {
        if (!isUserLoggedIn()) {
            Toast.makeText(this, "Usuario no logueado", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
        
        if (userId == null) {
            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            finish();
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

    private void setupRecyclerView() {
        activityAdapter = new ActivityAdapter(this, activitiesList);
        recyclerViewActividades.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewActividades.setAdapter(activityAdapter);
    }

    private void loadActivities() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Activity>>> call = apiService.getActivitiesByUser(userId);

        call.enqueue(new Callback<ApiResponse<List<Activity>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Activity>>> call, Response<ApiResponse<List<Activity>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Activity>> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        List<Activity> activities = apiResponse.getData();
                        
                        if (activities != null && !activities.isEmpty()) {
                            activitiesList.clear();
                            activitiesList.addAll(activities);
                            activityAdapter.updateActivities(activitiesList);
                            showEmptyState(false);
                            
                            Log.d("ListActivity", "Cargadas " + activities.size() + " actividades");
                        } else {
                            showEmptyState(true);
                        }
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(listActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                    }
                } else {
                    Toast.makeText(listActivity.this, "Error al cargar las actividades", Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                    Log.e("ListActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Activity>>> call, Throwable t) {
                showLoading(false);
                showEmptyState(true);
                Toast.makeText(listActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("ListActivity", "Error de conexión: ", t);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewActividades.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewActividades.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void crearActividad(View v) {
        Intent intent = new Intent(this, CreateActivity.class);
        startActivity(intent);
    }

    public void retroceder(View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}