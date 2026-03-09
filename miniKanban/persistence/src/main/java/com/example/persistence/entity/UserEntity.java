package com.example.persistence.entity;

import com.example.persistence.type.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_name",nullable=false)
    private String userName;

    @Column(name="password",nullable=false)
    private String password;

    @Column(name="email",nullable = false,unique = true)
    private String email;


    @Column(name="created_at",updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void OnCreat(){
        createdAt = LocalDateTime.now();
    }
}
