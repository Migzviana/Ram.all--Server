package com.example.Ramal_back.dto;

import com.example.Ramal_back.domain.user.User;
import lombok.Data;

@Data
public class ExtensionResponseDTO {
    private String extensionNumber;
    private String loggedUser;
    private UserInfo user;

    public ExtensionResponseDTO(String extensionNumber, User loggedUser) {
        this.extensionNumber = extensionNumber;
        this.loggedUser = (loggedUser != null) ? loggedUser.getName() : null;

        if (loggedUser != null) {
            this.user = new UserInfo(loggedUser.getId(), loggedUser.getName(), loggedUser.getEmail());
        }
    }

    @Data
    public static class UserInfo {
        private final String id;
        private final String name;
        private final String email;
    }
}