package com.lifegame.mvp.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private Long userId;
}
