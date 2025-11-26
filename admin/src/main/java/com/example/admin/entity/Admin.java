package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@Data
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String password;
}