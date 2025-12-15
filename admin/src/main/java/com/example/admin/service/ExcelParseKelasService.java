package com.example.admin.service;

import com.example.admin.dto.ExcelImportData;
import com.example.admin.dto.MataKuliahSheet;
import com.example.admin.dto.KelasSheet;
import com.example.admin.dto.DosenAssignmentSheet;
import com.example.admin.dto.MahasiswaEnrollmentSheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelParseKelasService {

    public ExcelImportData parseExcelFile(MultipartFile file) throws IOException {
        ExcelImportData importData = new ExcelImportData();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            // Parse each sheet
            importData.setMataKuliahList(parseMataKuliahSheet(workbook));
            importData.setKelasList(parseKelasSheet(workbook));
            importData.setDosenAssignments(parseDosenAssignmentsSheet(workbook));
            importData.setMahasiswaEnrollments(parseMahasiswaEnrollmentsSheet(workbook));
        }
        
        return importData;
    }

    private List<MataKuliahSheet> parseMataKuliahSheet(Workbook workbook) {
        List<MataKuliahSheet> mataKuliahList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("Mata_Kuliah_Data");
        
        if (sheet == null) {
            throw new RuntimeException("Sheet 'Mata_Kuliah_Data' not found");
        }
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row)) continue;
            
            MataKuliahSheet mataKuliah = new MataKuliahSheet();
            mataKuliah.setKodeMk(getCellStringValue(row.getCell(0))); // Kode_MK
            mataKuliah.setNamaMk(getCellStringValue(row.getCell(1))); // Nama_MK
            
            if (mataKuliah.getKodeMk() != null && !mataKuliah.getKodeMk().trim().isEmpty()) {
                mataKuliahList.add(mataKuliah);
            }
        }
        
        return mataKuliahList;
    }

    private List<KelasSheet> parseKelasSheet(Workbook workbook) {
        List<KelasSheet> kelasList = new ArrayList<>();
        Sheet sheet = workbook.getSheet("Kelas_Data");
        
        if (sheet == null) {
            throw new RuntimeException("Sheet 'Kelas_Data' not found");
        }
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row)) continue;
            
            KelasSheet kelas = new KelasSheet();
            kelas.setKodeMk(getCellStringValue(row.getCell(0))); // Kode_MK
            kelas.setNamaKelas(getCellStringValue(row.getCell(1))); // Nama_Kelas
            kelas.setSemester(getCellStringValue(row.getCell(2))); // Semester
            kelas.setNikPrimary(getCellStringValue(row.getCell(3))); // NIK_Primary
            
            if (isValidKelasData(kelas)) {
                kelasList.add(kelas);
            }
        }
        
        return kelasList;
    }

    private List<DosenAssignmentSheet> parseDosenAssignmentsSheet(Workbook workbook) {
        List<DosenAssignmentSheet> assignments = new ArrayList<>();
        Sheet sheet = workbook.getSheet("Dosen_Assignments");
        
        if (sheet == null) {
            throw new RuntimeException("Sheet 'Dosen_Assignments' not found");
        }
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row)) continue;
            
            DosenAssignmentSheet assignment = new DosenAssignmentSheet();
            assignment.setKodeMk(getCellStringValue(row.getCell(0))); // Kode_MK
            assignment.setNamaKelas(getCellStringValue(row.getCell(1))); // Nama_Kelas
            assignment.setSemester(getCellStringValue(row.getCell(2))); // Semester
            assignment.setNik(getCellStringValue(row.getCell(3))); // NIK
            assignment.setNamaDosen(getCellStringValue(row.getCell(4))); // Nama_Dosen
            
            if (isValidDosenAssignment(assignment)) {
                assignments.add(assignment);
            }
        }
        
        return assignments;
    }

    private List<MahasiswaEnrollmentSheet> parseMahasiswaEnrollmentsSheet(Workbook workbook) {
        List<MahasiswaEnrollmentSheet> enrollments = new ArrayList<>();
        Sheet sheet = workbook.getSheet("Mahasiswa_Enrollments");
        
        if (sheet == null) {
            throw new RuntimeException("Sheet 'Mahasiswa_Enrollments' not found");
        }
        
        Iterator<Row> rowIterator = sheet.iterator();
        
        // Skip header row
        if (rowIterator.hasNext()) {
            rowIterator.next();
        }
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (isRowEmpty(row)) continue;
            
            MahasiswaEnrollmentSheet enrollment = new MahasiswaEnrollmentSheet();
            enrollment.setKodeMk(getCellStringValue(row.getCell(0))); // Kode_MK
            enrollment.setNamaKelas(getCellStringValue(row.getCell(1))); // Nama_Kelas
            enrollment.setSemester(getCellStringValue(row.getCell(2))); // Semester
            enrollment.setNpm(getCellStringValue(row.getCell(3))); // NPM
            enrollment.setNamaMahasiswa(getCellStringValue(row.getCell(4))); // Nama_Mahasiswa
            
            if (isValidMahasiswaEnrollment(enrollment)) {
                enrollments.add(enrollment);
            }
        }
        
        return enrollments;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    //harusnya g kepake krn semua keisi
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && 
                getCellStringValue(cell) != null && !getCellStringValue(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidKelasData(KelasSheet kelas) {
        return kelas.getKodeMk() != null && !kelas.getKodeMk().isEmpty() &&
               kelas.getNamaKelas() != null && !kelas.getNamaKelas().isEmpty() &&
               kelas.getSemester() != null && !kelas.getSemester().isEmpty();
    }

    private boolean isValidDosenAssignment(DosenAssignmentSheet assignment) {
        return assignment.getKodeMk() != null && !assignment.getKodeMk().isEmpty() &&
               assignment.getNamaKelas() != null && !assignment.getNamaKelas().isEmpty() &&
               assignment.getSemester() != null && !assignment.getSemester().isEmpty() &&
               assignment.getNik() != null && !assignment.getNik().isEmpty();
    }

    private boolean isValidMahasiswaEnrollment(MahasiswaEnrollmentSheet enrollment) {
        return enrollment.getKodeMk() != null && !enrollment.getKodeMk().isEmpty() &&
               enrollment.getNamaKelas() != null && !enrollment.getNamaKelas().isEmpty() &&
               enrollment.getSemester() != null && !enrollment.getSemester().isEmpty() &&
               enrollment.getNpm() != null && !enrollment.getNpm().isEmpty();
    }
}