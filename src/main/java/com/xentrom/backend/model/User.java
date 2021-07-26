package com.xentrom.backend.model;

import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private RoleEnum role;

    @Getter
    public enum RoleEnum {
        USER ("USER"),
        ADMIN ("ADMIN"),
        ;

        private final String role;

        RoleEnum(String role) {
            this.role = role;
        }
    }
}
