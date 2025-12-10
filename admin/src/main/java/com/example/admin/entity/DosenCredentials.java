package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dosencredentials")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class DosenCredentials {
    
    @Id
    @Column(name = "nik", length = 20)
    private String nik;
    
    @Column(name = "email", length = 100)
    private String email;
    
    @Column(name = "password", length = 20)
    private String password;
}