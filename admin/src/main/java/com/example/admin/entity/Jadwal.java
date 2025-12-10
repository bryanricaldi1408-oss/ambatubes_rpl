package com.example.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDateTime;

@Entity
@Table(name = "jadwal")
@Data
public class Jadwal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idjadwal")
    private Integer idJadwal;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "idtubes")
    private Integer idTubes;

    // Relasi ke Tugas Besar
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtubes", insertable = false, updatable = false)
    @ToString.Exclude
    private TugasBesar tugasBesar;
}