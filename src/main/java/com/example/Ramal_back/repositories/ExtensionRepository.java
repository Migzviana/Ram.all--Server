package com.example.Ramal_back.repositories;

import com.example.Ramal_back.domain.extensions.Extension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExtensionRepository extends JpaRepository<Extension, Long> {
    Optional<Extension> findByLoggedUser_Id(String userId);
    Optional<Extension> findByExtensionNumber(String extensionNumber);
    List<Extension> findByLoggedUserIsNull();
    List<Extension> findByExtensionNumberIn(List<String> extensionNumbers);
}
