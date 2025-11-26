package com.example.admin.service;

import com.example.admin.entity.Dosen;
import com.example.admin.repository.DosenRepository;
import com.example.admin.repository.PengajaranKelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DosenService {

    @Autowired
    private DosenRepository dosenRepository;
    
    @Autowired
    private PengajaranKelasRepository pengajaranKelasRepository;

    public List<Dosen> getAllDosen() {
        return dosenRepository.findAll();
    }
    
    public List<Dosen> getDosenByKelas(Integer idKelas) {
        return pengajaranKelasRepository.findByKelasWithDosen(idKelas)
                .stream()
                .map(pk -> pk.getDosen())
                .collect(Collectors.toList());
    }

    public Dosen saveDosen(Dosen dosen) {
        return dosenRepository.save(dosen);
    }
    
    public Optional<Dosen> findDosenByNik(String nik) {
        return dosenRepository.findById(nik);
    }
    
    public boolean existsByNik(String nik) {
        return dosenRepository.existsById(nik);
    }
}