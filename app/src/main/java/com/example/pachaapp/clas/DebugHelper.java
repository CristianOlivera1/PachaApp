package com.example.pachaapp.clas;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DebugHelper {
    private static final String TAG = "DebugHelper";
    
    public static void testTimestampParsing() {
        // Crear el mismo Gson que usa la aplicación
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Long.class, new TimestampTypeAdapter())
                .registerTypeAdapter(long.class, new TimestampTypeAdapter())
                .create();
        
        // JSON de prueba similar al que devuelve el backend
        String testJson = "{"
            + "\"idActividad\":\"1\","
            + "\"idUsuario\":\"user123\","
            + "\"descripcion\":\"Actividad de prueba\","
            + "\"lugar\":\"Lima\","
            + "\"estado\":\"pendiente\","
            + "\"fechaActividad\":\"2025-07-29T01:10:15.721+00:00\","
            + "\"fechaRegistro\":\"2025-07-29T01:10:15.721+00:00\","
            + "\"fechaActualizacion\":\"2025-07-29T01:10:15.721+00:00\""
            + "}";
        
        try {
            Activity activity = gson.fromJson(testJson, Activity.class);
            Log.d(TAG, "Parsing exitoso!");
            Log.d(TAG, "ID: " + activity.getIdActividad());
            Log.d(TAG, "Descripción: " + activity.getDescripcion());
            Log.d(TAG, "Fecha Actividad: " + activity.getFechaActividad());
            Log.d(TAG, "Fecha Registro: " + activity.getFechaRegistro());
            Log.d(TAG, "Fecha Actualización: " + activity.getFechaActualizacion());
        } catch (Exception e) {
            Log.e(TAG, "Error en parsing: " + e.getMessage(), e);
        }
    }
}
