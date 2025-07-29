package com.example.pachaapp.Controller.Activity;

import java.util.List;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pachaapp.Business.BusinessActivity;
import com.example.pachaapp.Controller.Generic.ResponseGeneric;
import com.example.pachaapp.Controller.Activity.ResponseObject.ResponseGetAllActivities;
import com.example.pachaapp.Dto.DtoActivity;

@RestController
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    private BusinessActivity businessActivity;

    @PostMapping("/create")
    public ResponseEntity<ResponseGeneric<DtoActivity>> createActivity(
            @RequestParam String idUsuario,
            @RequestParam String descripcion,
            @RequestParam(required = false) String lugar,
            @RequestParam long fechaActividad) {
        
        ResponseGeneric<DtoActivity> response = new ResponseGeneric<>();
        try {
            DtoActivity dtoActivity = new DtoActivity();
            dtoActivity.setIdUsuario(idUsuario);
            dtoActivity.setDescripcion(descripcion);
            dtoActivity.setLugar(lugar);
            dtoActivity.setFechaActividad(new Timestamp(fechaActividad));

            DtoActivity createdActivity = businessActivity.createActivity(dtoActivity);

            response.setType("success");
            response.setListMessage(List.of("Actividad creada correctamente"));
            response.setData(createdActivity);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/update/{idActividad}")
    public ResponseEntity<ResponseGeneric<DtoActivity>> updateActivity(
            @PathVariable String idActividad,
            @RequestParam String idUsuario,
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String lugar,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Long fechaActividad) {
        
        ResponseGeneric<DtoActivity> response = new ResponseGeneric<>();
        try {
            DtoActivity dtoActivity = new DtoActivity();
            dtoActivity.setIdUsuario(idUsuario);
            
            if (descripcion != null) {
                dtoActivity.setDescripcion(descripcion);
            }
            if (lugar != null) {
                dtoActivity.setLugar(lugar);
            }
            if (estado != null) {
                dtoActivity.setEstado(estado);
            }
            if (fechaActividad != null) {
                dtoActivity.setFechaActividad(new Timestamp(fechaActividad));
            }

            DtoActivity updatedActivity = businessActivity.updateActivity(idActividad, dtoActivity);

            response.setType("success");
            response.setListMessage(List.of("Actividad actualizada correctamente"));
            response.setData(updatedActivity);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get/{idActividad}")
    public ResponseEntity<ResponseGeneric<DtoActivity>> getActivityById(@PathVariable String idActividad) {
        ResponseGeneric<DtoActivity> response = new ResponseGeneric<>();
        try {
            DtoActivity dtoActivity = businessActivity.getActivityById(idActividad);

            if (dtoActivity == null) {
                response.setType("error");
                response.setListMessage(List.of("Actividad no encontrada"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setType("success");
            response.setData(dtoActivity);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al obtener la actividad"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/user/{idUsuario}")
    public ResponseEntity<ResponseGetAllActivities> getActivitiesByUser(@PathVariable String idUsuario) {
        ResponseGetAllActivities response = new ResponseGetAllActivities();
        try {
            List<DtoActivity> activities = businessActivity.getActivitiesByUser(idUsuario);
            response.setData(activities);
            response.setType("success");
            response.setListMessage(List.of("Actividades obtenidas correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las actividades del usuario"));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/user/{idUsuario}/status/{estado}")
    public ResponseEntity<ResponseGetAllActivities> getActivitiesByUserAndStatus(
            @PathVariable String idUsuario, 
            @PathVariable String estado) {
        ResponseGetAllActivities response = new ResponseGetAllActivities();
        try {
            if (!estado.equals("iniciado") && !estado.equals("concluido")) {
                response.setType("error");
                response.setListMessage(List.of("Estado inválido. Debe ser 'iniciado' o 'concluido'"));
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            List<DtoActivity> activities = businessActivity.getActivitiesByUserAndStatus(idUsuario, estado);
            response.setData(activities);
            response.setType("success");
            response.setListMessage(List.of("Actividades obtenidas correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las actividades"));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllActivities> getAllActivities() {
        ResponseGetAllActivities response = new ResponseGetAllActivities();
        try {
            List<DtoActivity> activities = businessActivity.getAllActivities();
            response.setData(activities);
            response.setType("success");
            response.setListMessage(List.of("Todas las actividades obtenidas correctamente"));

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of("Error al obtener las actividades"));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/complete/{idActividad}")
    public ResponseEntity<ResponseGeneric<DtoActivity>> markActivityAsCompleted(
            @PathVariable String idActividad,
            @RequestParam String idUsuario) {
        
        ResponseGeneric<DtoActivity> response = new ResponseGeneric<>();
        try {
            DtoActivity completedActivity = businessActivity.markActivityAsCompleted(idActividad, idUsuario);

            response.setType("success");
            response.setListMessage(List.of("Actividad marcada como concluida"));
            response.setData(completedActivity);

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{idActividad}")
    public ResponseEntity<ResponseGeneric<String>> deleteActivity(
            @PathVariable String idActividad,
            @RequestParam String idUsuario) {
        
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            boolean deleted = businessActivity.deleteActivity(idActividad, idUsuario);
            
            if (!deleted) {
                response.setType("error");
                response.setListMessage(List.of("No se encontró la actividad para eliminar"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            response.setType("success");
            response.setListMessage(List.of("Actividad eliminada correctamente"));
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setType("error");
            response.setListMessage(List.of(e.getMessage()));
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
