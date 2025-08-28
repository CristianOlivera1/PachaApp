package com.example.pachaapp.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FCMTokenDto {
    private String idUsuario;
    private String fcmToken;

    public FCMTokenDto() {}

    public FCMTokenDto(String idUsuario, String fcmToken) {
        this.idUsuario = idUsuario;
        this.fcmToken = fcmToken;
    }
}
