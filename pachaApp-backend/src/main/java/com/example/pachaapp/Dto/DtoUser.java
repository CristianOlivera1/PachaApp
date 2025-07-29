package com.example.pachaapp.Dto;

import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUser {
	private String idUsuario;
	private String nombre;
	private String apellido;
	private String email;
	private String foto;
	private Timestamp fechaRegistro;
	private String jwtToken;
}