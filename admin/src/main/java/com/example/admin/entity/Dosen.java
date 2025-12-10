package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "dosen")
@Data
public class Dosen {
    @Id
    @Column(name = "nik")
    private String nik;
    
    @Column(name = "nama")
    private String nama;
}