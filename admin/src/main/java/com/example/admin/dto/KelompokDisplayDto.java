package com.example.admin.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class KelompokDisplayDto {
    private Integer idKelompok;
    private String namaKelompok;
    private int jumlahSaatIni;
    private int kapasitasMax;
    private boolean isSelected; //True lo mahasiswa ada di klompok ini
}