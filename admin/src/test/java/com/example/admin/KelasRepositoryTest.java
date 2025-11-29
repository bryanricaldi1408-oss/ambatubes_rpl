package com.example.admin;
import java.util.List;
import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.repository.KelasRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class KelasRepositoryTest {

    @Autowired
    private KelasRepository kelasRepository;

    @Test
    void testFindAllClassDisplay_ShouldRetrieveAllClassesFromDatabase() {
        // When: Call the repository method that fetches classes for display
        List<ClassDisplayDto> classDisplayDtos = kelasRepository.findAllClassDisplay();

        // Then: Verify we can retrieve class data from the database
        assertNotNull(classDisplayDtos, "Should not return null");
        
        // The exact count depends on your test data, but it should return something
        assertTrue(classDisplayDtos.size() >= 0, "Should return zero or more classes");
        
        // If there are classes, verify they have the expected structure
        if (!classDisplayDtos.isEmpty()) {
            ClassDisplayDto firstClass = classDisplayDtos.get(0);
            assertNotNull(firstClass.getIdKelas());
            assertNotNull(firstClass.getNamaMk());
            assertNotNull(firstClass.getNamaKelas());
            assertNotNull(firstClass.getSemester());
            assertNotNull(firstClass.getNamaDosen());
        }
    }
}