package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "DosenCredentials")
@Data 
@NoArgsConstructor 
@AllArgsConstructor
public class DosenCredentials {
    
    @Id
    @Column(name = "NIK", length = 20)
    private String nik;
    
    @Column(name = "Email", length = 100)
    private String email;
    
    @Column(name = "Password", length = 20)
    private String password;
}