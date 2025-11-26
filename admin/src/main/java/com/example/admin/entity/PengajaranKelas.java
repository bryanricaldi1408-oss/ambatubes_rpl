package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "pengajaran_kelas")
@Data
public class PengajaranKelas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pengajaran")
    private Integer idPengajaran;
    
    @Column(name = "nik")
    private String nik;
    
    @Column(name = "idkelas")
    private Integer idKelas;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "nik", referencedColumnName = "nik", insertable = false, updatable = false)
    private Dosen dosen;
}