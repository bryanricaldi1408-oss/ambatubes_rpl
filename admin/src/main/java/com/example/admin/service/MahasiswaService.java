package com.example.admin.service;

import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.MahasiswaCredentials;
import com.example.admin.repository.MahasiswaRepository;
import com.example.admin.repository.MahasiswaCredentialsRepository;
// Tambahkan Repository dari Admin ini:
import com.example.admin.repository.PengambilanKelasRepository; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Ini otomatis membuat constructor untuk injection
@Slf4j
public class MahasiswaService {

    // 1. Dependency Gabungan
    private final MahasiswaRepository mahasiswaRepository;
    private final MahasiswaCredentialsRepository mahasiswaCredentialsRepository;
    private final PengambilanKelasRepository pengambilanKelasRepository; // Inject repo admin

    // ==========================================
    // BAGIAN 1: LOGIKA LOGIN & USER (DARI MAHASISWA)
    // ==========================================

    /**
     * Validasi login mahasiswa berdasarkan email dan password
     */
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

    // ==========================================
    // BAGIAN 2: LOGIKA ADMIN (DARI ADMIN)
    // ==========================================

    /**
     * Mengambil semua data mahasiswa
     */
    public List<Mahasiswa> getAllMahasiswa() {
        return mahasiswaRepository.findAll();
    }

    /**
     * Mengambil daftar mahasiswa berdasarkan ID Kelas (Fitur Admin)
     * Pastikan PengambilanKelasRepository sudah ada method findByKelasWithMahasiswa atau sejenisnya
     */
    public List<Mahasiswa> getMahasiswaByKelas(Integer idKelas) {
        // PERBAIKAN: Gunakan 'findByKelasWithMahasiswa' yang sudah ada di Repository kamu
        return pengambilanKelasRepository.findByKelasWithMahasiswa(idKelas) 
                .stream()
                .map(pk -> pk.getMahasiswa()) 
                .collect(Collectors.toList());
    }

    /**
     * Menyimpan data mahasiswa baru (Fitur Admin)
     */
    public Mahasiswa saveMahasiswa(Mahasiswa mahasiswa) {
        return mahasiswaRepository.save(mahasiswa);
    }

    /**
     * Cek apakah NPM sudah ada (Fitur Admin)
     */
    public boolean existsByNpm(String npm) {
        return mahasiswaRepository.existsById(npm);
    }
}