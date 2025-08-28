package com.example.pachaapp.Business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.pachaapp.Dto.DtoBusquedaReciente;

import com.example.pachaapp.Entity.TBusquedaReciente;
import com.example.pachaapp.Entity.TUser;
import com.example.pachaapp.Repository.RepoBusquedaReciente;
import com.example.pachaapp.Repository.RepoUser;

import jakarta.transaction.Transactional;

@Service
public class BusinessBusquedaReciente {

    @Autowired
    private RepoBusquedaReciente repoBusquedaReciente;

    @Autowired
    private RepoUser repoUser;

    @Transactional
    public DtoBusquedaReciente createBusqueda(DtoBusquedaReciente dtoBusquedaReciente) throws Exception {
        
        Optional<TUser> optionalUser = repoUser.findById(dtoBusquedaReciente.getIdUsuario());
        if (!optionalUser.isPresent()) {
            throw new RuntimeException("Usuario no encontrado con ID: " + dtoBusquedaReciente.getIdUsuario());
        }

        String busquedaRecienteId = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        TBusquedaReciente tBusquedaReciente = new TBusquedaReciente();
        tBusquedaReciente.setIdBusquedaReciente(busquedaRecienteId);
        tBusquedaReciente.setIdUsuario(dtoBusquedaReciente.getIdUsuario());
        tBusquedaReciente.setCiudad(dtoBusquedaReciente.getCiudad());
        tBusquedaReciente.setFechaRegistro(now);

        repoBusquedaReciente.save(tBusquedaReciente);

        dtoBusquedaReciente.setIdBusquedaReciente(busquedaRecienteId);
        dtoBusquedaReciente.setFechaRegistro(now);

        return dtoBusquedaReciente;
    }

    public List<DtoBusquedaReciente> getAllBusquedaReciente() {
        List<TBusquedaReciente> busquedaRecientes = repoBusquedaReciente.findAll();
        List<DtoBusquedaReciente> dtoBusquedaRecientes = new ArrayList<>();
        
        for (TBusquedaReciente busquedaReciente : busquedaRecientes) {
            dtoBusquedaRecientes.add(convertToDto(busquedaReciente));
        }
        
        return dtoBusquedaRecientes;
    }

    
    public List<DtoBusquedaReciente> getBusquedaRecienteByUser(String idUsuario) {
        List<TBusquedaReciente> busquedaRecientes = repoBusquedaReciente.findByIdUsuario(idUsuario);
        List<DtoBusquedaReciente> dtoBusquedaRecientes = new ArrayList<>();
        
        for (TBusquedaReciente busquedaReciente : busquedaRecientes) {
            dtoBusquedaRecientes.add(convertToDto(busquedaReciente));
        }
        
        return dtoBusquedaRecientes;
    }

    private DtoBusquedaReciente convertToDto(TBusquedaReciente tBusquedaReciente) {
        DtoBusquedaReciente dto = new DtoBusquedaReciente();
        dto.setIdBusquedaReciente(tBusquedaReciente.getIdBusquedaReciente());
        dto.setIdUsuario(tBusquedaReciente.getIdUsuario());
        dto.setCiudad(tBusquedaReciente.getCiudad());
        dto.setFechaRegistro(tBusquedaReciente.getFechaRegistro());
        
        if (tBusquedaReciente.getUsuario() != null) {
            dto.setNombreUsuario(tBusquedaReciente.getUsuario().getNombre());
            dto.setApellidoUsuario(tBusquedaReciente.getUsuario().getApellido());
            dto.setEmailUsuario(tBusquedaReciente.getUsuario().getEmail());
        }
        
        return dto;
    }



}
