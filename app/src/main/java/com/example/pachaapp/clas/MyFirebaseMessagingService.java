package com.example.pachaapp.clas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.pachaapp.R;
import com.example.pachaapp.MainActivity;
import com.example.pachaapp.detailActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    
    private static final String TAG = "FCMService";
    private static final String CHANNEL_ID = "pacha_activities";
    private static final String CHANNEL_NAME = "Recordatorios de Actividades";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones para recordar tus actividades próximas";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "Mensaje recibido de: " + remoteMessage.getFrom());

        // Verificar si el mensaje contiene datos
        if (!remoteMessage.getData().isEmpty()) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
            
            // Procesar mensaje basado en los datos
            String type = remoteMessage.getData().get("type");
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            
            if (title != null && body != null) {
                // Usar título y cuerpo de los datos
                showNotification(title, body, remoteMessage.getData());
            } else {
                // Fallback al método anterior
                handleDataMessage(remoteMessage);
            }
        }

        // Solo procesar notification payload si no hay datos (para compatibilidad)
        else if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Cuerpo de la notificación: " + remoteMessage.getNotification().getBody());
            showNotification(
                remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody(),
                remoteMessage.getData()
            );
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Token FCM actualizado: " + token);
        
        // Guardar el token localmente
        FCMTokenManager.saveToken(this, token);
        
        // Enviar el token al servidor
        FCMTokenManager.sendTokenToServer(this, token);
    }

    private void handleDataMessage(RemoteMessage remoteMessage) {
        String type = remoteMessage.getData().get("type");
        
        if ("activity_reminder".equals(type)) {
            showActivityReminderNotification(remoteMessage);
        } else if ("activity_created".equals(type)) {
            showActivityCreatedNotification(remoteMessage);
        } else {
            // Fallback para tipos desconocidos
            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            if (title == null) title = "PachaApp";
            if (body == null) body = "Nueva notificación";
            showNotification(title, body, remoteMessage.getData());
        }
    }

    private void showActivityCreatedNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String userId = remoteMessage.getData().get("userId");
        
        if (title == null) title = "Nueva actividad creada";
        if (body == null) {
            String description = remoteMessage.getData().get("activityDescription");
            String dateString = remoteMessage.getData().get("activityDate");
            body = "Actividad: " + (description != null ? description : "Sin descripción");
            
            if (dateString != null) {
                try {
                    long timestamp = Long.parseLong(dateString);
                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                    String formattedDate = sdf.format(new java.util.Date(timestamp));
                    body += "\nFecha: " + formattedDate;
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing date: " + dateString);
                }
            }
        }
        
        showNotification(title, body, remoteMessage.getData());
    }

    private void showActivityReminderNotification(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
        String activityId = remoteMessage.getData().get("activityId");
        
        if (title == null) title = "Recordatorio de Actividad";
        if (body == null) {
            String description = remoteMessage.getData().get("description");
            body = "Tienes una actividad próxima" + (description != null ? ": " + description : "");
        }
        
        showNotification(title, body, remoteMessage.getData());
    }

    private void showNotification(String title, String messageBody, java.util.Map<String, String> data) {
        Log.d(TAG, "Mostrando notificación: " + title + " - " + messageBody);
        
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Si hay un activityId en los datos, abrir el detalle
        if (data != null && data.containsKey("activityId")) {
            intent = new Intent(this, detailActivity.class);
            intent.putExtra("idActividad", data.get("activityId"));
            if (data.containsKey("descripcion")) intent.putExtra("descripcion", data.get("descripcion"));
            if (data.containsKey("lugar")) intent.putExtra("lugar", data.get("lugar"));
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent, 
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder =
            new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logopacha)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = title.hashCode();
        Log.d(TAG, "Enviando notificación con ID: " + notificationId);
        notificationManager.notify(notificationId, notificationBuilder.build());
        Log.d(TAG, "Notificación enviada");
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}