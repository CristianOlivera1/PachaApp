package com.example.pachaapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiResponse;
import com.example.pachaapp.clas.ApiService;
import com.example.pachaapp.clas.Busqueda;
import com.example.pachaapp.clas.ForecastResponse;
import com.example.pachaapp.clas.WeatherApiService;
import com.example.pachaapp.clas.WeatherResponse;
import com.example.pachaapp.clas.FCMTokenManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewClima;
    private ImageView imageViewClima;
    private WeatherApiService weatherAPI;
    private final String apiKey = "8ae91641bc3d122541f4c2bbb2195f18";

    private TextView textSensacion, textHumedad, textViento, textNubes;

    private FusedLocationProviderClient fusedLocationClient;
    private Geocoder geocoder;

    private Spinner spinnerCiudades;

    private String userId;
    private ApiService apiService;

    private String ciudad = null;
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


        textViewClima = findViewById(R.id.textViewClima);
        imageViewClima = findViewById(R.id.imageViewClima);

        textSensacion = findViewById(R.id.textSensacion);
        textHumedad = findViewById(R.id.textHumedad);
        textViento = findViewById(R.id.textViento);
        textNubes = findViewById(R.id.textNubes);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());

        solicitarPermisosUbicacion();
        
        // Solicitar permisos de notificación
        solicitarPermisosNotificacion();

        // Inicializar FCM si el usuario está logueado
        if (isUserLoggedIn()) {
            FCMTokenManager.initializeForUser(this);
        }

        //Configurar Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherAPI = retrofit.create(WeatherApiService.class);

        spinnerCiudades = findViewById(R.id.spinnerCiudades);

        // Inicializar Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        getUserId();
        
        // Inicializar spinner con ciudad actual detectada por GPS
        // Las búsquedas del historial se cargarán después si hay usuario logueado
        inicializarSpinner();
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
                    int nubes = weatherData.getClouds().getAll();
                    String descripcion = weatherData.getWeather().get(0).getDescription();
                    String iconCode = weatherData.getWeather().get(0).getIcon();

                    String textoClima =temperatura + "°C\n"
                            + descripcion + "\n"
                            + "📉 Min: " + tempMin + "°C\n"
                            + "📈 Máx: " + tempMax + "°C\n";

                    textSensacion.setText("🥵 Sensación térmica \n " + sensacion + "°C");
                    textHumedad.setText("💧 Humedad \n " + humedad + "%");
                    textViento.setText("💨 Viento \n" + viento + " m/s");
                    textNubes.setText("☁️ Nubes \n" + nubes + "%");

                    //0TextView ciudadHeader = findViewById(R.id.textCiudadHeader);
                    //ciudadHeader.setText(ciudad); // Actualiza el nombre en la barra

                    textViewClima.setText(textoClima);

                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
                    Picasso.get().load(iconUrl).into(imageViewClima);

                    obtenerPronostico(ciudad);


                } else {
                    textViewClima.setText("❌ No se pudo obtener el clima.");
                    Log.e("CLIMA","Error HTTP: "+ response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewClima.setText("❌ Error de conexión.");
                Log.e("CLIMA","Fallo en conexión: ", t);

            }
        });
    }

    public void Busca (View v) {
        Intent intent = new Intent(MainActivity.this, BuscarActivity.class);
        startActivity(intent);
    }

    private void solicitarPermisosUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
        } else {
            obtenerCiudadActual();
        }
    }

    private void obtenerCiudadActual() {
        // Verificar permisos antes de proceder
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("UBICACION", "Sin permisos de ubicación");
            ciudad = "Lima";
            actualizarSpinnerConCiudadActual();
            obtenerClima(ciudad);
            return;
        }

        // Agregar timeout para la detección de ubicación
        Handler timeoutHandler = new Handler();
        Runnable timeoutRunnable = () -> {
            Log.w("UBICACION", "Timeout en detección de ubicación, usando Lima por defecto");
            if (ciudad == null) { // Solo si no se ha detectado ya una ciudad
                ciudad = "Lima";
                actualizarSpinnerConCiudadActual();
                obtenerClima(ciudad);
            }
        };
        
        // Ejecutar timeout después de 5 segundos
        timeoutHandler.postDelayed(timeoutRunnable, 5000);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            // Cancelar timeout ya que obtuvimos respuesta
            timeoutHandler.removeCallbacks(timeoutRunnable);
            
            if (location != null) {
                try {
                    List<Address> direcciones = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (!direcciones.isEmpty()) {
                        ciudad = direcciones.get(0).getLocality();
                        if (ciudad == null) ciudad = direcciones.get(0).getSubAdminArea(); // Fallback
                        Log.d("UBICACION", "Ciudad detectada: " + ciudad);
                        
                        // Actualizar spinner con la ciudad detectada
                        actualizarSpinnerConCiudadActual();
                        
                        // Obtener clima de la ciudad actual
                        obtenerClima(ciudad);
                    } else {
                        Log.e("UBICACION", "No se encontraron direcciones");
                        ciudad = "Lima";
                        actualizarSpinnerConCiudadActual();
                        obtenerClima(ciudad);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("UBICACION", "Error en geocodificación: " + e.getMessage());
                    // Si falla la geolocalización, usar una ciudad por defecto
                    ciudad = "Lima";
                    actualizarSpinnerConCiudadActual();
                    obtenerClima(ciudad);
                }
            } else {
                Log.e("UBICACION", "Ubicación es nula");
                // Si no se puede obtener ubicación, usar ciudad por defecto
                ciudad = "Lima";
                actualizarSpinnerConCiudadActual();
                obtenerClima(ciudad);
            }
        }).addOnFailureListener(e -> {
            // Cancelar timeout ya que obtuvimos respuesta (aunque sea de error)
            timeoutHandler.removeCallbacks(timeoutRunnable);
            
            Log.e("UBICACION", "Error al obtener ubicación: " + e.getMessage());
            // En caso de error, usar ciudad por defecto
            ciudad = "Lima";
            actualizarSpinnerConCiudadActual();
            obtenerClima(ciudad);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            obtenerCiudadActual();
        } else if (requestCode == 1001) {
            // Si se deniega el permiso de ubicación, usar ciudad por defecto
            textViewClima.setText("❌ Permiso de ubicación denegado. Mostrando clima de Lima.");
            ciudad = "Lima";
            actualizarSpinnerConCiudadActual();
            obtenerClima(ciudad);
            actualizarSpinnerConCiudadActual();
            obtenerClima(ciudad);
        } else if (requestCode == 1002) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISOS", "Permiso de notificación concedido");
            } else {
                Log.d("PERMISOS", "Permiso de notificación denegado");
            }
        }
    }

    private void solicitarPermisosNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1002);
            }
        }
    }

    private void obtenerPronostico(String ciudad) {
        Call<ForecastResponse> call = weatherAPI.getForecast(ciudad, apiKey, "metric", "es");
        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ForecastResponse.ForecastItem> lista = response.body().getList();

                    // Horas: primeras 8 (24 horas aprox)
                    List<ForecastResponse.ForecastItem> horas = lista.subList(0, Math.min(8, lista.size()));

                    // Días: uno cada 8 elementos (~1 por día)
                    List<ForecastResponse.ForecastItem> dias = new ArrayList<>();
                    for (int i = 0; i < lista.size(); i += 8) {
                        dias.add(lista.get(i));
                    }

                    mostrarPronostico(horas, dias);
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Log.e("CLIMA", "Error obteniendo pronóstico", t);
            }
        });
    }

    private void mostrarPronostico(List<ForecastResponse.ForecastItem> horas,
                                   List<ForecastResponse.ForecastItem> dias) {
        LinearLayout contHoras = findViewById(R.id.listaHoras);
        LinearLayout contDias = findViewById(R.id.listaDias);

        contHoras.removeAllViews();
        contDias.removeAllViews();


        // Lista por horas con icono
        for (ForecastResponse.ForecastItem h : horas) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 10, 0, 10);

            // Icono del clima
            ImageView icono = new ImageView(this);
            String iconUrl = "https://openweathermap.org/img/wn/" +
                    h.getWeather().get(0).getIcon() + "@2x.png";
            Picasso.get().load(iconUrl).resize(100, 100).into(icono);

            // Texto hora y datos
            String horaTxt = h.getDateTime().substring(11, 16);
            String texto = horaTxt + " - " + Math.round(h.getMain().getTemp()) + "°C - 💨 " +
                    Math.round(h.getWind().getSpeed()) + " m/s";
            TextView tv = new TextView(this);
            tv.setText(texto);
            tv.setTextColor(Color.WHITE);
            tv.setPadding(20, 0, 0, 0);

            fila.addView(icono);
            fila.addView(tv);
            contHoras.addView(fila);
        }

        // Lista por días con icono y nombre del día
        for (ForecastResponse.ForecastItem d : dias) {
            LinearLayout fila = new LinearLayout(this);
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setPadding(0, 10, 0, 10);

            // Icono
            ImageView icono = new ImageView(this);
            String iconUrl = "https://openweathermap.org/img/wn/" +
                    d.getWeather().get(0).getIcon() + "@2x.png";
            Picasso.get().load(iconUrl).resize(100, 100).into(icono);

            // Fecha - nombre de día
            String fechaTxt = obtenerNombreDia(d.getDateTime());
            String texto = fechaTxt + " - Máx: " + Math.round(d.getMain().getTempMax()) +
                    "° ➖ Mín: " + Math.round(d.getMain().getTempMin()) + "°";
            TextView tv = new TextView(this);
            tv.setText(texto);
            tv.setTextColor(Color.WHITE);
            tv.setPadding(20, 0, 0, 0);

            fila.addView(icono);
            fila.addView(tv);
            contDias.addView(fila);
        }
    }


    private String obtenerNombreDia(String fechaHora) {
        try {
            java.text.SimpleDateFormat formatoEntrada = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            java.util.Date fecha = formatoEntrada.parse(fechaHora);

            java.text.SimpleDateFormat formatoDia = new java.text.SimpleDateFormat("EEEE", new Locale("es", "ES"));
            return formatoDia.format(fecha);
        } catch (Exception e) {
            e.printStackTrace();
            return fechaHora;
        }
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);

        /*if (userId == null) {
            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }*/
    }

    private void inicializarSpinner() {
        // Configurar spinner básico mientras se detecta la ubicación
        List<String> ciudadesIniciales = new ArrayList<>();
        ciudadesIniciales.add("Detectando ubicación...");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                ciudadesIniciales);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        
        spinnerCiudades.setAdapter(adapter);
    }

    private void actualizarSpinnerConCiudadActual() {
        Log.d("SPINNER", "Actualizando spinner con ciudad: " + ciudad);
        List<String> ciudades = new ArrayList<>();
        
        // Agregar la ciudad actual detectada por GPS
        if (ciudad != null && !ciudad.isEmpty()) {
            ciudades.add(ciudad + " (Actual)");
            Log.d("SPINNER", "Ciudad agregada: " + ciudad + " (Actual)");
        } else {
            ciudades.add("Lima (Por defecto)");
            Log.d("SPINNER", "Ciudad por defecto agregada");
        }
        
        // Si hay usuario logueado, cargar también su historial
        if (isUserLoggedIn() && userId != null) {
            loadCiudadesDelUsuario(ciudades);
        } else {
            // Si no hay usuario logueado, solo mostrar ciudad actual
            configurarSpinnerFinal(ciudades);
        }
    }

    private void configurarSpinnerFinal(List<String> ciudades) {
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                ciudades);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        
        runOnUiThread(() -> {
            spinnerCiudades.setAdapter(adapter);
            
            // Seleccionar la ciudad actual por defecto
            spinnerCiudades.setSelection(0);

        });
        
        // Listener para selección
        spinnerCiudades.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ciudadSeleccionada = ciudades.get(position);
                
                // Extraer el nombre de la ciudad (remover " (Actual)" si existe)
                String nombreCiudad = ciudadSeleccionada.replace(" (Actual)", "").replace(" (Por defecto)", "");
                
                Log.d("SPINNER", "Ciudad seleccionada: " + nombreCiudad);
                obtenerClima(nombreCiudad);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
    private void loadCiudadesDelUsuario(List<String> ciudadesExistentes) {
        if (userId == null) {
            configurarSpinnerFinal(ciudadesExistentes);
            return;
        }

        Call<ApiResponse<List<Busqueda>>> call = apiService.getBusquedaRecienteByUser(userId);

        call.enqueue(new Callback<ApiResponse<List<Busqueda>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Busqueda>>> call, Response<ApiResponse<List<Busqueda>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Busqueda>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        List<Busqueda> busquedas = apiResponse.getData();

                        if (busquedas != null && !busquedas.isEmpty()) {
                            // Agregar ciudades del historial (evitando duplicados)
                            for (Busqueda b : busquedas) {
                                String nombreCiudad = b.getCiudad();
                                // Verificar que no sea la misma ciudad actual
                                if (ciudad == null || !nombreCiudad.equalsIgnoreCase(ciudad)) {
                                    ciudadesExistentes.add(nombreCiudad);
                                }
                            }
                        }
                    }
                }
                
                // Configurar spinner con todas las ciudades (actual + historial)
                configurarSpinnerFinal(ciudadesExistentes);
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Busqueda>>> call, Throwable t) {
                Log.e("CIUDADES", "Error al cargar historial de ciudades: " + t.getMessage());
                // Si falla, usar solo la ciudad actual
                configurarSpinnerFinal(ciudadesExistentes);
            }
        });
    }
}