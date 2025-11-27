package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "mahasiswacredentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MahasiswaCredentials {
    @Id
    @Column(name = "npm")
    private String npm;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password")
    private String password;
}