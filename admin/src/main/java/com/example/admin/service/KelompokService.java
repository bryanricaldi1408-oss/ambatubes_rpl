package com.example.admin.service;

import com.example.admin.entity.Kelompok;
import com.example.admin.entity.Kelompok;
import com.example.admin.repository.AnggotaKelompokRepository;
import com.example.admin.repository.KelompokRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KelompokService {

    private final KelompokRepository kelompokRepository;
    private final AnggotaKelompokRepository anggotaKelompokRepository;

    @Transactional
    public List<Kelompok> generateKelompok(Integer idTubes, Integer jumlahGrup, Integer maxAnggota) {
        // 1. Hapus anggota yang sudah ada untuk tubes ini (bersih-bersih)
        anggotaKelompokRepository.deleteByIdTubes(idTubes);

        // 2. Hapus kelompok yang sudah ada untuk tubes ini (bersih-bersih)
        kelompokRepository.deleteByIdTubes(idTubes);
        
        // 3. Buat kelompok baru
        List<Kelompok> newGroups = new ArrayList<>();
        char groupName = 'A';
        
        for (int i = 0; i < jumlahGrup; i++) {
            Kelompok k = new Kelompok();
            k.setIdTubes(idTubes);
            k.setNamaKelompok(String.valueOf(groupName));
            k.setJumlahAnggota(maxAnggota); // Ini kapasitas max
            
            newGroups.add(k);
            groupName++;
            
            // Handle jika lebih dari 26 grup (A-Z), bisa lanjut AA, AB dst atau cukup A-Z dulu
            // Asumsi: maks 26 grup untuk simplisitas
        }
        
        return kelompokRepository.saveAll(newGroups);
    }

    public List<Kelompok> getByTubesId(Integer idTubes) {
        return kelompokRepository.findByIdTubes(idTubes);
    }
}
