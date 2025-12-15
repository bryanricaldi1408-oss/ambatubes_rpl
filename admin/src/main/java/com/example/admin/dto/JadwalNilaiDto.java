package com.example.admin.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime; 

@Data
@AllArgsConstructor
public class JadwalNilaiDto {
    private Integer idKegiatan;  
    private LocalDateTime deadline; 
    private String namaKegiatan;  
    private Double nilai;         
    private String keterangan;
}