package com.example.pachaapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pachaapp.clas.Activity;
import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiResponse;
import com.example.pachaapp.clas.ApiService;
import com.example.pachaapp.clas.WeatherApiClient;
import com.example.pachaapp.clas.WeatherApiService;
import com.example.pachaapp.clas.WeatherResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class detailActivity extends AppCompatActivity {

    private TextView tvDescripcionDetail, tvLugarDetail, tvFechaDetail, tvEstadoDetail;
    private Button btnMarcarCompletado, btnEditar, btnEliminar;
    private ProgressBar progressBarAction;
    
    // Elementos para información meteorológica
    private LinearLayout weatherDetailLayout;
    private ImageView ivWeatherIconDetail;
    private TextView tvTempDetail, tvWeatherDescDetail, tvTempMinMax, tvHumidity, tvPressure;
    
    private String idActividad;
    private String descripcion;
    private String lugar;
    private String estado;
    private long fechaActividad;
    private String userId;
    
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        getUserId();
        getIntentData();
        setupUI();
        loadWeatherData(); // Cargar datos meteorológicos
    }

    private void initializeViews() {
        tvDescripcionDetail = findViewById(R.id.tvDescripcionDetail);
        tvLugarDetail = findViewById(R.id.tvLugarDetail);
        tvFechaDetail = findViewById(R.id.tvFechaDetail);
        tvEstadoDetail = findViewById(R.id.tvEstadoDetail);
        
        btnMarcarCompletado = findViewById(R.id.btnMarcarCompletado);
        btnEditar = findViewById(R.id.btnEditar);
        btnEliminar = findViewById(R.id.btnEliminar);
        progressBarAction = findViewById(R.id.progressBarAction);
        
        // Elementos meteorológicos
        weatherDetailLayout = findViewById(R.id.weatherDetailLayout);
        ivWeatherIconDetail = findViewById(R.id.ivWeatherIconDetail);
        tvTempDetail = findViewById(R.id.tvTempDetail);
        tvWeatherDescDetail = findViewById(R.id.tvWeatherDescDetail);
        tvTempMinMax = findViewById(R.id.tvTempMinMax);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvPressure = findViewById(R.id.tvPressure);
        
        dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        idActividad = intent.getStringExtra("idActividad");
        descripcion = intent.getStringExtra("descripcion");
        lugar = intent.getStringExtra("lugar");
        estado = intent.getStringExtra("estado");
        fechaActividad = intent.getLongExtra("fechaActividad", 0);
    }

    private void setupUI() {
        // Mostrar datos
        tvDescripcionDetail.setText(descripcion);
        
        if (lugar != null && !lugar.isEmpty()) {
            tvLugarDetail.setText(lugar);
        } else {
            tvLugarDetail.setText("No especificado");
        }
        
        tvFechaDetail.setText(dateFormat.format(new Date(fechaActividad)));
        tvEstadoDetail.setText(estado.toUpperCase());

        // Configurar colores según el estado
        if ("concluido".equals(estado)) {
            tvEstadoDetail.setTextColor(ContextCompat.getColor(this, R.color.green));
            btnMarcarCompletado.setVisibility(View.GONE);
        } else {
            tvEstadoDetail.setTextColor(ContextCompat.getColor(this, R.color.cyan));
            btnMarcarCompletado.setVisibility(View.VISIBLE);
        }
    }

    public void marcarComoCompletado(View v) {
        if ("concluido".equals(estado)) {
            Toast.makeText(this, "La actividad ya está completada", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Estás seguro de que quieres marcar esta actividad como completada?")
                .setPositiveButton("Sí", (dialog, which) -> {
                    completarActividad();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void completarActividad() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Activity>> call = apiService.markActivityAsCompleted(idActividad, userId);

        call.enqueue(new Callback<ApiResponse<Activity>>() {
            @Override
            public void onResponse(Call<ApiResponse<Activity>> call, Response<ApiResponse<Activity>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Activity> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(detailActivity.this, "Actividad marcada como completada", Toast.LENGTH_SHORT).show();
                        
                        // Actualizar estado local
                        estado = "concluido";
                        setupUI(); // Actualizar UI
                        
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(detailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(detailActivity.this, "Error al completar la actividad", Toast.LENGTH_SHORT).show();
                    Log.e("DetailActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Activity>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(detailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("DetailActivity", "Error de conexión: ", t);
            }
        });
    }

    public void editarActividad(View v) {
        Intent intent = new Intent(this, editActivity.class);
        intent.putExtra("idActividad", idActividad);
        intent.putExtra("descripcion", descripcion);
        intent.putExtra("lugar", lugar);
        intent.putExtra("estado", estado);
        intent.putExtra("fechaActividad", fechaActividad);
        startActivity(intent);
    }

    public void eliminarActividad(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar eliminación")
                .setMessage("¿Estás seguro de que quieres eliminar esta actividad? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    realizarEliminacion();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void realizarEliminacion() {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<String>> call = apiService.deleteActivity(idActividad, userId);

        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(detailActivity.this, "Actividad eliminada correctamente", Toast.LENGTH_SHORT).show();
                        
                        // Regresar a la lista
                        Intent intent = new Intent(detailActivity.this, listActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(detailActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(detailActivity.this, "Error al eliminar la actividad", Toast.LENGTH_SHORT).show();
                    Log.e("DetailActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(detailActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("DetailActivity", "Error de conexión: ", t);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBarAction.setVisibility(show ? View.VISIBLE : View.GONE);
        btnMarcarCompletado.setEnabled(!show);
        btnEditar.setEnabled(!show);
        btnEliminar.setEnabled(!show);
    }

    public void retroceder(View v) {
        Intent intent = new Intent(this, listActivity.class);
        startActivity(intent);
    }
    
    private void loadWeatherData() {
        Log.d("WeatherDetail", "Cargando datos meteorológicos para: " + lugar);
        if (lugar == null || lugar.trim().isEmpty()) {
            Log.d("WeatherDetail", "No hay lugar especificado, ocultando sección meteorológica");
            weatherDetailLayout.setVisibility(View.GONE);
            return;
        }
        
        Log.d("WeatherDetail", "Haciendo petición a API del clima para: " + lugar);
        WeatherApiService weatherService = WeatherApiClient.getWeatherApiService();
        Call<WeatherResponse> call = weatherService.getWeatherByCity(lugar, WeatherApiService.API_KEY, "metric", "es");
        
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("WeatherDetail", "Respuesta de API clima: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    Log.d("WeatherDetail", "Datos del clima obtenidos exitosamente para: " + weatherData.getName());
                    displayWeatherData(weatherData);
                } else {
                    Log.w("WeatherDetail", "Error al obtener clima para: " + lugar + " Code: " + response.code());
                    weatherDetailLayout.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherDetail", "Error de conexión API clima: " + t.getMessage());
                weatherDetailLayout.setVisibility(View.GONE);
            }
        });
    }
    
    private void displayWeatherData(WeatherResponse weatherData) {
        Log.d("WeatherDetail", "Mostrando datos del clima en detalle");
        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            WeatherResponse.Weather weather = weatherData.getWeather().get(0);
            WeatherResponse.Main main = weatherData.getMain();
            
            Log.d("WeatherDetail", "Datos: Temp=" + main.getTempFormatted() + ", Desc=" + weather.getDescription());
            
            // Cargar icono del clima
            String iconUrl = weather.getIconUrl();
            Glide.with(this)
                .load(iconUrl)
                .into(ivWeatherIconDetail);
            
            // Mostrar datos
            tvTempDetail.setText(main.getTempFormatted());
            tvWeatherDescDetail.setText(weather.getDescription());
            tvTempMinMax.setText(main.getTempMinFormatted() + "/" + main.getTempMaxFormatted());
            tvHumidity.setText(main.getHumidity() + "%");
            tvPressure.setText(main.getPressure() + " hPa");
            
            weatherDetailLayout.setVisibility(View.VISIBLE);
            Log.d("WeatherDetail", "Información meteorológica mostrada correctamente en detalle");
        } else {
            Log.w("WeatherDetail", "Datos del clima vacíos o nulos en detalle");
            weatherDetailLayout.setVisibility(View.GONE);
        }
    }
}