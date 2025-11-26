package com.example.admin.service;

import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.repository.KelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
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
}