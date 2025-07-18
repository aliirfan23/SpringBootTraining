package com.redmath.user;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "users")
public class Users {

    @Id
    private Long userId;
    private String username;
    private String password;
    private String roles;
    private LocalDateTime createdAt;
}
