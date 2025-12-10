package com.example.admin.service;

import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.entity.Kelas;
import com.example.admin.repository.KelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KelasService {
    
    @Autowired
    private KelasRepository kelasRepository;
    
    public List<ClassDisplayDto> getAllClassesForDisplay() {
        return kelasRepository.findAllClassDisplay();
    }
    
    public List<String> getUniqueSemesters() {
        List<ClassDisplayDto> classes = getAllClassesForDisplay();
        return classes.stream()
                .map(ClassDisplayDto::getSemester)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public ClassDisplayDto getClassById(Integer classId) {
        return kelasRepository.findById(classId)
                .map(kelas -> new ClassDisplayDto(
                    kelas.getIdKelas(),
                    kelas.getMataKuliah().getNamaMk(),
                    kelas.getNamaKelas(),
                    kelas.getSemester(),
                    kelas.getDosen().getNama()
                ))
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));
    }

    public Kelas saveKelas(Kelas kelas) {
    return kelasRepository.save(kelas);
}

    public Optional<Kelas> findKelasByCompositeKey(String kodeMk, String namaKelas, String semester) {
        return kelasRepository.findByCompositeKey(kodeMk, namaKelas, semester);
    }

    public boolean existsByCompositeKey(String kodeMk, String namaKelas, String semester) {
        return kelasRepository.findByCompositeKey(kodeMk, namaKelas, semester).isPresent();
    }
    
    public Optional<Kelas> findByCompositeKey(String kodeMk, String namaKelas, String semester) {
        return kelasRepository.findByCompositeKey(kodeMk, namaKelas, semester);
    }
    
    public Optional<Integer> findIdKelasByCompositeKey(String kodeMk, String namaKelas, String semester) {
        return kelasRepository.findIdKelasByCompositeKey(kodeMk, namaKelas, semester);
    }
}