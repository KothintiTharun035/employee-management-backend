package com.ems.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String password;

    @Builder.Default
    private Role role = Role.ROLE_USER;

    public enum Role {
        ROLE_ADMIN,
        ROLE_USER,
        ROLE_MANAGER
    }
}