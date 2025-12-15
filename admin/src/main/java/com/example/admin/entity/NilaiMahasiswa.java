package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "nilai_mahasiswa")
@Data
public class NilaiMahasiswa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnilaimahasiswa")
    private Integer idNilaiMahasiswa;
    
    @Column(name = "nilai")
    private Double nilai;
    
    @Column(name = "keterangan", columnDefinition = "TEXT")
    private String keterangan;
    
    @Column(name = "idnilaikelompok")
    private Integer idNilaiKelompok;
    
    @Column(name = "npm")
    private String npm;
    
    // relasi ke Nilai Kelompok (Many-to-One)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idnilaikelompok", insertable = false, updatable = false)
    @ToString.Exclude
    private NilaiKelompok nilaiKelompok;
    
    // relasi ke Mahasiswa (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "npm", insertable = false, updatable = false)
    @ToString.Exclude
    private Mahasiswa mahasiswa;
}