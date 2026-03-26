package com.pm.authservice.Dto;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String email;
    private String name;
}
