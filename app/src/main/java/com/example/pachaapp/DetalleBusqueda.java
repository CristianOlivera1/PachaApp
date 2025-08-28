package com.example.pachaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.pachaapp.clas.WeatherApiClient;
import com.example.pachaapp.clas.WeatherApiService;
import com.example.pachaapp.clas.WeatherResponse;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetalleBusqueda extends AppCompatActivity {
    private TextView tvCiudad, tvTemp, tvWeatherDesc, tvTempera;
    private ImageView ivIcon;
    private String idBusquedaReciente;
    private String userId;
    private String ciudad;
    private ProgressBar progressBarAction;
    private LinearLayout weatherDetailLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_busqueda);
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
        tvCiudad = findViewById(R.id.tvCiudad);

        // Elementos meteorológicos
        weatherDetailLayout = findViewById(R.id.weatherDetailLayout);
        ivIcon = findViewById(R.id.ivIcon);
        tvTemp = findViewById(R.id.tvTemp);
        tvWeatherDesc = findViewById(R.id.tvWeatherDesc);
        tvTempera = findViewById(R.id.tvTempera);

    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        idBusquedaReciente = intent.getStringExtra("idBusquedaReciente");
        ciudad = intent.getStringExtra("ciudad");
    }

    private void setupUI() {
        // Mostrar datos

        if (ciudad != null && !ciudad.isEmpty()) {
            tvCiudad.setText(ciudad);
        } else {
            tvCiudad.setText("No especificado");
        }
    }


    public void retroceder(View v) {
        Intent intent = new Intent(this, BuscarActivity.class);
        startActivity(intent);
    }


    private void loadWeatherData() {
        Log.d("WeatherDetail", "Cargando datos meteorológicos para: " + ciudad);
        if (ciudad == null || ciudad.trim().isEmpty()) {
            Log.d("WeatherDetail", "No hay lugar especificado, ocultando sección meteorológica");
            weatherDetailLayout.setVisibility(View.GONE);
            return;
        }

        Log.d("WeatherDetail", "Haciendo petición a API del clima para: " + ciudad);
        WeatherApiService weatherService = WeatherApiClient.getWeatherApiService();
        Call<WeatherResponse> call = weatherService.getWeatherByCity(ciudad, WeatherApiService.API_KEY, "metric", "es");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("WeatherDetail", "Respuesta de API clima: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    Log.d("WeatherDetail", "Datos del clima obtenidos exitosamente para: " + weatherData.getName());
                    displayWeatherData(weatherData);
                } else {
                    Log.w("WeatherDetail", "Error al obtener clima para: " + ciudad + " Code: " + response.code());
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


            String iconCode = weatherData.getWeather().get(0).getIcon();
            String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
            Picasso.get().load(iconUrl).into(ivIcon);

            // Mostrar datos
            tvTemp.setText(main.getTempFormatted());

            String descripcion = weatherData.getWeather().get(0).getDescription();

            String tem = "🌤️  Descripción: " + descripcion;
            tvWeatherDesc.setText(tem);

            double sensacion = weatherData.getMain().getFeelsLike();
            double tempMin = weatherData.getMain().getTempMin();
            double tempMax = weatherData.getMain().getTempMax();
            int humedad = weatherData.getMain().getHumidity();
            double viento = weatherData.getWind().getSpeed();

            String textoclima =
                    "🥵   Sensación: " + sensacion + "°C\n"
                            + "📉   Min: " + tempMin + " °C" + "\n"
                            + "📈   Máx: " + tempMax + " °C\n"
                            + "💧   Humedad: " + humedad + " %\n"
                            + "💨   Viento: " + viento + "  m/s";
            ;

            tvTempera.setText(textoclima);


            weatherDetailLayout.setVisibility(View.VISIBLE);
            Log.d("WeatherDetail", "Información meteorológica mostrada correctamente en detalle");
        } else {
            Log.w("WeatherDetail", "Datos del clima vacíos o nulos en detalle");
            weatherDetailLayout.setVisibility(View.GONE);
        }
    }


}