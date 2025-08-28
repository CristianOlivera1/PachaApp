package com.example.pachaapp.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.pachaapp.Entity.TActivity;

@Repository
public interface RepoActivity extends JpaRepository<TActivity, String> {
    
    // Buscar actividades por usuario
    List<TActivity> findByIdUsuario(String idUsuario);
    
    // Buscar actividades por estado
    List<TActivity> findByEstado(String estado);
    
    // Buscar actividades por usuario y estado
    List<TActivity> findByIdUsuarioAndEstado(String idUsuario, String estado);
    
    // Buscar actividades que ya deberían estar concluidas (fecha pasada y estado iniciado)
    @Query("SELECT a FROM TActivity a WHERE a.fechaActividad < CURRENT_TIMESTAMP AND a.estado = 'iniciado'")
    List<TActivity> findActivitiesExpired();
    
    // Buscar actividades de un usuario ordenadas por fecha
    @Query("SELECT a FROM TActivity a WHERE a.idUsuario = :idUsuario ORDER BY a.fechaActividad DESC")
    List<TActivity> findByIdUsuarioOrderByFechaActividadDesc(@Param("idUsuario") String idUsuario);
    
    // Buscar actividades que empiezan en la próxima hora (para recordatorios)
 @Query("SELECT a FROM TActivity a WHERE a.estado = 'iniciado' AND a.fechaActividad BETWEEN CURRENT_TIMESTAMP AND (CURRENT_TIMESTAMP + 1 HOUR)")
    List<TActivity> findActivitiesStartingInNextHour();
    
    // Buscar actividades programadas para mañana
    @Query("SELECT a FROM TActivity a WHERE a.estado = 'iniciado' AND DATE(a.fechaActividad) = DATE(CURRENT_TIMESTAMP + 1 DAY)")
    List<TActivity> findActivitiesForTomorrow();
    
}
