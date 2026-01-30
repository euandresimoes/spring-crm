package com.euandresimoes.spring_crm.auth.domain;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users", schema = "auth")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 50)
    private String email;
    @Column(name = "password_hash")
    private String passwordHash;
    private boolean active;

    public UserEntity() {}

    public UserEntity(String email,String passwordHash, boolean active) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
