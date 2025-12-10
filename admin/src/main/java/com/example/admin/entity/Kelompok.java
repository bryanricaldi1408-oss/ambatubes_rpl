package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "kelompok")
@Data
public class Kelompok {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idkelompok")
    private Integer idKelompok;
    
    @Column(name = "nama_kelompok", columnDefinition = "CHAR(1)")
    private String namaKelompok;
    
    @Column(name = "jumlah_anggota")
    private Integer jumlahAnggota;
    
    @Column(name = "idtubes")
    private Integer idTubes;
    
    // Relasi ke Tugas Besar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtubes", insertable = false, updatable = false)
    @ToString.Exclude
    private TugasBesar tugasBesar;
}