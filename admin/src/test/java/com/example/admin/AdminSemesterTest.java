package com.example.admin;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import com.example.admin.service.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class AdminSemesterTest {
    
    @Autowired
    private KelasService kelasService;
    
    @Test
    public void testAdminUniqueSemesters() {
        List<String> uniqueSemesters = kelasService.getUniqueSemesters();
        
        assertNotNull(uniqueSemesters, "Semester list should not be null");
        assertFalse(uniqueSemesters.isEmpty(), "Semester list should not be empty");
        
        assertTrue(uniqueSemesters.contains("Ganjil 2025/2026"), "Should contain Ganjil 2025/2026");
        assertTrue(uniqueSemesters.contains("Genap 2025/2026"), "Should contain Genap 2025/2026");
        assertTrue(uniqueSemesters.contains("Pendek 2025/2026"), "Should contain Pendek 2025/2026");
        assertTrue(uniqueSemesters.contains("Ganjil 1945/1946"), "Should contain Ganjil 1945/1946");
        assertTrue(uniqueSemesters.contains("Ganjil 1000/1001"), "Should contain Ganjil 1000/1001");
        
        assertEquals(5, uniqueSemesters.size(), "Should have exactly 5 unique semesters");
    }
}