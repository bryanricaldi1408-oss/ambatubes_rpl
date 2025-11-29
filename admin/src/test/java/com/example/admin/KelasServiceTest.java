package com.example.admin;

import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.repository.KelasRepository;
import com.example.admin.service.KelasService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KelasServiceTest {

    @Mock
    private KelasRepository kelasRepository;

    @InjectMocks
    private KelasService kelasService;

    @Test
    void testGetAllClassesForDisplay_ShouldReturnAllClassesFromDatabase() {
        // Given: Mock the exact 6 classes from your database
        List<ClassDisplayDto> expectedClasses = Arrays.asList(
            new ClassDisplayDto(1, "Operating System", "A", "Ganjil 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(2, "Operating System", "B", "Genap 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(3, "Operating System", "C", "Pendek 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(4, "Artifical Intelligence", "A", "Ganjil 2025/2026", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(5, "Artifical Intelligence", "B", "Ganjil 1945/1946", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(6, "Desain Antar Grafis", "A", "Ganjil 1000/1001", "Elisati Hulu")
        );

        when(kelasRepository.findAllClassDisplay()).thenReturn(expectedClasses);

        // When
        List<ClassDisplayDto> actualClasses = kelasService.getAllClassesForDisplay();

        // Then: Verify all 6 classes are returned with correct data
        assertNotNull(actualClasses);
        assertEquals(6, actualClasses.size(), "Should return all 6 classes from database");
        
        // Verify specific class data matches database
        assertEquals("Operating System", actualClasses.get(0).getNamaMk());
        assertEquals("A", actualClasses.get(0).getNamaKelas());
        assertEquals("Ganjil 2025/2026", actualClasses.get(0).getSemester());
        assertEquals("Aditya Bagoes Saputra", actualClasses.get(0).getNamaDosen());
        
        verify(kelasRepository, times(1)).findAllClassDisplay();
    }

    @Test
    void testGetUniqueSemesters_ShouldExtractAndSortUniqueSemesters() {
        // Given: Mock classes with various semesters (including duplicates)
        List<ClassDisplayDto> mockClasses = Arrays.asList(
            new ClassDisplayDto(1, "Operating System", "A", "Ganjil 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(2, "Operating System", "B", "Genap 2025/2026", "Aditya Bagoes Saputra"),
            new ClassDisplayDto(3, "Operating System", "C", "Ganjil 2025/2026", "Aditya Bagoes Saputra"), // Duplicate semester
            new ClassDisplayDto(4, "Artifical Intelligence", "A", "Ganjil 1945/1946", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(5, "Artifical Intelligence", "B", "Ganjil 1000/1001", "Cecilia Esti Nugraheni"),
            new ClassDisplayDto(6, "Desain Antar Grafis", "A", "Ganjil 1000/1001", "Elisati Hulu") // Duplicate semester
        );

        when(kelasRepository.findAllClassDisplay()).thenReturn(mockClasses);

        // When
        List<String> uniqueSemesters = kelasService.getUniqueSemesters();

        // Then: Verify unique extraction and alphabetical sorting
        assertNotNull(uniqueSemesters);
        assertEquals(4, uniqueSemesters.size(), "Should return 4 unique semesters from 6 classes");
        
        // Verify they are sorted alphabetically
        assertEquals("Ganjil 1000/1001", uniqueSemesters.get(0));
        assertEquals("Ganjil 1945/1946", uniqueSemesters.get(1));
        assertEquals("Ganjil 2025/2026", uniqueSemesters.get(2));
        assertEquals("Genap 2025/2026", uniqueSemesters.get(3));
    }
}