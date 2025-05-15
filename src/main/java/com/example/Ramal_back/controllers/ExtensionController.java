package com.example.Ramal_back.controllers;

import com.example.Ramal_back.domain.extensions.Extension;
import com.example.Ramal_back.domain.user.User;
import com.example.Ramal_back.dto.ExtensionLoginDTO;
import com.example.Ramal_back.dto.ExtensionResponseDTO;
import com.example.Ramal_back.infra.security.TokenService;
import com.example.Ramal_back.infra.service.RangeService;
import com.example.Ramal_back.repositories.ExtensionRepository;
import com.example.Ramal_back.repositories.UserRepository;
import com.example.Ramal_back.infra.service.RangeService;
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
    private final RangeService extensionRangeService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllExtensions() {
        try {
            List<Extension> all = extensionRepository.findAll();
            List<ExtensionResponseDTO> response = all.stream()
                    .filter(ext -> extensionRangeService.isInRange(ext.getExtensionNumber())
                            || ext.getLoggedUser() != null)
                    .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), ext.getLoggedUser()))
                    .toList();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao buscar todos os ramais."));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableExtension() {
        try {
            List<Extension> available = extensionRepository.findByLoggedUserIsNull();
            List<ExtensionResponseDTO> response = available.stream()
                    .filter(ext -> extensionRangeService.isInRange(ext.getExtensionNumber()))
                    .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), null))
                    .toList();

            if (response.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Nenhum ramal disponível no momento"));
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao buscar ramais disponíveis."));
        }
    }

    @GetMapping("/range")
    public ResponseEntity<?> getExtensionsInRangeOrLogged() {
        try {
            List<Extension> all = extensionRepository.findAll();
            List<ExtensionResponseDTO> filtered = all.stream()
                    .filter(ext -> extensionRangeService.isInRange(ext.getExtensionNumber())
                            || ext.getLoggedUser() != null)
                    .map(ext -> new ExtensionResponseDTO(ext.getExtensionNumber(), ext.getLoggedUser()))
                    .toList();

            return ResponseEntity.ok(filtered);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao buscar ramais no intervalo informado."));
        }
    }

    @PostMapping("/set-range")
    public ResponseEntity<?> setRange(@RequestParam int inicio, @RequestParam int fim) {
        if (inicio > fim) {
            return ResponseEntity.badRequest().body(Map.of("message", "Início não pode ser maior que o fim."));
        }

        extensionRangeService.setRange(inicio, fim);
        return ResponseEntity.ok(Map.of("message", "Intervalo atualizado com sucesso."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginExtension(@RequestBody @Valid ExtensionLoginDTO dto, HttpServletRequest request) {
        try {
            Optional<Extension> optionalExtension = extensionRepository.findByExtensionNumber(dto.extension());
            if (optionalExtension.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Ramal não encontrado."));
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
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao realizar login no ramal."));
        }
    }

    @DeleteMapping("/logout/{extensionNumber}")
    public ResponseEntity<?> logoutExtension(@PathVariable String extensionNumber, HttpServletRequest request) {
        try {
            Optional<Extension> optionalExtension = extensionRepository.findByExtensionNumber(extensionNumber);
            if (optionalExtension.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("message", "Ramal não encontrado."));
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
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Erro ao realizar logout do ramal."));
        }
    }
}
