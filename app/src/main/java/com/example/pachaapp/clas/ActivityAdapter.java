package com.example.pachaapp.clas;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pachaapp.R;
import com.example.pachaapp.detailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {
    private List<Activity> activities;
    private Context context;
    private SimpleDateFormat dateFormat;

    public ActivityAdapter(Context context, List<Activity> activities) {
        this.context = context;
        this.activities = activities;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        Activity activity = activities.get(position);
        
        holder.tvDescripcion.setText(activity.getDescripcion());
        holder.tvFecha.setText(dateFormat.format(new Date(activity.getFechaActividad())));
        holder.tvEstado.setText(activity.getEstado().toUpperCase());
        
        if (activity.getLugar() != null && !activity.getLugar().isEmpty()) {
            holder.tvLugar.setText(activity.getLugar());
            holder.tvLugar.setVisibility(View.VISIBLE);
            
            // Cargar datos meteorológicos
            loadWeatherData(activity, holder);
        } else {
            holder.tvLugar.setVisibility(View.GONE);
            holder.weatherLayout.setVisibility(View.GONE);
        }

        // Cambiar color según el estado
        if (activity.isCompleted()) {
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_card_completed));
        } else {
            holder.tvEstado.setTextColor(ContextCompat.getColor(context, R.color.cyan));
            holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_card));
        }

        // Click listener para ir al detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, detailActivity.class);
            intent.putExtra("idActividad", activity.getIdActividad());
            intent.putExtra("descripcion", activity.getDescripcion());
            intent.putExtra("lugar", activity.getLugar());
            intent.putExtra("estado", activity.getEstado());
            intent.putExtra("fechaActividad", activity.getFechaActividad());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return activities.size();
    }

    public void updateActivities(List<Activity> newActivities) {
        this.activities = newActivities;
        notifyDataSetChanged();
    }
    
    private void loadWeatherData(Activity activity, ActivityViewHolder holder) {
        Log.d("WeatherAdapter", "Intentando cargar clima para: " + activity.getLugar());
        
        // Hacer petición a la API del clima
        Log.d("WeatherAdapter", "Haciendo petición a API del clima");
        WeatherApiService weatherService = WeatherApiClient.getWeatherApiService();
        Call<WeatherResponse> call = weatherService.getWeatherByCity(
            activity.getLugar(), 
            WeatherApiService.API_KEY, 
            "metric", 
            "es"
        );
        
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("WeatherAdapter", "Respuesta de API clima: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherData = response.body();
                    Log.d("WeatherAdapter", "Datos del clima obtenidos exitosamente para: " + weatherData.getName());
                    displayWeatherData(weatherData, holder);
                } else {
                    Log.w("WeatherAdapter", "Error al obtener clima para: " + activity.getLugar() + " Code: " + response.code());
                    holder.weatherLayout.setVisibility(View.GONE);
                }
            }
            
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("WeatherAdapter", "Error de conexión API clima: " + t.getMessage());
                holder.weatherLayout.setVisibility(View.GONE);
            }
        });
    }
    
    private void displayWeatherData(WeatherResponse weatherData, ActivityViewHolder holder) {
        Log.d("WeatherAdapter", "Mostrando datos del clima en UI");
        if (weatherData.getWeather() != null && !weatherData.getWeather().isEmpty()) {
            WeatherResponse.Weather weather = weatherData.getWeather().get(0);
            WeatherResponse.Main main = weatherData.getMain();
            
            Log.d("WeatherAdapter", "Temperatura: " + main.getTempFormatted() + ", Icono: " + weather.getIcon());
            
            // Cargar icono del clima
            String iconUrl = weather.getIconUrl();
            Glide.with(context)
                .load(iconUrl)
                .into(holder.ivWeatherIcon);
            
            // Mostrar temperatura
            holder.tvTemperature.setText(main.getTempFormatted());
            holder.weatherLayout.setVisibility(View.VISIBLE);
            Log.d("WeatherAdapter", "Información meteorológica mostrada correctamente");
        } else {
            Log.w("WeatherAdapter", "Datos del clima vacíos o nulos");
            holder.weatherLayout.setVisibility(View.GONE);
        }
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDescripcion;
        TextView tvLugar;
        TextView tvFecha;
        TextView tvEstado;
        LinearLayout weatherLayout;
        ImageView ivWeatherIcon;
        TextView tvTemperature;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardActivity);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            weatherLayout = itemView.findViewById(R.id.weatherLayout);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
        }
    }
}
