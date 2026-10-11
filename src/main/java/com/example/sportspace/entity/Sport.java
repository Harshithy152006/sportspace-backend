package com.example.sportspace.entity;
import jakarta.persistence.*;
@Entity
public class Sport {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String name;
    public Sport() {}
    public Sport(String name) { this.name = name; }
    public Long getId() { return id; } public String getName() { return name; } public void setName(String n) { name = n; }
}
