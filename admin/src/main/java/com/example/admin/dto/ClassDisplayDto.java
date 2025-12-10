package com.example.admin.dto;

import lombok.Data;

@Data
public class ClassDisplayDto {
    private Integer idKelas;
    private String namaMk;
    private String namaKelas;
    private String semester;
    private String namaDosen;
    
    public ClassDisplayDto(Integer idKelas, String namaMk, String namaKelas, String semester, String namaDosen) {
        this.idKelas = idKelas;
        this.namaMk = namaMk;
        this.namaKelas = namaKelas;
        this.semester = semester;
        this.namaDosen = namaDosen;
    }
}