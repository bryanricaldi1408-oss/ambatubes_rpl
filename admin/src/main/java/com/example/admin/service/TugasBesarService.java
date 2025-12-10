package com.example.admin.service;

import com.example.admin.entity.TugasBesar;
import com.example.admin.repository.TugasBesarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TugasBesarService {
    
    @Autowired
    private TugasBesarRepository tugasBesarRepository;
    
    @Transactional
    public TugasBesar createTugasBesar(Integer kelasId, String namaTugas, String deskripsi) {
        // Validasi: Cek duplikasi nama tugas di kelas yang sama
        Optional<TugasBesar> existingTugas = tugasBesarRepository
            .findByKelasIdAndNama(kelasId, namaTugas);
        
        if (existingTugas.isPresent()) {
            throw new RuntimeException("Tugas dengan nama '" + namaTugas + "' sudah ada di kelas ini");
        }
        
        // Buat tugas baru
        TugasBesar tugasBesar = new TugasBesar();
        tugasBesar.setNamaTugas(namaTugas);
        tugasBesar.setDeskripsi(deskripsi);
        tugasBesar.setIdKelas(kelasId);
        tugasBesar.setTanggalDibuat(LocalDate.now());
        
        return tugasBesarRepository.save(tugasBesar);
    }
    
    public List<TugasBesar> getAllTugasByKelas(Integer kelasId) {
        return tugasBesarRepository.findByKelasId(kelasId);
    }
    
    public TugasBesar getTugasById(Integer idTubes) {
        return tugasBesarRepository.findById(idTubes)
            .orElseThrow(() -> new RuntimeException("Tugas besar tidak ditemukan dengan ID: " + idTubes));
    }
    
    public Optional<TugasBesar> getLatestTugasByKelas(Integer kelasId) {
        return tugasBesarRepository.findLatestByKelasId(kelasId);
    }
    
    @Transactional
    public TugasBesar updateTugasBesar(Integer idTubes, String namaTugas, String deskripsi) {
        TugasBesar tugasBesar = getTugasById(idTubes);
        tugasBesar.setNamaTugas(namaTugas);
        tugasBesar.setDeskripsi(deskripsi);
        return tugasBesarRepository.save(tugasBesar);
    }
    
    @Transactional
    public void deleteTugasBesar(Integer idTubes) {
        tugasBesarRepository.deleteById(idTubes);
    }
}