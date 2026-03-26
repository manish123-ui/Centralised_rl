package com.pm.authservice.Dto;

import com.pm.authservice.enums.Authority;
import com.pm.authservice.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class SignupDto {
    private String email;
    private String password;
    private String name;
    private Set<Role> roles;
    private Set<Authority> authoritys;
}
