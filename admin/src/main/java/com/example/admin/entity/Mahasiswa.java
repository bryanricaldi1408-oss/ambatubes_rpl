package com.example.admin.entity;

import lombok.Data;

import java.util.List;

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

    @ManyToMany(fetch = FetchType.EAGER) 
    @JoinTable(
        name = "pengambilan_kelas", 
        joinColumns = @JoinColumn(name = "npm"), 
        inverseJoinColumns = @JoinColumn(name = "idkelas") 
    )
    private List<Kelas> listKelas;
}