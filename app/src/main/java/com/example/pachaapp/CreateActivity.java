package com.example.pachaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pachaapp.clas.Activity;
import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiResponse;
import com.example.pachaapp.clas.ApiService;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateActivity extends AppCompatActivity {

    private TextInputEditText etDescripcionCreate, etLugarCreate;
    private Button btnSeleccionarFecha, btnSeleccionarHora, btnCrear;
    private TextView tvFechaSeleccionada;
    private ProgressBar progressBarCreate;
    
    private Calendar fechaSeleccionada;
    private String userId;
    private SimpleDateFormat dateTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        getUserId();
        setupDatePicker();
    }

    private void initializeViews() {
        etDescripcionCreate = findViewById(R.id.etDescripcionCreate);
        etLugarCreate = findViewById(R.id.etLugarCreate);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnSeleccionarHora = findViewById(R.id.btnSeleccionarHora);
        btnCrear = findViewById(R.id.btnCrear);
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        progressBarCreate = findViewById(R.id.progressBarCreate);
        
        fechaSeleccionada = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    private void getUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getString("user_id", null);
        
        if (userId == null) {
            Toast.makeText(this, "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupDatePicker() {
        fechaSeleccionada.add(Calendar.HOUR_OF_DAY, 1); // Agregar al menos 1 hora
        updateDateTimeDisplay();
    }

    public void seleccionarFecha(View v) {
        Calendar now = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada.set(Calendar.YEAR, year);
                    fechaSeleccionada.set(Calendar.MONTH, month);
                    fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateDateTimeDisplay();
                },
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
        );
        
        // Establecer fecha mínima como hoy
        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    public void seleccionarHora(View v) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    fechaSeleccionada.set(Calendar.MINUTE, minute);
                    fechaSeleccionada.set(Calendar.SECOND, 0);
                    fechaSeleccionada.set(Calendar.MILLISECOND, 0);
                    updateDateTimeDisplay();
                },
                fechaSeleccionada.get(Calendar.HOUR_OF_DAY),
                fechaSeleccionada.get(Calendar.MINUTE),
                true // Formato 24 horas
        );
        
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        String fechaTexto = dateTimeFormat.format(fechaSeleccionada.getTime());
        tvFechaSeleccionada.setText(fechaTexto);
    }

    public void crearActividad(View v) {
        String descripcion = etDescripcionCreate.getText().toString().trim();
        String lugar = etLugarCreate.getText().toString().trim();

        // Validaciones
        if (descripcion.isEmpty()) {
            etDescripcionCreate.setError("La descripción es requerida");
            etDescripcionCreate.requestFocus();
            return;
        }

        // Validar que la fecha no sea en el pasado
        Calendar now = Calendar.getInstance();
        if (fechaSeleccionada.before(now)) {
            Toast.makeText(this, "La fecha de actividad no puede ser en el pasado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lugar.isEmpty()) {
            lugar = null;
        }

        crearActividadEnServidor(descripcion, lugar, fechaSeleccionada.getTimeInMillis());
    }

    private void crearActividadEnServidor(String descripcion, String lugar, long fechaActividad) {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Activity>> call = apiService.createActivity(userId, descripcion, lugar, fechaActividad);

        call.enqueue(new Callback<ApiResponse<Activity>>() {
            @Override
            public void onResponse(Call<ApiResponse<Activity>> call, Response<ApiResponse<Activity>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Activity> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(CreateActivity.this, "Actividad creada correctamente", Toast.LENGTH_SHORT).show();
                        
                        // Regresar a la lista de actividades
                        Intent intent = new Intent(CreateActivity.this, listActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(CreateActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CreateActivity.this, "Error al crear la actividad", Toast.LENGTH_SHORT).show();
                    Log.e("CreateActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Activity>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(CreateActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("CreateActivity", "Error de conexión: ", t);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBarCreate.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCrear.setEnabled(!show);
        btnSeleccionarFecha.setEnabled(!show);
        btnSeleccionarHora.setEnabled(!show);
        etDescripcionCreate.setEnabled(!show);
        etLugarCreate.setEnabled(!show);
    }

    public void retroceder(View v) {
        Intent intent = new Intent(this, listActivity.class);
        startActivity(intent);
    }
}