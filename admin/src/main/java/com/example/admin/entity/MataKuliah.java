package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "mata_kuliah")
@Data
public class MataKuliah {
    @Id
    @Column(name = "kode_mk")
    private String kodeMk;
    
    @Column(name = "nama_mk")
    private String namaMk;
}