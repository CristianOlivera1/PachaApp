package com.example.pachaapp.Controller.Firebase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.pachaapp.Business.BusinessUser;
import com.example.pachaapp.Controller.Generic.ResponseGeneric;
import com.example.pachaapp.Dto.DtoRegisterUser;
import com.example.pachaapp.Dto.FirebaseUserRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

@RestController
@RequestMapping("/auth/firebase")
@CrossOrigin
public class FirebaseAuthController {

    @Autowired
    private BusinessUser businessUser;

    @PostMapping("/login")
    public ResponseEntity<ResponseGeneric<DtoRegisterUser>> loginWithFirebase(
            @RequestBody FirebaseUserRequest request) {
        ResponseGeneric<DtoRegisterUser> response = new ResponseGeneric<>();

        try {
            if (request == null) {
                System.err.println("ERROR: Request es null");
                response.setType("error");
                response.setListMessage(List.of("No se recibieron datos"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (request.getFirebaseToken() == null || request.getFirebaseToken().isEmpty()) {
                System.err.println("ERROR: firebaseToken vacío");
                response.setType("error");
                response.setListMessage(List.of("Token de Firebase requerido"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                System.err.println("ERROR: email vacío");
                response.setType("error");
                response.setListMessage(List.of("Email requerido"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String firebaseToken = request.getFirebaseToken();
            String email = request.getEmail();
            String firebaseUid = request.getUid();

            // Verificar token de Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();

            if (!uid.equals(firebaseUid)) {
                System.err.println("ERROR: UID del token no coincide");
                response.setType("error");
                response.setListMessage(List.of("Token de Firebase no válido"));
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // VERIFICAR SI EL USUARIO EXISTE (diferencia clave con register)
            if (!businessUser.emailExists(email)) {
                System.out.println("Usuario no encontrado - redirigir a registro");
                response.setType("error");
                response.setListMessage(List.of("Usuario no registrado. Por favor regístrate primero."));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            DtoRegisterUser userDto = businessUser.loginUsuarioConFirebase(email, firebaseUid);

            System.out.println("Login exitoso para usuario: " + userDto.getIdUsuario());

            response.setType("success");
            response.setListMessage(List.of("Login exitoso"));
            response.setData(userDto);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("ERROR en login Firebase: " + e.getMessage());
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error interno: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseGeneric<DtoRegisterUser>> registerWithFirebase(
            @RequestBody FirebaseUserRequest request) {
        ResponseGeneric<DtoRegisterUser> response = new ResponseGeneric<>();

        try {
            if (request == null) {
                response.setType("error");
                response.setListMessage(List.of("No se recibieron datos"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            // Validar campos requeridos
            if (request.getFirebaseToken() == null || request.getFirebaseToken().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("Token de Firebase requerido"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("Email requerido"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            if (request.getUid() == null || request.getUid().isEmpty()) {
                response.setType("error");
                response.setListMessage(List.of("UID de Firebase requerido"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String firebaseToken = request.getFirebaseToken();
            String email = request.getEmail();
            String apellido = request.getApellido();
            String nombre = request.getNombre() != null ? request.getNombre() : "";
            String photoUrl = request.getPhotoUrl();
            String firebaseUid = request.getUid();

            // Verificar token de Firebase
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(firebaseToken);
            String uid = decodedToken.getUid();

            if (!uid.equals(firebaseUid)) {
                System.err.println("ERROR: UID del token no coincide");
                response.setType("error");
                response.setListMessage(List.of("Token de Firebase no válido"));
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }

            // Verificar si el usuario ya existe
            if (businessUser.emailExists(email)) {
                System.out.println("Usuario existente encontrado");
                response.setType("error");
                response.setListMessage(List.of("El usuario ya existe"));
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }

            DtoRegisterUser dto = new DtoRegisterUser();
            dto.setEmail(email);

            if (nombre != null && !nombre.isEmpty()) {
                String[] nombrePartes = nombre.trim().split(" ", 2);
                dto.setNombre(nombrePartes[0]);
                dto.setApellido(nombrePartes.length > 1 ? nombrePartes[1] : "");
            } else {
                dto.setNombre("Usuario");
                dto.setApellido("Firebase");
            }

            dto.setAvatar(photoUrl);
            dto.setFirebaseUid(firebaseUid);

            businessUser.registrarUsuarioConFirebase(dto);

            response.setType("success");
            response.setListMessage(List.of("Registro exitoso"));
            response.setData(dto);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error interno: " + e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}