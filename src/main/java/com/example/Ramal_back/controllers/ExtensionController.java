package com.example.Ramal_back.controllers;

import com.example.Ramal_back.domain.extensions.Extension;
import com.example.Ramal_back.domain.user.User;
import com.example.Ramal_back.dto.ExtensionLoginDTO;
import com.example.Ramal_back.dto.ExtensionResponseDTO;
import com.example.Ramal_back.repositories.ExtensionRepository;
import com.example.Ramal_back.repositories.UserRepository;
import com.example.Ramal_back.infra.security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/extensions")
@RequiredArgsConstructor
public class ExtensionController {

    private final UserRepository userRepository;
    private final ExtensionRepository extensionRepository;
    private final TokenService tokenService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllExtensions() {
        List<Extension> all = extensionRepository.findAll();
        List<ExtensionResponseDTO> response = all.stream()
                .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), ext.getLoggedUser()))
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableExtension() {
        List<Extension> available = extensionRepository.findByLoggedUserIsNull();
        if (available.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Nenhum ramal disponível no momento"));
        }

        List<ExtensionResponseDTO> response = available.stream()
                .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), ext.getLoggedUser()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginExtension(@RequestBody @Valid ExtensionLoginDTO dto, HttpServletRequest request) {
        Optional<Extension> optionalExtension = extensionRepository.findByExtensionNumber(dto.extension());
        if (optionalExtension.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ramal não encontrado."));
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token JWT não fornecido ou mal formatado."));
        }

        token = token.replace("Bearer ", "");
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido."));
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuário não encontrado."));
        }

        User user = optionalUser.get();
        Extension extension = optionalExtension.get();

        if (extension.getLoggedUser() != null) {
            return ResponseEntity.status(409).body(Map.of("message", "Ramal já está em uso."));
        }

        Optional<Extension> existingUserExtension = extensionRepository.findByLoggedUser_Id(user.getId());
        if (existingUserExtension.isPresent()) {
            return ResponseEntity.status(409).body(Map.of("message", "Usuário já está logado em outro ramal."));
        }

        extension.setLoggedUser(user);
        extensionRepository.save(extension);

        return ResponseEntity.ok(Map.of("message", "Login realizado com sucesso"));
    }

    @DeleteMapping("/logout/{extensionNumber}")
    public ResponseEntity<?> logoutExtension(@PathVariable String extensionNumber, HttpServletRequest request) {
        Optional<Extension> optionalExtension = extensionRepository.findByExtensionNumber(extensionNumber);
        if (optionalExtension.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Ramal não encontrado."));
        }

        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(Map.of("message", "Token JWT não fornecido ou mal formatado."));
        }

        token = token.replace("Bearer ", "");
        String email = tokenService.validateToken(token);
        if (email == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Token inválido."));
        }

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("message", "Usuário não encontrado."));
        }

        User user = optionalUser.get();
        Extension extension = optionalExtension.get();

        if (extension.getLoggedUser() == null) {
            return ResponseEntity.status(400).body(Map.of("message", "Esse ramal não está em uso."));
        }

        if (!extension.getLoggedUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("message", "Você não tem permissão para deslogar esse ramal."));
        }

        extension.setLoggedUser(null);
        extensionRepository.save(extension);

        return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso."));
    }

    @GetMapping("/range")
    public ResponseEntity<?> rangeExtensions(@RequestParam int inicio, @RequestParam int fim) {
        if (inicio > fim) {
            return ResponseEntity.badRequest().body(Map.of("message", "Intervalo inválido: Início maior que o fim"));
        }

        List<String> rangeNumeros = new ArrayList<>();
        for (int i = inicio; i <= fim; i++) {
            rangeNumeros.add(String.valueOf(i));
        }

        List<Extension> extensionsInRange = extensionRepository.findByExtensionNumberIn(rangeNumeros);

        List<ExtensionResponseDTO> response = extensionsInRange.stream()
                .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), ext.getLoggedUser()))
                .toList();

        return ResponseEntity.ok(response);
    }
}