package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "nilai_kelompok")
@Data
public class NilaiKelompok {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnilaikelompok")
    private Integer idNilaiKelompok;
    
    @Column(name = "nilai")
    private Double nilai;
    
    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;
    
    @Column(name = "idkelompok")
    private Integer idKelompok;
    
    @Column(name = "idkegiatan")
    private Integer idKegiatan;
    

    // relasi ke Kelompok (Many-to-One)
    // Pastikan Entity Kelompok sudah ada, jika belum, buat dummy dulu atau hapus bagian ini
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idkelompok", insertable = false, updatable = false)
    @ToString.Exclude
    private Kelompok kelompok;
    
    // relasi ke Kegiatan (Many-to-One)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idkegiatan", insertable = false, updatable = false)
    @ToString.Exclude
    private Kegiatan kegiatan;
}