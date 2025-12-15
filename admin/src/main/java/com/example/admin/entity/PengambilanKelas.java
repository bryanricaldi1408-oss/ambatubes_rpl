package com.example.admin.entity;

import lombok.Data;
import jakarta.persistence.*;

@Entity
@Table(name = "pengambilan_kelas")
@Data
public class PengambilanKelas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pengambilan")
    private Integer idPengambilan;
    
    @Column(name = "npm")
    private String npm;
    
    @Column(name = "idkelas")
    private Integer idKelas;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "npm", referencedColumnName = "npm", insertable = false, updatable = false)
    private Mahasiswa mahasiswa;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idkelas", referencedColumnName = "idkelas", insertable = false, updatable = false)
    private Kelas kelas;
}