package com.example.pachaapp.clas;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampTypeAdapter extends TypeAdapter<Long> {
    
    @Override
    public void write(JsonWriter out, Long value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }
    
    @Override
    public Long read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        
        // Leer el valor como string para poder parsearlo
        if (in.peek() == JsonToken.NUMBER) {
            // Si es un número, devolverlo directamente
            return in.nextLong();
        }
        
        String value = in.nextString();
        
        try {
            if (value.matches("\\d+")) {
                return Long.parseLong(value);
            }
            
            return parseIsoDate(value);
            
        } catch (Exception e) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException ex) {
                // Si todo falla, retornar timestamp actual
                System.err.println("Error parseando timestamp: " + value + ". Usando timestamp actual.");
                return System.currentTimeMillis();
            }
        }
    }
    
    private Long parseIsoDate(String dateString) throws IOException {
        // Formatos de fecha comunes que puede enviar el backend
        SimpleDateFormat[] formats = {
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()), // ISO 8601 con timezone
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),  // ISO 8601 UTC
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),      // ISO 8601 UTC sin milisegundos
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()),     // ISO 8601 sin timezone
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),        // ISO 8601 sin milisegundos ni timezone
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())           // Formato simple
        };
        
        for (SimpleDateFormat format : formats) {
            format.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                Date date = format.parse(dateString);
                return date.getTime();
            } catch (ParseException ignored) {
            }
        }
        
        throw new IOException("No se pudo parsear la fecha: " + dateString);
    }
}
