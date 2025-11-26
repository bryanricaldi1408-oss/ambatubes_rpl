package com.example.admin.service;

import com.example.admin.dto.ExcelImportData;
import com.example.admin.dto.MataKuliahSheet;
import com.example.admin.dto.KelasSheet;
import com.example.admin.dto.DosenAssignmentSheet;
import com.example.admin.dto.MahasiswaEnrollmentSheet;
import com.example.admin.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class DatabaseImportService {

    @Autowired
    private MataKuliahService mataKuliahService;
    
    @Autowired
    private DosenService dosenService;
    
    @Autowired
    private MahasiswaService mahasiswaService;
    
    @Autowired
    private KelasService kelasService;
    
    @Autowired
    private PengajaranKelasService pengajaranKelasService;
    
    @Autowired
    private PengambilanKelasService pengambilanKelasService;

    @Transactional
    public ImportResult importExcelData(ExcelImportData excelData) {
        ImportResult result = new ImportResult();
        
        try {
            // Step 1: Import Mata Kuliah
            importMataKuliah(excelData.getMataKuliahList(), result);
            
            // Step 2: Import Dosen (from assignments)
            importDosenFromAssignments(excelData.getDosenAssignments(), result);
            
            // Step 3: Import Kelas with proper duplicate detection
            Map<String, Integer> kelasIdMap = importKelasWithDuplicateCheck(excelData.getKelasList(), result);
            
            // Step 4: Import Dosen Assignments with proper duplicate detection
            importDosenAssignmentsWithDuplicateCheck(excelData.getDosenAssignments(), kelasIdMap, result);
            
            // Step 5: Import Mahasiswa
            importMahasiswaFromEnrollments(excelData.getMahasiswaEnrollments(), result);
            
            // Step 6: Import Mahasiswa Enrollments with proper duplicate detection
            importMahasiswaEnrollmentsWithDuplicateCheck(excelData.getMahasiswaEnrollments(), kelasIdMap, result);
            
            result.setSuccess(true);
            result.setMessage("Data imported successfully");
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Import failed: " + e.getMessage());
            throw new RuntimeException("Import failed", e);
        }
        
        return result;
    }

    private void importMataKuliah(List<MataKuliahSheet> mataKuliahList, ImportResult result) {
        int imported = 0;
        int skipped = 0;
        
        for (MataKuliahSheet sheetData : mataKuliahList) {
            if (!mataKuliahService.existsByKodeMk(sheetData.getKodeMk())) {
                MataKuliah mataKuliah = new MataKuliah();
                mataKuliah.setKodeMk(sheetData.getKodeMk());
                mataKuliah.setNamaMk(sheetData.getNamaMk());
                mataKuliahService.saveMataKuliah(mataKuliah);
                imported++;
            } else {
                skipped++;
            }
        }
        
        result.getMataKuliahStats().put("imported", imported);
        result.getMataKuliahStats().put("skipped", skipped);
    }

    private void importDosenFromAssignments(List<DosenAssignmentSheet> assignments, ImportResult result) {
        Set<String> processedNiks = new HashSet<>();
        int imported = 0;
        int skipped = 0;
        
        for (DosenAssignmentSheet assignment : assignments) {
            if (!processedNiks.contains(assignment.getNik()) && !dosenService.existsByNik(assignment.getNik())) {
                Dosen dosen = new Dosen();
                dosen.setNik(assignment.getNik());
                dosen.setNama(assignment.getNamaDosen());
                dosenService.saveDosen(dosen);
                imported++;
                processedNiks.add(assignment.getNik());
            } else {
                skipped++;
            }
        }
        
        result.getDosenStats().put("imported", imported);
        result.getDosenStats().put("skipped", skipped);
    }

    private Map<String, Integer> importKelasWithDuplicateCheck(List<KelasSheet> kelasList, ImportResult result) {
        Map<String, Integer> kelasIdMap = new HashMap<>();
        int imported = 0;
        int skipped = 0;
        
        for (KelasSheet sheetData : kelasList) {
            String kelasKey = generateKelasKey(sheetData.getKodeMk(), sheetData.getNamaKelas(), sheetData.getSemester());
            
            // Skip if we've already processed this key in current import
            if (kelasIdMap.containsKey(kelasKey)) {
                skipped++;
                continue;
            }
            
            // Check if kelas already exists in database by composite key
            Optional<Integer> existingIdKelas = kelasService.findIdKelasByCompositeKey(
                sheetData.getKodeMk(), sheetData.getNamaKelas(), sheetData.getSemester());
            
            if (existingIdKelas.isPresent()) {
                // Use existing kelas ID
                kelasIdMap.put(kelasKey, existingIdKelas.get());
                skipped++;
            } else {
                // Create new kelas
                Optional<MataKuliah> mataKuliah = mataKuliahService.findMataKuliahByKode(sheetData.getKodeMk());
                Optional<Dosen> dosen = dosenService.findDosenByNik(sheetData.getNikPrimary());
                
                if (mataKuliah.isPresent() && dosen.isPresent()) {
                    Kelas kelas = new Kelas();
                    kelas.setNamaKelas(sheetData.getNamaKelas());
                    kelas.setSemester(sheetData.getSemester());
                    kelas.setMataKuliah(mataKuliah.get());
                    kelas.setDosen(dosen.get());
                    
                    Kelas savedKelas = kelasService.saveKelas(kelas);
                    kelasIdMap.put(kelasKey, savedKelas.getIdKelas());
                    imported++;
                } else {
                    // Log error: missing reference data
                    skipped++;
                }
            }
        }
        
        result.getKelasStats().put("imported", imported);
        result.getKelasStats().put("skipped", skipped);
        
        return kelasIdMap;
    }

    private void importDosenAssignmentsWithDuplicateCheck(List<DosenAssignmentSheet> assignments, 
                                                         Map<String, Integer> kelasIdMap, 
                                                         ImportResult result) {
        int imported = 0;
        int skipped = 0;
        
        for (DosenAssignmentSheet assignment : assignments) {
            String kelasKey = generateKelasKey(assignment.getKodeMk(), assignment.getNamaKelas(), assignment.getSemester());
            Integer idKelas = kelasIdMap.get(kelasKey);
            
            if (idKelas != null) {
                // Check if this teaching assignment already exists in database
                boolean alreadyExists = pengajaranKelasService.existsByCompositeKey(
                    assignment.getNik(), assignment.getKodeMk(), assignment.getNamaKelas(), assignment.getSemester());
                
                if (!alreadyExists) {
                    PengajaranKelas pengajaranKelas = new PengajaranKelas();
                    pengajaranKelas.setNik(assignment.getNik());
                    pengajaranKelas.setIdKelas(idKelas);
                    pengajaranKelasService.save(pengajaranKelas);
                    imported++;
                } else {
                    skipped++;
                }
            } else {
                skipped++;
            }
        }
        
        result.getPengajaranStats().put("imported", imported);
        result.getPengajaranStats().put("skipped", skipped);
    }

    private void importMahasiswaFromEnrollments(List<MahasiswaEnrollmentSheet> enrollments, ImportResult result) {
        Set<String> processedNpms = new HashSet<>();
        int imported = 0;
        int skipped = 0;
        
        for (MahasiswaEnrollmentSheet enrollment : enrollments) {
            if (!processedNpms.contains(enrollment.getNpm()) && !mahasiswaService.existsByNpm(enrollment.getNpm())) {
                Mahasiswa mahasiswa = new Mahasiswa();
                mahasiswa.setNpm(enrollment.getNpm());
                mahasiswa.setNama(enrollment.getNamaMahasiswa());
                mahasiswaService.saveMahasiswa(mahasiswa);
                imported++;
                processedNpms.add(enrollment.getNpm());
            } else {
                skipped++;
            }
        }
        
        result.getMahasiswaStats().put("imported", imported);
        result.getMahasiswaStats().put("skipped", skipped);
    }

    private void importMahasiswaEnrollmentsWithDuplicateCheck(List<MahasiswaEnrollmentSheet> enrollments, 
                                                             Map<String, Integer> kelasIdMap, 
                                                             ImportResult result) {
        int imported = 0;
        int skipped = 0;
        
        for (MahasiswaEnrollmentSheet enrollment : enrollments) {
            String kelasKey = generateKelasKey(enrollment.getKodeMk(), enrollment.getNamaKelas(), enrollment.getSemester());
            Integer idKelas = kelasIdMap.get(kelasKey);
            
            if (idKelas != null) {
                // Check if this enrollment already exists in database
                boolean alreadyExists = pengambilanKelasService.existsByCompositeKey(
                    enrollment.getNpm(), enrollment.getKodeMk(), enrollment.getNamaKelas(), enrollment.getSemester());
                
                if (!alreadyExists) {
                    PengambilanKelas pengambilanKelas = new PengambilanKelas();
                    pengambilanKelas.setNpm(enrollment.getNpm());
                    pengambilanKelas.setIdKelas(idKelas);
                    pengambilanKelasService.save(pengambilanKelas);
                    imported++;
                } else {
                    skipped++;
                }
            } else {
                skipped++;
            }
        }
        
        result.getPengambilanStats().put("imported", imported);
        result.getPengambilanStats().put("skipped", skipped);
    }

    private String generateKelasKey(String kodeMk, String namaKelas, String semester) {
        return kodeMk + "|" + namaKelas + "|" + semester;
    }
}