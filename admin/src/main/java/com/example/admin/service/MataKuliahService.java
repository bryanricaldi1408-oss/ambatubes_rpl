package com.example.admin.service;

import com.example.admin.entity.MataKuliah;
import com.example.admin.repository.MataKuliahRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MataKuliahService {

    @Autowired
    private MataKuliahRepository mataKuliahRepository;

    public MataKuliah saveMataKuliah(MataKuliah mataKuliah) {
        return mataKuliahRepository.save(mataKuliah);
    }

    public Optional<MataKuliah> findMataKuliahByKode(String kodeMk) {
        return mataKuliahRepository.findById(kodeMk);
    }

    public boolean existsByKodeMk(String kodeMk) {
        return mataKuliahRepository.existsById(kodeMk);
    }

    public List<MataKuliah> getAllMataKuliah() {
        return mataKuliahRepository.findAll();
    }
}