package com.example.admin.dto;

import lombok.Data;

@Data
public class TambahMahasiswaDto {
    private String npm;
    private String namaLengkap;
    
    // You can add validation annotations here later
    // @NotBlank, @Size, etc.
}