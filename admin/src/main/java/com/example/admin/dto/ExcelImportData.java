package com.example.admin.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExcelImportData {
    private List<MataKuliahSheet> mataKuliahList = new ArrayList<>();
    private List<KelasSheet> kelasList = new ArrayList<>();
    private List<DosenAssignmentSheet> dosenAssignments = new ArrayList<>();
    private List<MahasiswaEnrollmentSheet> mahasiswaEnrollments = new ArrayList<>();
}