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
        System.out.println("📨 Email recebido: " + body.email());
        System.out.println("🔑 Senha recebida (não exibida por segurança)");

        try {
            User user = this.repository.findByEmail(body.email())
                    .orElseThrow(() -> new RuntimeException("❌ Usuário não encontrado com o email: " + body.email()));

            System.out.println("✅ Usuário encontrado: " + user.getEmail());
            System.out.println("🔍 Verificando senha...");

            if (passwordEncoder.matches(body.password(), user.getPassword())) {
                System.out.println("🔓 Senha válida! Gerando token...");
                String token = this.tokenService.generateToken(user);
                System.out.println("✅ Token gerado com sucesso!");
                return ResponseEntity.ok(new ResponseDTO(user.getName(), user.getEmail(), token));
            }

            System.out.println("❌ Senha incorreta para o email: " + body.email());
            return ResponseEntity.badRequest().body("Senha incorreta");

        } catch (RuntimeException e) {
            System.out.println("⚠️ Erro no login: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterRequestDTO body){
        Optional<User> user = this.repository.findByEmail(body.email());

        if(user.isEmpty()) {
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

