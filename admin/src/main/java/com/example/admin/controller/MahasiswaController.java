package com.example.admin.controller;

import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.dto.KelompokDisplayDto; // IMPORT BARU
import com.example.admin.entity.AnggotaKelompok; // IMPORT BARU
import com.example.admin.entity.Kelas;
import com.example.admin.entity.Kelompok; // IMPORT BARU
import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.MahasiswaService;
import com.example.admin.repository.AnggotaKelompokRepository; // IMPORT BARU
import com.example.admin.repository.KelasRepository;
import com.example.admin.repository.KelompokRepository; // IMPORT BARU
import com.example.admin.repository.NilaiMahasiswaRepository;
import com.example.admin.repository.TugasBesarRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // IMPORT BARU
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // IMPORT BARU
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // IMPORT BARU
import org.springframework.web.bind.annotation.ResponseBody; // IMPORT BARU

import java.util.ArrayList; // IMPORT BARU
import java.util.Comparator;
import java.util.List;
import java.util.Optional; // IMPORT BARU
import java.util.stream.Collectors;

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
    private NilaiMahasiswaRepository nilaiMahasiswaRepository;

    // === INJECT REPOSITORY BARU UNTUK KELOMPOK ===
    @Autowired 
    private KelompokRepository kelompokRepository;
    
    @Autowired 
    private AnggotaKelompokRepository anggotaKelompokRepository;

    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = mahasiswaService.findMahasiswaByNpm(npm);
        
        List<String> uniqueSemesters = mahasiswa.getListKelas().stream()
            .map(kelas -> kelas.getSemester())
            .filter(semester -> semester != null)
            .distinct()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());
            
        model.addAttribute("uniqueSemesters", uniqueSemesters);
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

    @GetMapping("/tubes/jadwal-nilai/{idTubes}")
    public String jadwalNilai(@PathVariable Integer idTubes, HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }

        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");
        TugasBesar tubes = tugasBesarRepository.findById(idTubes).orElse(null);

        List<JadwalNilaiDto> listJadwalNilai = nilaiMahasiswaRepository.findJadwalDanNilai(npm, idTubes);

        model.addAttribute("listData", listJadwalNilai);
        model.addAttribute("tubesActive", tubes);
        model.addAttribute("mahasiswa", mahasiswa);

        return "mJadwalNilai";
    }

    // ==========================================
    // LOGIKA KELOMPOK (FULL BACKEND CONNECTED)
    // ==========================================

    // 1. Menampilkan Halaman Kelompok dengan Data SQL
    @GetMapping("/tubes/{idTubes}/kelompok")
    public String halamanKelompok(@PathVariable Integer idTubes, HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");

        // Ambil semua kelompok yang tersedia untuk Tugas Besar ini
        List<Kelompok> listKelompokDB = kelompokRepository.findByIdTubes(idTubes);
        List<KelompokDisplayDto> displayList = new ArrayList<>();

        // Cek user saat ini sedang berada di kelompok mana (untuk auto-checked radio button)
        Optional<AnggotaKelompok> currentGroup = anggotaKelompokRepository.findByNpmAndIdTubes(npm, idTubes);
        Integer userGroupId = currentGroup.map(AnggotaKelompok::getIdKelompok).orElse(-1);

        // Convert Entity ke DTO agar mudah dipakai di HTML
        for (Kelompok k : listKelompokDB) {
            int count = anggotaKelompokRepository.countAnggotaByKelompok(k.getIdKelompok());
            boolean isSelected = k.getIdKelompok().equals(userGroupId);
            
            displayList.add(new KelompokDisplayDto(
                k.getIdKelompok(),
                k.getNamaKelompok(),
                count,
                k.getJumlahAnggota(), // Kapasitas Maksimal
                isSelected
            ));
        }

        model.addAttribute("listKelompok", displayList);
        model.addAttribute("idTubes", idTubes);
        model.addAttribute("mahasiswa", mahasiswa);

        return "kelompok";
    }

    // 2. Proses Save/Pindah Kelompok
    // ... (kode atas sama)

    // PERBAIKAN 1: Save Kelompok dengan Validasi & Logika Transaksi
    @PostMapping("/tubes/{idTubes}/kelompok/save")
    public String saveKelompok(@PathVariable Integer idTubes, 
                               @RequestParam("selectedGroup") Integer idKelompokBaru,
                               HttpSession session) {
        
        String npm = (String) session.getAttribute("userNpm");
        
        // Validasi Session
        if (npm == null) {
            return "redirect:/login";
        }

        try {
            // Hapus keanggotaan lama user di tubes ini
            anggotaKelompokRepository.deleteByNpmAndIdTubes(npm, idTubes);
            
            // Masukkan ke kelompok baru
            AnggotaKelompok anggotaBaru = new AnggotaKelompok();
            anggotaBaru.setNpm(npm);
            anggotaBaru.setIdKelompok(idKelompokBaru);
            anggotaKelompokRepository.save(anggotaBaru);
            
        } catch (Exception e) {
            e.printStackTrace(); // Cek console jika error
        }

        return "redirect:/mahasiswa/tubes/" + idTubes + "/kelompok";
    }

    // PERBAIKAN 2: API Menggunakan DTO agar data JSON bersih & pasti muncul
    @GetMapping("/api/kelompok/{idKelompok}/anggota")
    @ResponseBody
    public ResponseEntity<List<com.example.admin.dto.AnggotaDto>> getAnggotaKelompok(@PathVariable Integer idKelompok) {
        
        List<Mahasiswa> listMahasiswa = anggotaKelompokRepository.findMahasiswaByKelompok(idKelompok);
        
        // Konversi ke DTO Sederhana (Nama & NPM saja)
        List<com.example.admin.dto.AnggotaDto> result = new ArrayList<>();
        for (Mahasiswa m : listMahasiswa) {
            result.add(new com.example.admin.dto.AnggotaDto(m.getNama(), m.getNpm()));
        }
        
        return ResponseEntity.ok(result);
    }
}