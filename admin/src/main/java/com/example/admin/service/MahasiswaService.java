package com.example.admin.service;

import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.MahasiswaCredentials;
import com.example.admin.repository.MahasiswaRepository;
import com.example.admin.repository.MahasiswaCredentialsRepository;

import com.example.admin.repository.PengambilanKelasRepository; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MahasiswaService {

    private final MahasiswaRepository mahasiswaRepository;
    private final MahasiswaCredentialsRepository mahasiswaCredentialsRepository;
    private final PengambilanKelasRepository pengambilanKelasRepository;

    //login mahasiswa
    public boolean validateMahasiswa(String email, String password) {
        try {
            MahasiswaCredentials credentials = mahasiswaCredentialsRepository.findByEmail(email);
            if (credentials != null) {
                return password.equals(credentials.getPassword());
            }
            return false;
        } catch (Exception e) {
            log.error("Error validating mahasiswa: {}", e.getMessage());
            return false;
        }
    }

    public Mahasiswa findMahasiswaByEmail(String email) {
        try {
            MahasiswaCredentials credentials = mahasiswaCredentialsRepository.findByEmail(email);
            if (credentials != null) {
                return mahasiswaRepository.findById(credentials.getNpm()).orElse(null);
            }
            return null;
        } catch (Exception e) {
            log.error("Error finding mahasiswa by email: {}", e.getMessage());
            return null;
        }
    }

    public Mahasiswa findMahasiswaByNpm(String npm) {
        try {
            return mahasiswaRepository.findById(npm).orElse(null);
        } catch (Exception e) {
            log.error("Error finding mahasiswa by NPM: {}", e.getMessage());
            return null;
        }
    }


    public List<Mahasiswa> getAllMahasiswa() {
        return mahasiswaRepository.findAll();
    }

    public List<Mahasiswa> getMahasiswaByKelas(Integer idKelas) {
                return pengambilanKelasRepository.findByKelasWithMahasiswa(idKelas) 
                .stream()
                .map(pk -> pk.getMahasiswa()) 
                .collect(Collectors.toList());
    }


    public Mahasiswa saveMahasiswa(Mahasiswa mahasiswa) {
        return mahasiswaRepository.save(mahasiswa);
    }

    public boolean existsByNpm(String npm) {
        return mahasiswaRepository.existsById(npm);
    }
}