package com.example.admin;

import com.example.admin.controller.AdminController;
import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.service.KelasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimpleAdminControllerTest {

    @Mock
    private KelasService kelasService;

    @Mock
    private Model model;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAdminHome_ShouldDisplayAllClassesAndUniqueSemesters() {
        // Given: Mock the exact data structure from your database
        List<ClassDisplayDto> mockClasses = Arrays.asList(
            new ClassDisplayDto(1, "Operating System", "A", "Ganjil 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(2, "Operating System", "B", "Genap 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(3, "Operating System", "C", "Pendek 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(4, "Artifical Intelligence", "A", "Ganjil 2025/2026", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(5, "Artifical Intelligence", "B", "Ganjil 1945/1946", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(6, "Desain Antar Grafis", "A", "Ganjil 1000/1001", "Elisati Hulu")
        );

        List<String> mockSemesters = Arrays.asList(
            "Ganjil 1000/1001", "Ganjil 1945/1946", "Ganjil 2025/2026", "Genap 2025/2026", "Pendek 2025/2026"
        );

        when(kelasService.getAllClassesForDisplay()).thenReturn(mockClasses);
        when(kelasService.getUniqueSemesters()).thenReturn(mockSemesters);

        // When
        String viewName = adminController.showAdminHome(model);

        // Then: Verify all data flows correctly to the view
        assertEquals("adminHome", viewName);
        
        // Verify all 6 classes are passed to the view
        verify(model, times(1)).addAttribute("classes", mockClasses);
        
        // Verify unique sorted semesters are passed to the view
        verify(model, times(1)).addAttribute("uniqueSemesters", mockSemesters);
        
        // Verify service methods are called to retrieve data
        verify(kelasService, times(1)).getAllClassesForDisplay();
        verify(kelasService, times(1)).getUniqueSemesters();
    }
}