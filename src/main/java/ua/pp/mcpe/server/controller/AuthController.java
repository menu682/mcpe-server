package ua.pp.mcpe.server.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.pp.mcpe.server.dto.MessageResponseDto;
import ua.pp.mcpe.server.dto.security.JwtResponseDto;
import ua.pp.mcpe.server.dto.security.LoginRequestDto;
import ua.pp.mcpe.server.dto.security.SignupRequestDto;
import ua.pp.mcpe.server.service.security.AuthService;

import javax.validation.Valid;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController()
@RequestMapping("/api_v1/auth/")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public JwtResponseDto login(@Valid @RequestBody LoginRequestDto loginRequestDTO){
        return authService.loginUser(loginRequestDTO);
    }

    @PostMapping("/register")
    public MessageResponseDto register(@Valid @RequestBody SignupRequestDto signupRequestDTO){
        return authService.registerUser(signupRequestDTO);
    }

}