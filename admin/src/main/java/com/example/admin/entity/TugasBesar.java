package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tugas_besar")
@Data
public class TugasBesar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtubes")
    private Integer idTubes;

    @Column(name = "nama_tugas")
    private String namaTugas;

    @Column(name = "deskripsi", columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "tanggal_dibuat")
    private LocalDate tanggalDibuat;

    @Column(name = "idkelas")
    private Integer idKelas;

    // Relasi balik ke Kelas (Opsional tapi bagus untuk data integrity)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idkelas", insertable = false, updatable = false)
    private Kelas kelas;
}