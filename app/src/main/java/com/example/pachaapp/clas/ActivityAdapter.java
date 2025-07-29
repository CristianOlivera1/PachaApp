package com.example.pachaapp.clas;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pachaapp.R;
import com.example.pachaapp.detailActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        } else {
            holder.tvLugar.setVisibility(View.GONE);
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

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvDescripcion;
        TextView tvLugar;
        TextView tvFecha;
        TextView tvEstado;

        public ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardActivity);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvLugar = itemView.findViewById(R.id.tvLugar);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvEstado = itemView.findViewById(R.id.tvEstado);
        }
    }
}
