package com.example.pachaapp.Controller.User;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.pachaapp.Business.BusinessUser;
import com.example.pachaapp.Controller.Generic.ResponseGeneric;
import com.example.pachaapp.Controller.User.ResponseObject.ResponseGetAllUsers;
import com.example.pachaapp.Dto.DtoUser;


@RestController

@RequestMapping("/user")
public class UserController {

    @Autowired
    private BusinessUser businessUser;

    @PostMapping("/insert")
    public ResponseEntity<ResponseGeneric<DtoUser>> insert(@RequestParam String email,@RequestParam String contrasenha, @RequestParam String nombre, @RequestParam String apellido) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
        try {
            if (businessUser.emailExists(email)) {
                response.setType("error");
                response.setListMessage(List.of("El nombre de usuario ya existe"));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            DtoUser dtoUser = new DtoUser();
            dtoUser.setEmail(email);
            dtoUser.setNombre(nombre);
            dtoUser.setApellido(apellido);

            businessUser.insert(dtoUser);

            response.setType("success");
            response.setListMessage(List.of("Registro realizado correctamente"));
            response.setData(dtoUser);

            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List
                    .of("Ocurrió un error inesperado, estamos trabajando para resolverlo. Gracias por su paciencia."));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{idUsuario}")
    public ResponseEntity<ResponseGeneric<DtoUser>> getUserById(@PathVariable String idUsuario) {
        ResponseGeneric<DtoUser> response = new ResponseGeneric<>();
        try {
            DtoUser dtoUser = businessUser.getUserById(idUsuario);

            if (dtoUser == null) {
                response.setType("error");
                response.setListMessage(List.of("Usuario no encontrado"));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            response.setType("success");
            response.setData(dtoUser);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            response.setType("exception");
            response.setListMessage(List.of("Error al obtener el Usuario"));
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getall")
    public ResponseEntity<ResponseGetAllUsers> getAll() {
        ResponseGetAllUsers response = new ResponseGetAllUsers();

        try {
            List<DtoUser> listDtoUser = businessUser.getAll();
            response.setData(listDtoUser);
            response.setType("success");
            response.setListMessage(List.of("Datos obtenidos correctamente."));

        } catch (Exception e) {
            response.setType("error");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{idUsuario}")
    public ResponseEntity<ResponseGeneric<String>> delete(@PathVariable String idUsuario) {
        ResponseGeneric<String> response = new ResponseGeneric<>();
        try {
            boolean deleted = businessUser.delete(idUsuario);
            if (!deleted) {
                response.setType("error");
                response.setListMessage(List.of("No se encontró el registro para eliminar."));
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            response.setType("success");
            response.setListMessage(List.of("Eliminación realizada correctamente"));
        } catch (Exception e) {
            response.setType("exception");
            response.setListMessage(List.of("Ocurrió un error inesperado."));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
