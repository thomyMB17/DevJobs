package com.example.devjobs.service;

import com.example.devjobs.dto.request.LoginRequest;
import com.example.devjobs.dto.request.RegisterRequest;
import com.example.devjobs.dto.response.AuthResponse;
import com.example.devjobs.exception.DuplicateEmailException;
import com.example.devjobs.exception.ResourceNotFoundException;
import com.example.devjobs.exception.UnauthorizedActionException;
import com.example.devjobs.model.User;
import com.example.devjobs.repository.IUserRepository;
import com.example.devjobs.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUserRepository iUserRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Iniciando registro de usuario con correo: {}", request.getEmail());
        if (iUserRepository.findByEmail(request.getEmail()).isPresent()){
            log.warn("Usuario con email {} ya existe.", request.getEmail());
            throw new DuplicateEmailException("Email ya existe.");
        }
        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        iUserRepository.save(user);
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", request.getRole());
        claims.put("email", request.getEmail());

        log.info("Generando token JWT para el usuario: {}", request.getEmail());
        String token = jwtService.getToken(claims, request.getEmail());
        log.info("Registro completado correctamente para el usuario: {}", request.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole())
                .build();
    }
    public AuthResponse login(LoginRequest request){
        User user = iUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn("Usuario con email: {} no existe.", request.getEmail());
                    return new ResourceNotFoundException("Email no existe.");
                });
        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            log.warn("Login fallido: contraseña incorrecta | email: {}", request.getEmail());
            throw new UnauthorizedActionException("Contraseña incorrecta.");
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole());
        claims.put("email", user.getEmail());

        log.info("Generando token para el usuario: {}", user.getEmail());
        String token = jwtService.getToken(claims, user.getEmail());
        log.info("Login exitoso para el usuario: {}", user.getEmail());
        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();

    }
}
