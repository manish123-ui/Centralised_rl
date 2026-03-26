package com.pm.authservice.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginResponseDto {
    private Long id;
    private String accessToken;
    private String refreshToken;
}
