package com.example.admin.service;

import com.example.admin.entity.PengambilanKelas;
import com.example.admin.repository.PengambilanKelasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PengambilanKelasService {

    @Autowired
    private PengambilanKelasRepository pengambilanKelasRepository;

    public PengambilanKelas save(PengambilanKelas pengambilanKelas) {
        return pengambilanKelasRepository.save(pengambilanKelas);
    }

    public boolean existsByNpmAndIdKelas(String npm, Integer idKelas) {
        return pengambilanKelasRepository.findByNpmAndIdKelas(npm, idKelas) != null;
    }

    public List<PengambilanKelas> findByKelasWithMahasiswa(Integer idKelas) {
        return pengambilanKelasRepository.findByKelasWithMahasiswa(idKelas);
    }

    public boolean existsByCompositeKey(String npm, String kodeMk, String namaKelas, String semester) {
        return pengambilanKelasRepository.existsByCompositeKey(npm, kodeMk, namaKelas, semester);
    }
}