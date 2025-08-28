package com.example.pachaapp.Business;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.pachaapp.Dto.FCMTokenDto;
import com.example.pachaapp.Entity.TActivity;
import com.example.pachaapp.Entity.TUser;
import com.example.pachaapp.Repository.RepoActivity;
import com.example.pachaapp.Repository.RepoUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;

import jakarta.transaction.Transactional;

@Service
public class BusinessNotification {
    
    @Autowired
    private RepoUser repoUser;
    
    @Autowired
    private RepoActivity repoActivity;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /**
     * Actualizar el token FCM de un usuario
     */
    @Transactional
    public boolean updateFCMToken(FCMTokenDto fcmTokenDto) {
        try {
            Optional<TUser> optionalUser = repoUser.findById(fcmTokenDto.getIdUsuario());
            if (!optionalUser.isPresent()) {
                return false;
            }
            
            TUser user = optionalUser.get();
            user.setFcmToken(fcmTokenDto.getFcmToken());
            user.setFechaActualizacionToken(new Timestamp(System.currentTimeMillis()));
            
            repoUser.save(user);
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Enviar notificación de recordatorio de actividad
     */
    public void sendActivityReminder(String userId, String activityId, String title, String body) {
        executorService.execute(() -> {
            try {
                Optional<TUser> optionalUser = repoUser.findById(userId);
                if (!optionalUser.isPresent() || optionalUser.get().getFcmToken() == null) {
                    System.out.println("Usuario no encontrado o sin token FCM: " + userId);
                    return;
                }

                Optional<TActivity> optionalActivity = repoActivity.findById(activityId);
                if (!optionalActivity.isPresent()) {
                    System.out.println("Actividad no encontrada: " + activityId);
                    return;
                }

                TUser user = optionalUser.get();
                TActivity activity = optionalActivity.get();
                String fcmToken = user.getFcmToken();

                // Construir el mensaje (solo con datos, sin notification payload)
                Message message = Message.builder()
                    .setToken(fcmToken)
                    .putData("type", "activity_reminder")
                    .putData("title", title)
                    .putData("body", body)
                    .putData("activityId", activityId)
                    .putData("userId", userId)
                    .putData("description", activity.getDescripcion())
                    .putData("lugar", activity.getLugar() != null ? activity.getLugar() : "")
                    .build();

                // Enviar la notificación
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Notificación enviada exitosamente: " + response);
                
            } catch (Exception e) {
                System.err.println("Error al enviar notificación: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Método programado para enviar recordatorios de actividades próximas
     * Se ejecuta cada 15 minutos
     */
    @Scheduled(fixedRate = 900000) // 15 minutos = 900000 ms
    public void sendUpcomingActivityReminders() {
        try {
            // Obtener actividades que están por comenzar en los próximos 60 minutos
            List<TActivity> upcomingActivities = repoActivity.findActivitiesStartingInNextHour();
            
            for (TActivity activity : upcomingActivities) {
                long timeUntilActivity = activity.getFechaActividad().getTime() - System.currentTimeMillis();
                long minutesUntil = timeUntilActivity / (1000 * 60);
                
                String title = "Recordatorio de Actividad";
                String body = "Tu actividad '" + activity.getDescripcion() + "' comenzará en " + minutesUntil + " minutos";
                
                if (activity.getLugar() != null && !activity.getLugar().isEmpty()) {
                    body += " en " + activity.getLugar();
                }
                
                sendActivityReminder(activity.getIdUsuario(), activity.getIdActividad(), title, body);
            }
            
            if (!upcomingActivities.isEmpty()) {
                System.out.println("Enviados " + upcomingActivities.size() + " recordatorios de actividades próximas");
            }
            
        } catch (Exception e) {
            System.err.println("Error al enviar recordatorios programados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método programado para recordatorios de actividades del día siguiente
     * Se ejecuta todos los días a las 8:00 PM
     */
    @Scheduled(cron = "0 0 20 * * ?") // Todos los días a las 8:00 PM
    public void sendTomorrowActivityReminders() {
        try {
            List<TActivity> tomorrowActivities = repoActivity.findActivitiesForTomorrow();
            
            for (TActivity activity : tomorrowActivities) {
                String title = "Recordatorio para Mañana";
                String body = "Mañana tienes programada la actividad: '" + activity.getDescripcion() + "'";
                
                if (activity.getLugar() != null && !activity.getLugar().isEmpty()) {
                    body += " en " + activity.getLugar();
                }
                
                sendActivityReminder(activity.getIdUsuario(), activity.getIdActividad(), title, body);
            }
            
            if (!tomorrowActivities.isEmpty()) {
                System.out.println("Enviados " + tomorrowActivities.size() + " recordatorios para mañana");
            }
            
        } catch (Exception e) {
            System.err.println("Error al enviar recordatorios de mañana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Enviar notificación personalizada
     */
    public void sendCustomNotification(String userId, String title, String body, String type) {
        executorService.execute(() -> {
            try {
                Optional<TUser> optionalUser = repoUser.findById(userId);
                if (!optionalUser.isPresent() || optionalUser.get().getFcmToken() == null) {
                    return;
                }

                String fcmToken = optionalUser.get().getFcmToken();

                Message message = Message.builder()
                    .setToken(fcmToken)
                    .putData("type", type)
                    .putData("title", title)
                    .putData("body", body)
                    .putData("userId", userId)
                    .build();

                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Notificación personalizada enviada: " + response);
                
            } catch (Exception e) {
                System.err.println("Error al enviar notificación personalizada: " + e.getMessage());
            }
            
        });
            }   

    /**
     * Enviar notificación de creación de actividad
     */
    public void sendActivityCreationNotification(String userId, String activityDescription, Timestamp activityDate) {
        executorService.execute(() -> {
            try {
                Optional<TUser> optionalUser = repoUser.findById(userId);
                if (!optionalUser.isPresent() || optionalUser.get().getFcmToken() == null) {
                    System.out.println("Usuario no encontrado o sin token FCM: " + userId);
                    return;
                }

                TUser user = optionalUser.get();
                String fcmToken = user.getFcmToken();

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String formattedDate = sdf.format(activityDate);

                Message message = Message.builder()
                    .setToken(fcmToken)
                    .putData("type", "activity_created")
                    .putData("title", "Nueva actividad creada")
                    .putData("body", "Actividad: " + activityDescription + "\nFecha: " + formattedDate)
                    .putData("userId", userId)
                    .putData("activityDescription", activityDescription)
                    .putData("activityDate", String.valueOf(activityDate.getTime()))
                    .build();

                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("Notificación de actividad creada enviada: " + response);
                
            } catch (Exception e) {
                System.err.println("Error al enviar notificación de actividad creada: " + e.getMessage());
            }
        });
    }
}
