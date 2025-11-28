package com.example.admin.controller;

import com.example.admin.dto.JadwalNilaiDto; // IMPORT DTO
import com.example.admin.entity.Kelas;
import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.MahasiswaService;
import com.example.admin.repository.KelasRepository;
import com.example.admin.repository.TugasBesarRepository;
import com.example.admin.repository.NilaiMahasiswaRepository; // IMPORT REPO NILAI

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List; // Import List

@Controller
@RequestMapping("/mahasiswa")
@Slf4j
public class MahasiswaController {

    @Autowired
    private MahasiswaService mahasiswaService;

    @Autowired
    private KelasRepository kelasRepository;

    @Autowired
    private TugasBesarRepository tugasBesarRepository;

    @Autowired
    private NilaiMahasiswaRepository nilaiMahasiswaRepository; // INJECT REPO BARU

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = mahasiswaService.findMahasiswaByNpm(npm);
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "mahasiswaHome";
    }

    @GetMapping("/tubes/{idKelas}")
    public String tubesDashboard(@PathVariable Integer idKelas, HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        Kelas kelasDipilih = kelasRepository.findById(idKelas).orElse(null);
        
        if (kelasDipilih == null) {
            return "redirect:/mahasiswa/home"; 
        }

        model.addAttribute("kelasActive", kelasDipilih);
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "mTubesDashboard";
    }

    @GetMapping("/tubes/deskripsi/{idTubes}")
    public String deskripsiTubes(@PathVariable Integer idTubes, HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }

        TugasBesar tubes = tugasBesarRepository.findById(idTubes).orElse(null);

        if (tubes == null) {
            return "redirect:/mahasiswa/home";
        }

        model.addAttribute("tubesActive", tubes);
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);

        return "deskripsiTubes";
    }

    // === INI METHOD YANG HILANG SEBELUMNYA ===
    @GetMapping("/tubes/jadwal-nilai/{idTubes}")
    public String jadwalNilai(@PathVariable Integer idTubes, HttpSession session, Model model) {
        // 1. Cek Login
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }

        // 2. Ambil Data Pendukung
        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        TugasBesar tubes = tugasBesarRepository.findById(idTubes).orElse(null);

        // 3. Ambil Data Gabungan (Jadwal + Nilai) via Repository
        // Method findJadwalDanNilai ini ada di NilaiMahasiswaRepository
        List<JadwalNilaiDto> listJadwalNilai = nilaiMahasiswaRepository.findJadwalDanNilai(npm, idTubes);

        // 4. Masukkan ke Model
        model.addAttribute("listData", listJadwalNilai);
        model.addAttribute("tubesActive", tubes);
        model.addAttribute("mahasiswa", mahasiswa);

        return "mJadwalNilai";
    }

    // --- Method statis lama (Kelompok) ---
    @GetMapping("/kelompok")
    public String kelompok(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        return "kelompok";
    }
}