package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "kegiatan")
@Data
public class Kegiatan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idkegiatan")
    private Integer idKegiatan;

    @Column(name = "nama_kegiatan")
    private String namaKegiatan;

    @Column(name = "idjadwal")
    private Integer idJadwal;

    // relasi ke Jadwal
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idjadwal", insertable = false, updatable = false)
    @ToString.Exclude
    private Jadwal jadwal;
}