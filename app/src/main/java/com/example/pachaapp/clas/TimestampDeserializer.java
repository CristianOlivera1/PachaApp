package com.example.pachaapp.clas;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampDeserializer implements JsonDeserializer<Long> {
    
    @Override
    public Long deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            String dateString = json.getAsString();
            
            if (dateString.matches("\\d+")) {
                return Long.parseLong(dateString);
            }
            
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            
            try {
                Date date = isoFormat.parse(dateString);
                return date.getTime();
            } catch (ParseException e) {
                // Intentar con otros formatos comunes
                SimpleDateFormat[] formats = {
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault()),
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                };
                
                for (SimpleDateFormat format : formats) {
                    format.setTimeZone(TimeZone.getTimeZone("UTC"));
                    try {
                        Date date = format.parse(dateString);
                        return date.getTime();
                    } catch (ParseException ignored) {
                    }
                }
                
                throw new JsonParseException("No se pudo parsear la fecha: " + dateString, e);
            }
            
        } catch (Exception e) {
            try {
                return json.getAsLong();
            } catch (Exception ex) {
                throw new JsonParseException("No se pudo convertir a timestamp: " + json.getAsString(), e);
            }
        }
    }
}
