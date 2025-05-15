package com.example.Ramal_back.domain.extensions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "extension_range")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtensionRange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int startRange;
    private int endRange;
}
