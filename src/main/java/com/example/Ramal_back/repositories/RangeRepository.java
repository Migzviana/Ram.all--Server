package com.example.Ramal_back.repositories;

import com.example.Ramal_back.domain.extensions.ExtensionRange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RangeRepository extends JpaRepository<ExtensionRange, Long> {}
