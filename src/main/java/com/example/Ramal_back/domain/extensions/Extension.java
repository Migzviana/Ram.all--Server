package com.example.Ramal_back.domain.extensions;
import com.example.Ramal_back.domain.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "extensions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Extension {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "extension_number", nullable = false, unique = true)
    private String extensionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "logged_user", referencedColumnName = "id", nullable = true)
    @JsonIgnore
    private User loggedUser;
}
