package com.example.pachaapp.Business;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.pachaapp.Dto.DtoActivity;
import com.example.pachaapp.Entity.TActivity;
import com.example.pachaapp.Entity.TUser;
import com.example.pachaapp.Repository.RepoActivity;
import com.example.pachaapp.Repository.RepoUser;

import jakarta.transaction.Transactional;

@Service
public class BusinessActivity {
    
    @Autowired
    private RepoActivity repoActivity;
    
    @Autowired
    private RepoUser repoUser;
    
    @Transactional
    public DtoActivity createActivity(DtoActivity dtoActivity) throws Exception {
        // Validar que el usuario existe
        Optional<TUser> optionalUser = repoUser.findById(dtoActivity.getIdUsuario());
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + dtoActivity.getIdUsuario());
        }
        
        // Validar datos requeridos
        if (dtoActivity.getDescripcion() == null || dtoActivity.getDescripcion().trim().isEmpty()) {
            throw new RuntimeException("La descripción es requerida");
        }
        
        if (dtoActivity.getFechaActividad() == null) {
            throw new RuntimeException("La fecha de actividad es requerida");
        }
        
        // Validar que la fecha no sea en el pasado
        if (dtoActivity.getFechaActividad().before(new Timestamp(System.currentTimeMillis()))) {
            throw new RuntimeException("La fecha de actividad no puede ser en el pasado");
        }
        
        String activityId = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        TActivity tActivity = new TActivity();
        tActivity.setIdActividad(activityId);
        tActivity.setIdUsuario(dtoActivity.getIdUsuario());
        tActivity.setDescripcion(dtoActivity.getDescripcion());
        tActivity.setLugar(dtoActivity.getLugar());
        tActivity.setEstado("iniciado"); // Estado inicial siempre es "iniciado"
        tActivity.setFechaActividad(dtoActivity.getFechaActividad());
        tActivity.setFechaRegistro(now);
        tActivity.setFechaActualizacion(now);
        
        repoActivity.save(tActivity);
        
        dtoActivity.setIdActividad(activityId);
        dtoActivity.setEstado("iniciado");
        dtoActivity.setFechaRegistro(now);
        dtoActivity.setFechaActualizacion(now);
        
        return dtoActivity;
    }
    
    @Transactional
    public DtoActivity updateActivity(String idActividad, DtoActivity dtoActivity) throws Exception {
        Optional<TActivity> optionalActivity = repoActivity.findById(idActividad);
        if (!optionalActivity.isPresent()) {
            throw new RuntimeException("Actividad no encontrada con ID: " + idActividad);
        }
        
        TActivity tActivity = optionalActivity.get();
        
        // Validar que solo el propietario puede modificar la actividad
        if (!tActivity.getIdUsuario().equals(dtoActivity.getIdUsuario())) {
            throw new RuntimeException("No tienes permisos para modificar esta actividad");
        }
        
        // Solo permitir actualizar ciertos campos
        if (dtoActivity.getDescripcion() != null && !dtoActivity.getDescripcion().trim().isEmpty()) {
            tActivity.setDescripcion(dtoActivity.getDescripcion());
        }
        
        if (dtoActivity.getLugar() != null) {
            tActivity.setLugar(dtoActivity.getLugar());
        }
        
        if (dtoActivity.getFechaActividad() != null) {
            // Validar que la nueva fecha no sea en el pasado si la actividad está iniciada
            if (tActivity.getEstado().equals("iniciado") && 
                dtoActivity.getFechaActividad().before(new Timestamp(System.currentTimeMillis()))) {
                throw new RuntimeException("La fecha de actividad no puede ser en el pasado");
            }
            tActivity.setFechaActividad(dtoActivity.getFechaActividad());
        }
        
        if (dtoActivity.getEstado() != null && 
            (dtoActivity.getEstado().equals("iniciado") || dtoActivity.getEstado().equals("concluido"))) {
            tActivity.setEstado(dtoActivity.getEstado());
        }
        
        tActivity.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        
        repoActivity.save(tActivity);
        
        return convertToDto(tActivity);
    }
    
    public DtoActivity getActivityById(String idActividad) {
        Optional<TActivity> optionalActivity = repoActivity.findById(idActividad);
        if (!optionalActivity.isPresent()) {
            return null;
        }
        
        return convertToDto(optionalActivity.get());
    }
    
    public List<DtoActivity> getActivitiesByUser(String idUsuario) {
        List<TActivity> activities = repoActivity.findByIdUsuarioOrderByFechaActividadDesc(idUsuario);
        List<DtoActivity> dtoActivities = new ArrayList<>();
        
        for (TActivity activity : activities) {
            dtoActivities.add(convertToDto(activity));
        }
        
        return dtoActivities;
    }
    
    public List<DtoActivity> getActivitiesByUserAndStatus(String idUsuario, String estado) {
        List<TActivity> activities = repoActivity.findByIdUsuarioAndEstado(idUsuario, estado);
        List<DtoActivity> dtoActivities = new ArrayList<>();
        
        for (TActivity activity : activities) {
            dtoActivities.add(convertToDto(activity));
        }
        
        return dtoActivities;
    }
    
    public List<DtoActivity> getAllActivities() {
        List<TActivity> activities = repoActivity.findAll();
        List<DtoActivity> dtoActivities = new ArrayList<>();
        
        for (TActivity activity : activities) {
            dtoActivities.add(convertToDto(activity));
        }
        
        return dtoActivities;
    }
    
    @Transactional
    public boolean deleteActivity(String idActividad, String idUsuario) {
        Optional<TActivity> optionalActivity = repoActivity.findById(idActividad);
        if (!optionalActivity.isPresent()) {
            return false;
        }
        
        TActivity activity = optionalActivity.get();
        
        // Validar que solo el propietario puede eliminar la actividad
        if (!activity.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permisos para eliminar esta actividad");
        }
        
        repoActivity.deleteById(idActividad);
        return true;
    }
    
    @Transactional
    public DtoActivity markActivityAsCompleted(String idActividad, String idUsuario) throws Exception {
        Optional<TActivity> optionalActivity = repoActivity.findById(idActividad);
        if (!optionalActivity.isPresent()) {
            throw new RuntimeException("Actividad no encontrada con ID: " + idActividad);
        }
        
        TActivity tActivity = optionalActivity.get();
        
        // Validar que solo el propietario puede marcar como concluida
        if (!tActivity.getIdUsuario().equals(idUsuario)) {
            throw new RuntimeException("No tienes permisos para modificar esta actividad");
        }
        
        if (tActivity.getEstado().equals("concluido")) {
            throw new RuntimeException("La actividad ya está concluida");
        }
        
        tActivity.setEstado("concluido");
        tActivity.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        
        repoActivity.save(tActivity);
        
        return convertToDto(tActivity);
    }
    
    // Método programado para ejecutarse cada hora y marcar como concluidas las actividades vencidas
    @Scheduled(fixedRate = 3600000) // Ejecutar cada hora (3600000 ms)
    @Transactional
    public void markExpiredActivitiesAsCompleted() {
        List<TActivity> expiredActivities = repoActivity.findActivitiesExpired();
        
        for (TActivity activity : expiredActivities) {
            activity.setEstado("concluido");
            activity.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
            repoActivity.save(activity);
        }
        
        if (!expiredActivities.isEmpty()) {
            System.out.println("Se marcaron " + expiredActivities.size() + " actividades como concluidas automáticamente");
        }
    }
    
    private DtoActivity convertToDto(TActivity tActivity) {
        DtoActivity dto = new DtoActivity();
        dto.setIdActividad(tActivity.getIdActividad());
        dto.setIdUsuario(tActivity.getIdUsuario());
        dto.setDescripcion(tActivity.getDescripcion());
        dto.setLugar(tActivity.getLugar());
        dto.setEstado(tActivity.getEstado());
        dto.setFechaActividad(tActivity.getFechaActividad());
        dto.setFechaRegistro(tActivity.getFechaRegistro());
        dto.setFechaActualizacion(tActivity.getFechaActualizacion());
        
        // Agregar información del usuario si está disponible
        if (tActivity.getUsuario() != null) {
            dto.setNombreUsuario(tActivity.getUsuario().getNombre());
            dto.setApellidoUsuario(tActivity.getUsuario().getApellido());
            dto.setEmailUsuario(tActivity.getUsuario().getEmail());
        }
        
        return dto;
    }
}
