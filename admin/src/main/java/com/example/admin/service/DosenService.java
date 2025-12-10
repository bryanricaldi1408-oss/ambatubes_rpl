package com.example.admin.service;

import com.example.admin.entity.Dosen;
import com.example.admin.entity.DosenCredentials;
import com.example.admin.entity.Kelas;
import com.example.admin.repository.DosenCredentialsRepository;
import com.example.admin.repository.DosenRepository;
import com.example.admin.repository.PengajaranKelasRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DosenService {

    private final DosenRepository dosenRepository;
    private final PengajaranKelasRepository pengajaranKelasRepository;
    private final DosenCredentialsRepository dosenCredentialsRepository;


    public boolean validateDosen(String email, String password) {
        try {
            log.debug("Validating dosen with email: {}", email);
            DosenCredentials credentials = dosenCredentialsRepository.findByEmail(email);
            if (credentials != null) {
                log.debug("Found credentials for NIK: {}, Password match: {}", 
                    credentials.getNik(), password.equals(credentials.getPassword()));
                return password.equals(credentials.getPassword());
            }
            log.debug("No credentials found for email: {}", email);
            return false;
        } catch (Exception e) {
            log.error("Error validating dosen: {}", e.getMessage());
            return false;
        }
    }

    public Dosen findDosenByEmail(String email) {
        try {
            log.debug("Finding dosen by email: {}", email);
            DosenCredentials credentials = dosenCredentialsRepository.findByEmail(email);
            if (credentials != null) {
                  log.debug("Found credentials, NIK: {}", credentials.getNik());
                return dosenRepository.findById(credentials.getNik()).orElse(null);
            }
            log.debug("No credentials found for email: {}", email);
            return null;
        } catch (Exception e) {
            log.error("Error finding dosen by email: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Kelas> getKelasByDosenNik(String nik) {
        try {
            return pengajaranKelasRepository.findKelasByDosenNik(nik);
        } catch (Exception e) {
            log.error("Error getting kelas by dosen NIK: {}", e.getMessage());
            return List.of(); // Return empty list jika error
        }
    }

    //ADMIN

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