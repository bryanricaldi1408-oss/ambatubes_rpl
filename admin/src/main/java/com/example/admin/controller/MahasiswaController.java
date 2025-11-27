package com.example.admin.controller;

import com.example.admin.entity.Mahasiswa;
import com.example.admin.service.MahasiswaService;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/mahasiswa")
@Slf4j
public class MahasiswaController {

    @Autowired
    private MahasiswaService mahasiswaService; // Pastikan Service di-inject

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Cek autentikasi
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        // Ambil NPM dari session
        String npm = (String) session.getAttribute("userNpm");
        
        // Ambil data FULL mahasiswa dari database (agar listKelas terbawa)
        Mahasiswa mahasiswa = mahasiswaService.findMahasiswaByNpm(npm);
        
        // Kirim ke view
        model.addAttribute("mahasiswa", mahasiswa);
        
        // Debugging (opsional, cek di terminal apakah kelas terbaca)
        if(mahasiswa != null) {
            log.info("Jumlah kelas diambil: {}", mahasiswa.getListKelas().size());
        }

        return "mahasiswaHome";
    }

    @GetMapping("/tubes")
    public String tubesDashboard(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "mTubesDashboard";
    }

    @GetMapping("/deskripsi")
    public String deskripsiTubes(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "deskripsiTubes";
    }

    @GetMapping("/kelompok")
    public String kelompok(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "kelompok";
    }

    @GetMapping("/jadwal-nilai")
    public String jadwalNilai(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        model.addAttribute("mahasiswa", mahasiswa);
        
        return "mJadwalNilai";
    }
}