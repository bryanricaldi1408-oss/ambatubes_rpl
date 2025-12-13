package com.example.admin.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; // Sesuaikan dengan tipe data deadline di DB

@Data
@AllArgsConstructor
public class JadwalNilaiDto {
    private Integer idKegiatan;     // id Kegiatan
    private LocalDateTime deadline; // Dari tabel Jadwal
    private String namaKegiatan;    // Dari tabel Kegiatan
    private Double nilai;           // Dari tabel Nilai_Mahasiswa
    private String keterangan;      // Dari tabel Nilai_Mahasiswa
}