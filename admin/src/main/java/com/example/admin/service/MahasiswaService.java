package com.example.admin.service;

import com.example.admin.entity.Mahasiswa;
import com.example.admin.repository.MahasiswaRepository;
import com.example.admin.repository.PengambilanKelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MahasiswaService {

    @Autowired
    private MahasiswaRepository mahasiswaRepository;
    
    @Autowired
    private PengambilanKelasRepository pengambilanKelasRepository;

    public List<Mahasiswa> getAllMahasiswa() {
        return mahasiswaRepository.findAll();
    }
    
    public List<Mahasiswa> getMahasiswaByKelas(Integer idKelas) {
        return pengambilanKelasRepository.findByKelasWithMahasiswa(idKelas)
                .stream()
                .map(pk -> pk.getMahasiswa())
                .collect(Collectors.toList());
    }

    // New methods for adding mahasiswa
    public Mahasiswa saveMahasiswa(Mahasiswa mahasiswa) {
        return mahasiswaRepository.save(mahasiswa);
    }
    
    public Optional<Mahasiswa> findMahasiswaByNpm(String npm) {
        return mahasiswaRepository.findById(npm);
    }
    
    public boolean existsByNpm(String npm) {
        return mahasiswaRepository.existsById(npm);
    }
}