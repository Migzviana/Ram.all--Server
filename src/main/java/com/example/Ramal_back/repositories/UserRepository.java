package com.example.Ramal_back.repositories;

import com.example.Ramal_back.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String resetToken);

}
