package com.example.pachaapp.Repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.pachaapp.Entity.TBusquedaReciente;

@Repository
public interface RepoBusquedaReciente extends JpaRepository<TBusquedaReciente,String> {
    List<TBusquedaReciente> findByIdUsuario(String idUsuario);
}
