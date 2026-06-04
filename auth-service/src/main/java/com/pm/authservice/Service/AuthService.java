package com.pm.authservice.Service;


import com.pm.authservice.Dto.LoginDto;
import com.pm.authservice.Dto.LoginResponseDto;
import com.pm.authservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final SessionService sessionService;

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        System.out.println("here we go");
        User user = (User) authentication.getPrincipal();
        System.out.println("here we go after get the user");
        String accessToken = jwtService.generateAccessToken(user);
        System.out.println("here we go after access token");
        String refreshToken = jwtService.generateRefreshToken(user);
        System.out.println("here we go after refresh token");
        sessionService.generateNewSession(user, refreshToken);
        System.out.println("here we go after new session");
        return new LoginResponseDto(accessToken, refreshToken,"hey guyys");
    }
    public LoginResponseDto refreshToken(String refreshToken) {
        Long userId = jwtService.getUserIdfromToken(refreshToken);
        sessionService.validateSession(refreshToken);
        User user = userService.getUserById(userId);

        String accessToken = jwtService.generateAccessToken(user);
        return new LoginResponseDto(accessToken, refreshToken,"hey guyys");
    }
}
