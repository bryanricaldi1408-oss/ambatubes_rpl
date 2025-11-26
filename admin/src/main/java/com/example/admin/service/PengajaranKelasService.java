package com.example.admin.service;

import com.example.admin.entity.PengajaranKelas;
import com.example.admin.repository.PengajaranKelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PengajaranKelasService {

    @Autowired
    private PengajaranKelasRepository pengajaranKelasRepository;

    public PengajaranKelas save(PengajaranKelas pengajaranKelas) {
        return pengajaranKelasRepository.save(pengajaranKelas);
    }

    public boolean existsByNikAndIdKelas(String nik, Integer idKelas) {
        return pengajaranKelasRepository.findByNikAndIdKelas(nik, idKelas) != null;
    }

    public List<PengajaranKelas> findByKelasWithDosen(Integer idKelas) {
        return pengajaranKelasRepository.findByKelasWithDosen(idKelas);
    }

    public boolean existsByCompositeKey(String nik, String kodeMk, String namaKelas, String semester) {
        return pengajaranKelasRepository.existsByCompositeKey(nik, kodeMk, namaKelas, semester);
    }
}