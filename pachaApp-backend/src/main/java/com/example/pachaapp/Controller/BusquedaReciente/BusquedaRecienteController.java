package com.example.pachaapp.Controller.BusquedaReciente;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pachaapp.Business.BusinessBusquedaReciente;
import com.example.pachaapp.Controller.BusquedaReciente.ResponseObject.ResponseGetAllBusquedaReciente;
import com.example.pachaapp.Controller.Generic.ResponseGeneric;

import com.example.pachaapp.Dto.DtoBusquedaReciente;

@RestController
@RequestMapping("/busqueda")
public class BusquedaRecienteController {
@Autowired
    private BusinessBusquedaReciente businessBusquedaReciente;

    // ✅ Crear búsqueda
    @PostMapping("/create")
    public ResponseEntity<ResponseGeneric<DtoBusquedaReciente>> createBusqueda(
            @RequestParam String idUsuario,
            @RequestParam String ciudad) {

        ResponseGeneric<DtoBusquedaReciente> response = new ResponseGeneric<>();
        try {
            DtoBusquedaReciente dtoBusquedaReciente = new DtoBusquedaReciente();
            dtoBusquedaReciente.setIdUsuario(idUsuario);
            dtoBusquedaReciente.setCiudad(ciudad);

            DtoBusquedaReciente creaDtoBusquedaReciente = businessBusquedaReciente.createBusqueda(dtoBusquedaReciente);

            response.setType("success");
            response.setListMessage(List.of("Búsqueda creada correctamente"));
            response.setData(creaDtoBusquedaReciente);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/{idUsuario}")
    public ResponseEntity<ResponseGetAllBusquedaReciente> getBusquedaRecienteByUser(@PathVariable String idUsuario) {
        ResponseGetAllBusquedaReciente response = new ResponseGetAllBusquedaReciente();
        try {
            List<DtoBusquedaReciente> busquedaRecientes = businessBusquedaReciente.getBusquedaRecienteByUser(idUsuario);
            response.setData(busquedaRecientes);
            response.setType("success");
            response.setListMessage(List.of("Busquedas Recientes obtenidas correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las actividades del usuario"));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

     @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllBusquedaReciente> getAllBusquedaReciente() {
        ResponseGetAllBusquedaReciente response = new ResponseGetAllBusquedaReciente();
        try {
            List<DtoBusquedaReciente> busquedaRecientes = businessBusquedaReciente.getAllBusquedaReciente();
            response.setData(busquedaRecientes);
            response.setType("success");
            response.setListMessage(List.of("Todas las busquedas recientes obtenidas correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las busquedas recientes"));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }





}
