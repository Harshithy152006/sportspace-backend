package com.example.sportspace.entity;
import jakarta.persistence.*; import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity @Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    private String name; @Column(unique = true, nullable = false) private String email;
    @JsonIgnore @Column(nullable = false) private String passwordHash; private String role; // USER or ADMIN
    public User() {}
    public User(String name, String email, String passwordHash, String role) { this.name = name; this.email = email; this.passwordHash = passwordHash; this.role = role; }
    public Long getId() { return id; } public String getName() { return name; } public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; } public String getRole() { return role; }
}
