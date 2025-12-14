package com.example.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.admin.entity.Kelas;
import com.example.admin.service.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class DosenSemesterTest {
    
    @Autowired
    private DosenService dosenService;
    
    @Test
    public void testDosenUniqueSemesters() {
        String nik = "19800001";
        
        List<Kelas> dosenKelas = dosenService.getKelasByDosenNik(nik);
        List<String> uniqueSemesters = dosenKelas.stream()
                .map(Kelas::getSemester)
                .distinct()
                .collect(Collectors.toList());
        
        assertNotNull(dosenKelas, "Dosen kelas list should not be null");
        assertNotNull(uniqueSemesters, "Semester list should not be null");
        
        assertTrue(uniqueSemesters.contains("Ganjil 2025/2026"), "Should contain Ganjil 2025/2026");
        assertTrue(uniqueSemesters.contains("Genap 2025/2026"), "Should contain Genap 2025/2026");
        assertTrue(uniqueSemesters.contains("Pendek 2025/2026"), "Should contain Pendek 2025/2026");
        
        assertEquals(3, uniqueSemesters.size(), "Should have exactly 3 unique semesters");

        assertFalse(uniqueSemesters.contains("Ganjil 1945/1946"), "Should NOT contain Ganjil 1945/1946");
        assertFalse(uniqueSemesters.contains("Ganjil 1000/1001"), "Should NOT contain Ganjil 1000/1001");
    }
}