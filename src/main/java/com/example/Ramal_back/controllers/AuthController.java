package com.example.Ramal_back.controllers;

import com.example.Ramal_back.domain.user.User;
import com.example.Ramal_back.dto.*;
import com.example.Ramal_back.infra.security.TokenGenerator;
import com.example.Ramal_back.infra.security.TokenService;
import com.example.Ramal_back.repositories.UserRepository;
import com.example.Ramal_back.infra.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final EmailService emailService;
    private final TokenGenerator tokenGenerator;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO body) {
        try {
            User user = this.repository.findByEmail(body.email())
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            if (passwordEncoder.matches(body.password(), user.getPassword())) {
                String token = this.tokenService.generateToken(user);
                return ResponseEntity.ok(new ResponseDTO(user.getName(), user.getEmail(), token));
            }

            return ResponseEntity.badRequest().body("Senha incorreta");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO body) {
        Optional<User> user = this.repository.findByEmail(body.email());

        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setPassword(passwordEncoder.encode(body.password()));
            newUser.setEmail(body.email());
            newUser.setName(body.name());
            this.repository.save(newUser);

            String token = this.tokenService.generateToken(newUser);
            return ResponseEntity.ok(new ResponseDTO(newUser.getName(), newUser.getEmail(), token));
        }

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequestDTO body) {
        Optional<User> optionalUser = repository.findByEmail(body.email());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "E-mail não encontrado."));
        }

        User user = optionalUser.get();

        String token;
        do {
            token = tokenGenerator.generateToken(8);
        } while (repository.findByResetToken(token).isPresent());

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        repository.save(user);

        emailService.sendResetPasswordEmail(user.getEmail(), token);

        return ResponseEntity.ok(Map.of("message", "E-mail de recuperação enviado."));
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<?> verifyResetToken(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        Optional<User> optionalUser = repository.findByResetToken(token);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido."));
        }

        User user = optionalUser.get();

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expirado."));
        }

        return ResponseEntity.ok(Map.of("message", "Token válido."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordDTO body) {
        Optional<User> optionalUser = repository.findByResetToken(body.token());

        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido."));
        }

        User user = optionalUser.get();

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token expirado."));
        }

        user.setPassword(passwordEncoder.encode(body.newPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        repository.save(user);

        return ResponseEntity.ok(Map.of("message", "Senha redefinida com sucesso."));
    }
}