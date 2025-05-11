package com.example.Ramal_back.dto;

import jakarta.validation.constraints.NotNull;

public record ExtensionLoginDTO(@NotNull(message = "Ramal não pode ser nulo") String extension) {
}
