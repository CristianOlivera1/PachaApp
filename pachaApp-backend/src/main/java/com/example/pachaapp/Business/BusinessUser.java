package com.example.pachaapp.Business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.pachaapp.Dto.DtoRegisterUser;
import com.example.pachaapp.Dto.DtoUser;
import com.example.pachaapp.Entity.TUser;
import com.example.pachaapp.Helper.Validation;
import com.example.pachaapp.Repository.RepoUser;

import jakarta.transaction.Transactional;

@Service
public class BusinessUser {
    @Value("${avatar.service.url}")
    private String avatarUrlService;

    @Autowired
    private RepoUser repoUser;

    @Autowired
    private SupabaseStorageService supabaseStorageService;

    @Transactional
    public void insert(DtoUser dtoUser) throws Exception {
        dtoUser.setIdUsuario(UUID.randomUUID().toString());
        dtoUser.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        TUser tUser = new TUser();
        tUser.setIdUsuario(dtoUser.getIdUsuario());
        tUser.setNombre(dtoUser.getNombre());
        tUser.setApellido(dtoUser.getApellido());
        tUser.setEmail(dtoUser.getEmail());
        tUser.setFechaRegistro(dtoUser.getFechaRegistro());

        repoUser.save(tUser);
    }

    @Transactional
    public DtoRegisterUser loginUsuarioConFirebase(String email, String firebaseUid) throws Exception {
  
        // Buscar usuario primero por Firebase UID (más confiable)
        Optional<TUser> optionalUser = repoUser.findByFirebaseUid(firebaseUid);

        // Si no existe por UID, buscar por email
        if (!optionalUser.isPresent()) {
            optionalUser = repoUser.findByEmail(email);
        }

        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Usuario no encontrado con email: " + email);
        }

        TUser usuario = optionalUser.get();
        System.out.println("Usuario encontrado: " + usuario.getIdUsuario());

        // Actualizar Firebase UID si es necesario
        if (usuario.getFirebaseUid() == null || !usuario.getFirebaseUid().equals(firebaseUid)) {
            System.out.println("Actualizando Firebase UID");
            usuario.setFirebaseUid(firebaseUid);
            repoUser.save(usuario);
        }

        DtoRegisterUser dto = new DtoRegisterUser();
        dto.setIdUsuario(usuario.getIdUsuario());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setAvatar(usuario.getFoto());
        dto.setFirebaseUid(usuario.getFirebaseUid());
        dto.setFechaRegistro(usuario.getFechaRegistro());

        return dto;
    }

    @Transactional
    public void registrarUsuarioConFirebase(DtoRegisterUser dto) throws Exception {

        String userId = UUID.randomUUID().toString();
        TUser usuario = new TUser();
        usuario.setIdUsuario(userId);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setFirebaseUid(dto.getFirebaseUid()); 

        usuario.setFechaRegistro(new Timestamp(System.currentTimeMillis()));

        String perfilUrl = subirFotoPerfil(dto, usuario);
        usuario.setFoto(perfilUrl);

        repoUser.save(usuario);

        dto.setIdUsuario(userId);
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setAvatar(perfilUrl);
    }

    public boolean emailExists(String email) {
        return repoUser.findByEmail(email).isPresent();
    }

    private String subirFotoPerfil(DtoRegisterUser dto, TUser usuario) throws Exception {

        String perfilPath = "foto/" + usuario.getIdUsuario() + ".png";

        byte[] imagenBytes;

        if (dto.getAvatar() != null && !dto.getAvatar().isEmpty()) {
            imagenBytes = Validation.descargarImagen(dto.getAvatar());
            if (imagenBytes == null || imagenBytes.length == 0) {
                throw new RuntimeException("La imagen descargada desde Google está vacía.");
            }
        } else {
            // Generar una imagen predeterminada si no hay avatar
            String[] nombres = dto.getNombre().split(" ");
            String nombre = nombres.length > 0 ? nombres[0] : "";
            String avatarUrl = avatarUrlService + "?name="
                    + URLEncoder.encode(nombre, StandardCharsets.UTF_8) + "&background=random";

            System.out.println("Generando imagen predeterminada desde URL: " + avatarUrl);
            imagenBytes = Validation.descargarImagen(avatarUrl);
            if (imagenBytes == null || imagenBytes.length == 0) {
                throw new RuntimeException("No se pudo generar la imagen predeterminada.");
            }
        }

        return supabaseStorageService.uploadFileUrl(imagenBytes, perfilPath, "image/png");
    }

    public DtoUser getUserById(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (!tUser.isPresent()) {
            return null;
        }

        TUser user = tUser.get();
        DtoUser dtoUser = new DtoUser();
        dtoUser.setIdUsuario(user.getIdUsuario());
        dtoUser.setNombre(user.getNombre());
        dtoUser.setApellido(user.getApellido());
        dtoUser.setEmail(user.getEmail());
        dtoUser.setFoto(user.getFoto());
        dtoUser.setFechaRegistro(user.getFechaRegistro());

        return dtoUser;
    }

    public List<DtoUser> getAll() {
        List<TUser> listTUser = repoUser.findAll();
        List<DtoUser> listDtoUser = new ArrayList<>();

        for (TUser item : listTUser) {
            DtoUser dtoUser = new DtoUser();
            dtoUser.setIdUsuario(item.getIdUsuario());
            dtoUser.setNombre(item.getNombre());
            dtoUser.setApellido(item.getApellido());
            dtoUser.setEmail(item.getEmail());
            dtoUser.setFoto(item.getFoto());
            dtoUser.setFechaRegistro(item.getFechaRegistro());

            listDtoUser.add(dtoUser);
        }

        return listDtoUser;
    }

    @Transactional
    public boolean delete(String idUsuario) {
        Optional<TUser> tUser = repoUser.findById(idUsuario);

        if (tUser.isPresent()) {
            repoUser.deleteById(idUsuario);
            return true;
        }

        return false;
    }
}
