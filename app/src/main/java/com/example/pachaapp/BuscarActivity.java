package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.example.pachaapp.clas.Busqueda;
import com.example.pachaapp.clas.BusquedaAdapter;
import com.example.pachaapp.clas.WeatherApiService;
import com.example.pachaapp.clas.WeatherResponse;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BuscarActivity extends AppCompatActivity {
    private TextView textCiudadResultado;
    private TextView textTemperatura;
    private EditText editTextCiudad;
    private WeatherApiService weatherAPI;
    private String userId;
    private final String apiKey = "8ae91641bc3d122541f4c2bbb2195f18";

    private ProgressBar progressBar;
    private LinearLayout emptyState;
    private BusquedaAdapter busquedaAdapter;
    private List<Busqueda> busquedasList;

    private RecyclerView recyclerViewBusquedas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar vistas primero
        initializeViews();

        // Regresar al MainActivity
        ImageView btnVolver = findViewById(R.id.btnVolver);

        // Buscar clima
        ImageView btnBuscar = findViewById(R.id.btnBuscar);
        editTextCiudad = findViewById(R.id.editTextCiudad);
        textCiudadResultado = findViewById(R.id.textCiudadResultado);
        textTemperatura = findViewById(R.id.textTemperatura);


        //Configurar Retrofit

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherAPI = retrofit.create(WeatherApiService.class);

        getUserId();


        setupRecyclerView();
        loadActivities();

    }


    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        if (userId == null) {
            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void buscarClima(View v){

        String ciudad = editTextCiudad.getText().toString().trim();
        if (!ciudad.isEmpty()){
            obtenerClima(ciudad);
        } else {
            textCiudadResultado.setText("Ingrese una ciudad.");
        }
    }

    public void obtenerClima(String ciudad){
        Call<WeatherResponse> call = weatherAPI.getCurrentWeather(ciudad, apiKey,"metric", "es");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();

                    String ciudad = weatherData.getName();
                    String pais = weatherData.getSys().getCountry();
                    double temperatura = weatherData.getMain().getTemp();
                    double sensacion = weatherData.getMain().getFeelsLike();
                    double tempMin = weatherData.getMain().getTempMin();
                    double tempMax = weatherData.getMain().getTempMax();
                    int humedad = weatherData.getMain().getHumidity();
                    double viento = weatherData.getWind().getSpeed();
                    String descripcion = weatherData.getWeather().get(0).getDescription();
                    String iconCode = weatherData.getWeather().get(0).getIcon();

                    String textoClima = ciudad + ", " + pais + "\n"
                            + descripcion + "\n"
                            + tempMin + "° / " + tempMax + "°";

                    textTemperatura.setText(temperatura + "°C");


                    textCiudadResultado.setText(textoClima);

                    crearBusquedaEnServidor(ciudad);


                } else {
                    textCiudadResultado.setText("❌ No se pudo obtener el clima.");
                    Log.e("CLIMA","Error HTTP: "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textCiudadResultado.setText("❌ Error de conexión.");
                Log.e("CLIMA","Fallo en conexión: ", t);

            }
        });
    }

    public void volver (View v) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void crearBusquedaEnServidor(String ciudad) {


        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Busqueda>> call = apiService.createBusqueda(userId, ciudad);

        call.enqueue(new Callback<ApiResponse<Busqueda>>() {
            @Override
            public void onResponse(Call<ApiResponse<Busqueda>> call, Response<ApiResponse<Busqueda>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Busqueda> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        Toast.makeText(BuscarActivity.this, "Busqueda creada correctamente", Toast.LENGTH_SHORT).show();

                        loadActivities();

                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(BuscarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(BuscarActivity.this, "Error al crear la busqueda", Toast.LENGTH_SHORT).show();
                    Log.e("CreateBusqueda", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Busqueda>> call, Throwable t) {

                Toast.makeText(BuscarActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("CreateActivity", "Error de conexión: ", t);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Recargar actividades cuando se regrese a esta pantalla
        loadActivities();
    }

    private void initializeViews() {
        recyclerViewBusquedas = findViewById(R.id.recyclerViewBusquedas);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        busquedasList = new ArrayList<>();
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
        busquedaAdapter = new BusquedaAdapter(this, busquedasList);

        recyclerViewBusquedas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBusquedas.setAdapter(busquedaAdapter);
    }

    private void loadActivities() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<List<Busqueda>>> call = apiService.getBusquedaRecienteByUser(userId);

        call.enqueue(new Callback<ApiResponse<List<Busqueda>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Busqueda>>> call, Response<ApiResponse<List<Busqueda>>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Busqueda>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<Busqueda> busquedas = apiResponse.getData();

                        if (busquedas != null && !busquedas.isEmpty()) {

                            // 👉 Aquí invertimos el orden de la lista
                            Collections.sort(busquedas, (b1, b2) -> Long.compare(b2.getFechaRegistro(), b1.getFechaRegistro()));
                            busquedasList.clear();
                            busquedasList.addAll(busquedas);
                            busquedaAdapter.updateBusqueda(busquedasList);
                            showEmptyState(false);

                            Log.d("ListActivity", "Cargadas " + busquedas.size() + " actividades");
                        } else {
                            showEmptyState(true);
                        }
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(BuscarActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        showEmptyState(true);
                    }
                } else {
                    Toast.makeText(BuscarActivity.this, "Error al cargar las actividades", Toast.LENGTH_SHORT).show();
                    showEmptyState(true);
                    Log.e("ListActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Busqueda>>> call, Throwable t) {
                showLoading(false);
                showEmptyState(true);
                Toast.makeText(BuscarActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("ListActivity", "Error de conexión: ", t);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBusquedas.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showEmptyState(boolean show) {
        emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerViewBusquedas.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}