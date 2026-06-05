package com.pm.authservice.controller;


import com.pm.authservice.Dto.LoginDto;
import com.pm.authservice.Dto.LoginResponseDto;
import com.pm.authservice.Dto.SignupDto;
import com.pm.authservice.Dto.UserDto;
import com.pm.authservice.Service.AuthService;
import com.pm.authservice.Service.JwtService;
import com.pm.authservice.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class Logincontroller {
    private final UserService userService;
    private final AuthService  authService;
    private final JwtService jwtService;
    @PostMapping("genrate_token")
    public LoginResponseDto login(@RequestBody LoginDto userdto){
        LoginResponseDto loginResponseDto=authService.login(userdto);
        return loginResponseDto;

    }
    @PostMapping("signup")
    public UserDto signup(@RequestBody  SignupDto signupDto){
        return userService.signUp(signupDto);
    }
    @GetMapping("refresh_token")
    public LoginResponseDto getrefreshToken(@RequestBody String refreshToken){
        return authService.refreshToken(refreshToken);
    }
    @GetMapping("validate")
    public Map<String, Object> validateToken(
            @RequestHeader("Authorization") String token) {
        System.out.println("i am validating your token");

        String userId = jwtService.getNamefromToken(token);
        System.out.println("this is username"+ userId);
        return Map.of(
                "userId", userId //here i am giving user id but it can be diff here and in that company id it can be diff so here i should return orginaztion name
        );
    }
}
