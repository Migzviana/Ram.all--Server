package com.example.Ramal_back.controllers;

import com.example.Ramal_back.domain.user.User;
import com.example.Ramal_back.dto.LoginRequestDTO;
import com.example.Ramal_back.dto.RegisterRequestDTO;
import com.example.Ramal_back.dto.ResponseDTO;
import com.example.Ramal_back.infra.security.TokenService;
import com.example.Ramal_back.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequestDTO body) {
        System.out.println("🔐 Tentando login...");

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
    public ResponseEntity register(@RequestBody RegisterRequestDTO body) {
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
}

