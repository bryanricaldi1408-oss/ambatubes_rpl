package com.example.admin.dto;

import lombok.Data;

@Data
public class TambahDosenDto {
    private String nik;
    private String namaLengkap;
    
    // You can add validation annotations here later
    // @NotBlank, @Size, etc.
}