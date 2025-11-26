package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "kelas")
@Data
public class Kelas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idkelas")
    private Integer idKelas;
    
    @Column(name = "nama_kelas")
    private String namaKelas;
    
    @Column(name = "semester")
    private String semester;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "kode_mk", referencedColumnName = "kode_mk")
    private MataKuliah mataKuliah;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nik", referencedColumnName = "nik")
    private Dosen dosen;
    
    @OneToMany(mappedBy = "kelas", fetch = FetchType.LAZY)
    private List<PengajaranKelas> pengajaranKelasList;
    
    @OneToMany(mappedBy = "kelas", fetch = FetchType.LAZY)
    private List<PengambilanKelas> pengambilanKelasList;
}