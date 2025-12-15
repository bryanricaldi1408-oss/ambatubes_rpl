package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "anggota_kelompok")
@Data
public class AnggotaKelompok {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_anggota")
    private Integer idAnggota;

    @Column(name = "idkelompok")
    private Integer idKelompok;

    @Column(name = "npm")
    private String npm;

    // relasi ke Entity Kelompok (Many-to-One)
    // buat cek kapasitas & idTubes
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idkelompok", insertable = false, updatable = false)
    @ToString.Exclude
    private Kelompok kelompok;

    // relasi ke Entity Mahasiswa (Many-to-One)
    // Digunakan untuk menampilkan nama anggota di Modal Show
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "npm", insertable = false, updatable = false)
    @ToString.Exclude
    private Mahasiswa mahasiswa;
}