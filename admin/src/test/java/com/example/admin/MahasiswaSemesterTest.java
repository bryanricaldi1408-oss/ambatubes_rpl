package com.example.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.Kelas;

import java.util.List;
import java.util.stream.Collectors;
import com.example.admin.service.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class MahasiswaSemesterTest {
    
    @Autowired
    private MahasiswaService mahasiswaService;
    
    @Test
    public void testMahasiswaUniqueSemesters() {
        String npm = "61823011";
        
        Mahasiswa mahasiswa = mahasiswaService.findMahasiswaByNpm(npm);
        assertNotNull(mahasiswa, "Mahasiswa should not be null");
        
        List<Kelas> mahasiswaKelas = mahasiswa.getListKelas();
        List<String> uniqueSemesters = mahasiswaKelas.stream()
                .map(Kelas::getSemester)
                .distinct()
                .collect(Collectors.toList());
        
        assertNotNull(mahasiswaKelas, "Mahasiswa kelas list should not be null");
        assertNotNull(uniqueSemesters, "Semester list should not be null");
        
        assertTrue(uniqueSemesters.contains("Ganjil 2025/2026"), "Should contain Ganjil 2025/2026");
        
        assertEquals(1, uniqueSemesters.size(), "Should have exactly 1 unique semester");
        
        assertFalse(uniqueSemesters.contains("Genap 2025/2026"), "Should NOT contain Genap 2025/2026");
        assertFalse(uniqueSemesters.contains("Pendek 2025/2026"), "Should NOT contain Pendek 2025/2026");
        assertFalse(uniqueSemesters.contains("Ganjil 1945/1946"), "Should NOT contain Ganjil 1945/1946");
        assertFalse(uniqueSemesters.contains("Ganjil 1000/1001"), "Should NOT contain Ganjil 1000/1001");
    }
}