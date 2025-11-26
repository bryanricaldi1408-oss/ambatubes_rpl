package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "mahasiswa")
@Data
public class Mahasiswa {
    @Id
    @Column(name = "npm")
    private String npm;
    
    @Column(name = "nama")
    private String nama;
}