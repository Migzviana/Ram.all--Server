package com.example.Ramal_back.dto;

public record ExtensionStatusDTO(String extension, String status, String usuarioId) {
    public ExtensionStatusDTO(String extension, String status){
        this(extension, status, null);
    }
}
