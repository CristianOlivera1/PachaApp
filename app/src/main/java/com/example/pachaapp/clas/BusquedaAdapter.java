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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pachaapp.DetalleBusqueda;
import com.example.pachaapp.R;
import com.example.pachaapp.detailActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BusquedaAdapter extends RecyclerView.Adapter<BusquedaAdapter.BusquedaViewHolder> {

    private List<Busqueda> listaBusquedas;

    private Context context;
    public BusquedaAdapter(Context context, List<Busqueda> listaBusquedas) {
        this.context = context;
        this.listaBusquedas = listaBusquedas;
    }

    @NonNull
    @Override
    public BusquedaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_busqueda, parent, false);
        return new BusquedaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusquedaViewHolder holder, int position) {
        Busqueda busqueda = listaBusquedas.get(position);

        holder.tvCiudad.setText(busqueda.getCiudad());

        if (busqueda.getCiudad() != null && !busqueda.getCiudad().isEmpty()) {
            holder.tvCiudad.setText(busqueda.getCiudad());
            holder.tvCiudad.setVisibility(View.VISIBLE);

            // Cargar datos meteorológicos
            loadWeatherData(busqueda, holder);
        } else {
            holder.tvCiudad.setVisibility(View.GONE);
            holder.weatherLayout.setVisibility(View.GONE);
        }

        // Click listener para ir al detalle
        // Click listener para ir al detalle
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleBusqueda.class);
            intent.putExtra("idBusquedaReciente", busqueda.getIdBusquedaReciente());
            intent.putExtra("ciudad", busqueda.getCiudad());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return listaBusquedas.size();
    }

    public void updateBusqueda(List<Busqueda> newlistaBusquedas) {
        this.listaBusquedas = newlistaBusquedas;
        notifyDataSetChanged();
    }

    private void loadWeatherData(Busqueda busqueda, BusquedaViewHolder holder) {
        Log.d("WeatherAdapter", "Intentando cargar clima para: " + busqueda.getCiudad());

        // Hacer petición a la API del clima
        Log.d("WeatherAdapter", "Haciendo petición a API del clima");
        WeatherApiService weatherService = WeatherApiClient.getWeatherApiService();
        Call<WeatherResponse> call = weatherService.getWeatherByCity(
                busqueda.getCiudad(),
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
                    Log.w("WeatherAdapter", "Error al obtener clima para: " + busqueda.getCiudad() + " Code: " + response.code());
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

    private void displayWeatherData(WeatherResponse weatherData, BusquedaAdapter.BusquedaViewHolder holder) {
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

    static class BusquedaViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvCiudad;
        LinearLayout weatherLayout;
        ImageView ivWeatherIcon;
        TextView tvTemperature;

        public BusquedaViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardActivity);
            tvCiudad = itemView.findViewById(R.id.tvCiudad);
            weatherLayout = itemView.findViewById(R.id.weatherLayout);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
        }
    }
}
