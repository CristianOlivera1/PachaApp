package com.example.pachaapp.Controller.Notification;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pachaapp.Business.BusinessNotification;
import com.example.pachaapp.Controller.Generic.ResponseGeneric;
import com.example.pachaapp.Dto.FCMTokenDto;

@RestController
@RequestMapping("/user")
public class NotificationController {
    
    @Autowired
    private BusinessNotification businessNotification;

    /**
     * Endpoint para actualizar el token FCM de un usuario
     */
    @PostMapping("/fcm-token")
    public ResponseEntity<ResponseGeneric<String>> updateFCMToken(@RequestBody FCMTokenDto fcmTokenDto) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            // Validar datos de entrada
            if (fcmTokenDto.getIdUsuario() == null || fcmTokenDto.getIdUsuario().trim().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("El ID de usuario es requerido"));
                return ResponseEntity.badRequest().body(response);
            }
            
            if (fcmTokenDto.getFcmToken() == null || fcmTokenDto.getFcmToken().trim().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("El token FCM es requerido"));
                return ResponseEntity.badRequest().body(response);
            }

            // Actualizar el token
            boolean success = businessNotification.updateFCMToken(fcmTokenDto);
            
            if (success) {
                response.setType("success");
                response.setListMessage(List.of("Token FCM actualizado exitosamente"));
                response.setData("Token actualizado correctamente");
                return ResponseEntity.ok(response);
            } else {
                response.setType("error");
                response.setListMessage(List.of("Usuario no encontrado"));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error interno del servidor: " + e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Endpoint para enviar notificación de prueba
     */
    @PostMapping("/test-notification")
    public ResponseEntity<ResponseGeneric<String>> sendTestNotification(@RequestBody FCMTokenDto fcmTokenDto) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            businessNotification.sendCustomNotification(
                fcmTokenDto.getIdUsuario(),
                "Notificación de Prueba",
                "Esta es una notificación de prueba de PachaApp",
                "test"
            );
            
            response.setType("success");
            response.setListMessage(List.of("Notificación de prueba enviada exitosamente"));
            response.setData("Notificación enviada");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Error al enviar notificación: " + e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
