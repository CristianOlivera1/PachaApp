package com.example.pachaapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import com.example.pachaapp.clas.Activity;
import com.example.pachaapp.clas.ApiClient;
import com.example.pachaapp.clas.ApiResponse;
import com.example.pachaapp.clas.ApiService;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class editActivity extends AppCompatActivity {

    private TextInputEditText etDescripcionEdit, etLugarEdit;
    private TextView tvEstadoActual, tvFechaSeleccionadaEdit;
    private SwitchMaterial switchEstado;
    private LinearLayout layoutCambiarEstado;
    private Button btnSeleccionarFechaEdit, btnSeleccionarHoraEdit, btnGuardarCambios;
    private ProgressBar progressBarEdit;
    
    private String idActividad;
    private String descripcionOriginal;
    private String lugarOriginal;
    private String estadoOriginal;
    private long fechaActividadOriginal;
    private String userId;
    
    private Calendar fechaSeleccionada;
    private SimpleDateFormat dateTimeFormat;
    private boolean hasChanges = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        getUserId();
        getIntentData();
        setupUI();
        setupListeners();
    }

    private void initializeViews() {
        etDescripcionEdit = findViewById(R.id.etDescripcionEdit);
        etLugarEdit = findViewById(R.id.etLugarEdit);
        tvEstadoActual = findViewById(R.id.tvEstadoActual);
        tvFechaSeleccionadaEdit = findViewById(R.id.tvFechaSeleccionadaEdit);
        switchEstado = findViewById(R.id.switchEstado);
        layoutCambiarEstado = findViewById(R.id.layoutCambiarEstado);
        btnSeleccionarFechaEdit = findViewById(R.id.btnSeleccionarFechaEdit);
        btnSeleccionarHoraEdit = findViewById(R.id.btnSeleccionarHoraEdit);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);
        progressBarEdit = findViewById(R.id.progressBarEdit);
        
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

    private void getIntentData() {
        Intent intent = getIntent();
        idActividad = intent.getStringExtra("idActividad");
        descripcionOriginal = intent.getStringExtra("descripcion");
        lugarOriginal = intent.getStringExtra("lugar");
        estadoOriginal = intent.getStringExtra("estado");
        fechaActividadOriginal = intent.getLongExtra("fechaActividad", 0);
    }

    private void setupUI() {
        // Mostrar datos actuales
        etDescripcionEdit.setText(descripcionOriginal);
        
        if (lugarOriginal != null && !lugarOriginal.isEmpty()) {
            etLugarEdit.setText(lugarOriginal);
        }
        
        tvEstadoActual.setText(estadoOriginal.toUpperCase());
        
        // Configurar fecha
        fechaSeleccionada.setTimeInMillis(fechaActividadOriginal);
        updateDateTimeDisplay();
        
        // Configurar colores y visibilidad según el estado
        if ("concluido".equals(estadoOriginal)) {
            tvEstadoActual.setTextColor(ContextCompat.getColor(this, R.color.green));
            layoutCambiarEstado.setVisibility(View.GONE);
            
            // Si está concluido, no permitir cambiar la fecha hacia atrás
            btnSeleccionarFechaEdit.setText("Ver Fecha");
            btnSeleccionarHoraEdit.setText("Ver Hora");
            btnSeleccionarFechaEdit.setEnabled(false);
            btnSeleccionarHoraEdit.setEnabled(false);
            
        } else {
            tvEstadoActual.setTextColor(ContextCompat.getColor(this, R.color.cyan));
            layoutCambiarEstado.setVisibility(View.VISIBLE);
        }
    }

    private void setupListeners() {
        // Listener para detectar cambios en los campos de texto
        etDescripcionEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanges = true;
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        etLugarEdit.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                hasChanges = true;
                updateSaveButtonState();
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        // Listener para el switch de estado
        switchEstado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            hasChanges = true;
            updateSaveButtonState();
        });
    }

    private void updateSaveButtonState() {
        // Habilitar el botón de guardar solo si hay cambios
        btnGuardarCambios.setEnabled(hasChanges);
        btnGuardarCambios.setAlpha(hasChanges ? 1.0f : 0.6f);
    }

    public void seleccionarFecha(View v) {
        if ("concluido".equals(estadoOriginal)) {
            return; // No permitir cambios si está concluido
        }

        Calendar now = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada.set(Calendar.YEAR, year);
                    fechaSeleccionada.set(Calendar.MONTH, month);
                    fechaSeleccionada.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    hasChanges = true;
                    updateDateTimeDisplay();
                    updateSaveButtonState();
                },
                fechaSeleccionada.get(Calendar.YEAR),
                fechaSeleccionada.get(Calendar.MONTH),
                fechaSeleccionada.get(Calendar.DAY_OF_MONTH)
        );
        
        // Para actividades iniciadas, permitir fechas pasadas (en caso de reprogramación)
        // Pero mostrar advertencia si es necesario
        datePickerDialog.show();
    }

    public void seleccionarHora(View v) {
        if ("concluido".equals(estadoOriginal)) {
            return; // No permitir cambios si está concluido
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    fechaSeleccionada.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    fechaSeleccionada.set(Calendar.MINUTE, minute);
                    fechaSeleccionada.set(Calendar.SECOND, 0);
                    fechaSeleccionada.set(Calendar.MILLISECOND, 0);
                    hasChanges = true;
                    updateDateTimeDisplay();
                    updateSaveButtonState();
                },
                fechaSeleccionada.get(Calendar.HOUR_OF_DAY),
                fechaSeleccionada.get(Calendar.MINUTE),
                true // Formato 24 horas
        );
        
        timePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        String fechaTexto = dateTimeFormat.format(fechaSeleccionada.getTime());
        tvFechaSeleccionadaEdit.setText(fechaTexto);
    }

    public void guardarCambios(View v) {
        String nuevaDescripcion = etDescripcionEdit.getText().toString().trim();
        String nuevoLugar = etLugarEdit.getText().toString().trim();

        // Validaciones
        if (nuevaDescripcion.isEmpty()) {
            etDescripcionEdit.setError("La descripción es requerida");
            etDescripcionEdit.requestFocus();
            return;
        }

        // Determinar el nuevo estado
        String nuevoEstado = estadoOriginal;
        if (switchEstado.isChecked() && "iniciado".equals(estadoOriginal)) {
            nuevoEstado = "concluido";
        }

        // Si el lugar está vacío, enviarlo como null
        if (nuevoLugar.isEmpty()) {
            nuevoLugar = null;
        }

        // Validar si hay cambios reales
        boolean hayFechaChange = fechaSeleccionada.getTimeInMillis() != fechaActividadOriginal;
        boolean hayDescripcionChange = !nuevaDescripcion.equals(descripcionOriginal);
        boolean hayLugarChange = !java.util.Objects.equals(nuevoLugar, lugarOriginal);
        boolean hayEstadoChange = !nuevoEstado.equals(estadoOriginal);

        if (!hayFechaChange && !hayDescripcionChange && !hayLugarChange && !hayEstadoChange) {
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        actualizarActividadEnServidor(nuevaDescripcion, nuevoLugar, nuevoEstado, fechaSeleccionada.getTimeInMillis());
    }

    private void actualizarActividadEnServidor(String descripcion, String lugar, String estado, long fechaActividad) {
        showLoading(true);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<ApiResponse<Activity>> call = apiService.updateActivity(
            idActividad, userId, descripcion, lugar, estado, fechaActividad
        );

        call.enqueue(new Callback<ApiResponse<Activity>>() {
            @Override
            public void onResponse(Call<ApiResponse<Activity>> call, Response<ApiResponse<Activity>> response) {
                showLoading(false);

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Activity> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(editActivity.this, "Actividad actualizada correctamente", Toast.LENGTH_SHORT).show();
                        
                        // Regresar al detalle con los nuevos datos
                        Intent intent = new Intent(editActivity.this, detailActivity.class);
                        Activity updatedActivity = apiResponse.getData();
                        intent.putExtra("idActividad", updatedActivity.getIdActividad());
                        intent.putExtra("descripcion", updatedActivity.getDescripcion());
                        intent.putExtra("lugar", updatedActivity.getLugar());
                        intent.putExtra("estado", updatedActivity.getEstado());
                        intent.putExtra("fechaActividad", updatedActivity.getFechaActividad());
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        
                    } else {
                        String errorMessage = apiResponse.getFirstMessage();
                        Toast.makeText(editActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(editActivity.this, "Error al actualizar la actividad", Toast.LENGTH_SHORT).show();
                    Log.e("EditActivity", "Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Activity>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(editActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                Log.e("EditActivity", "Error de conexión: ", t);
            }
        });
    }

    private void showLoading(boolean show) {
        progressBarEdit.setVisibility(show ? View.VISIBLE : View.GONE);
        btnGuardarCambios.setEnabled(!show);
        btnSeleccionarFechaEdit.setEnabled(!show && !"concluido".equals(estadoOriginal));
        btnSeleccionarHoraEdit.setEnabled(!show && !"concluido".equals(estadoOriginal));
        etDescripcionEdit.setEnabled(!show);
        etLugarEdit.setEnabled(!show);
        switchEstado.setEnabled(!show);
    }

    @Override
    public void onBackPressed() {
        if (hasChanges) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cambios sin guardar")
                    .setMessage("Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?")
                    .setPositiveButton("Salir", (dialog, which) -> {
                        super.onBackPressed();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    public void retroceder(View v) {
        if (hasChanges) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Cambios sin guardar")
                    .setMessage("Tienes cambios sin guardar. ¿Estás seguro de que quieres salir?")
                    .setPositiveButton("Salir", (dialog, which) -> {
                        Intent intent = new Intent(this, detailActivity.class);
                        intent.putExtra("idActividad", idActividad);
                        intent.putExtra("descripcion", descripcionOriginal);
                        intent.putExtra("lugar", lugarOriginal);
                        intent.putExtra("estado", estadoOriginal);
                        intent.putExtra("fechaActividad", fechaActividadOriginal);
                        startActivity(intent);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            Intent intent = new Intent(this, detailActivity.class);
            intent.putExtra("idActividad", idActividad);
            intent.putExtra("descripcion", descripcionOriginal);
            intent.putExtra("lugar", lugarOriginal);
            intent.putExtra("estado", estadoOriginal);
            intent.putExtra("fechaActividad", fechaActividadOriginal);
            startActivity(intent);
        }
    }
}