package com.example.admin.controller;

import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.dto.KelompokDisplayDto; 
import com.example.admin.entity.AnggotaKelompok; 
import com.example.admin.entity.Kelas;
import com.example.admin.entity.Kelompok; 
import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.MahasiswaService;
import com.example.admin.repository.AnggotaKelompokRepository; 
import com.example.admin.repository.KelasRepository;
import com.example.admin.repository.KelompokRepository; 
import com.example.admin.repository.NilaiMahasiswaRepository;
import com.example.admin.repository.TugasBesarRepository;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.ResponseBody; 

import java.util.ArrayList; 
import java.util.Comparator;
import java.util.List;
import java.util.Optional; 
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

    @Autowired 
    private KelompokRepository kelompokRepository;
    
    @Autowired 
    private AnggotaKelompokRepository anggotaKelompokRepository;

    // === DASHBOARD UTAMA ===
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

    // === DASHBOARD KELAS (LIST TUGAS BESAR) ===
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

    // === HALAMAN DESKRIPSI TUGAS ===
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

    // === HALAMAN JADWAL & NILAI ===
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
    // LOGIKA KELOMPOK (FULL SINKRONISASI)
    // ==========================================

    // 1. Menampilkan Halaman Kelompok
    // URL Format Baru: /mahasiswa/tubes/kelompok/{idTubes}
    @GetMapping("/tubes/kelompok/{idTubes}")
    public String halamanKelompok(@PathVariable Integer idTubes, HttpSession session, Model model) {
        if (!"mahasiswa".equals(session.getAttribute("userRole"))) {
            return "redirect:/login?error=unauthorized";
        }
        
        String npm = (String) session.getAttribute("userNpm");
        Mahasiswa mahasiswa = (Mahasiswa) session.getAttribute("mahasiswa");

        // Ambil Data Tubes (PENTING untuk Breadcrumb & Navigasi Balik)
        TugasBesar tubes = tugasBesarRepository.findById(idTubes).orElse(null);
        if (tubes == null) {
            return "redirect:/mahasiswa/home";
        }

        // Ambil Daftar Kelompok untuk Tubes ini
        List<Kelompok> listKelompokDB = kelompokRepository.findByIdTubes(idTubes);
        List<KelompokDisplayDto> displayList = new ArrayList<>();

        // Cek user saat ini ada di kelompok mana
        Optional<AnggotaKelompok> currentGroup = anggotaKelompokRepository.findByNpmAndIdTubes(npm, idTubes);
        Integer userGroupId = currentGroup.map(AnggotaKelompok::getIdKelompok).orElse(-1);

        // Convert ke DTO untuk Tampilan
        for (Kelompok k : listKelompokDB) {
            int count = anggotaKelompokRepository.countAnggotaByKelompok(k.getIdKelompok());
            boolean isSelected = k.getIdKelompok().equals(userGroupId);
            
            displayList.add(new KelompokDisplayDto(
                k.getIdKelompok(),
                k.getNamaKelompok(),
                count,
                k.getJumlahAnggota(), 
                isSelected
            ));
        }

        model.addAttribute("listKelompok", displayList);
        model.addAttribute("idTubes", idTubes);
        model.addAttribute("tubesActive", tubes); // Kirim object tubes agar HTML bisa baca idKelas
        model.addAttribute("mahasiswa", mahasiswa);

        return "kelompok";
    }

    // 2. Proses Save/Pindah Kelompok
    // URL Format Baru: /mahasiswa/tubes/kelompok/{idTubes}/save
    @PostMapping("/tubes/kelompok/{idTubes}/save")
    public String saveKelompok(@PathVariable Integer idTubes, 
                               @RequestParam("selectedGroup") Integer idKelompokBaru,
                               HttpSession session) {
        
        String npm = (String) session.getAttribute("userNpm");
        if (npm == null) {
            return "redirect:/login";
        }

        try {
            // Hapus dulu keanggotaan lama user di tubes ini
            anggotaKelompokRepository.deleteByNpmAndIdTubes(npm, idTubes);
            
            // Masukkan user ke kelompok baru
            AnggotaKelompok anggotaBaru = new AnggotaKelompok();
            anggotaBaru.setNpm(npm);
            anggotaBaru.setIdKelompok(idKelompokBaru);
            anggotaKelompokRepository.save(anggotaBaru);
            
        } catch (Exception e) {
            e.printStackTrace(); 
        }

        // Redirect balik ke halaman kelompok yang sama
        return "redirect:/mahasiswa/tubes/kelompok/" + idTubes;
    }

    // 3. API Data Anggota (JSON) untuk Modal
    @GetMapping("/api/kelompok/{idKelompok}/anggota")
    @ResponseBody
    public ResponseEntity<List<com.example.admin.dto.AnggotaDto>> getAnggotaKelompok(@PathVariable Integer idKelompok) {
        
        List<Mahasiswa> listMahasiswa = anggotaKelompokRepository.findMahasiswaByKelompok(idKelompok);
        
        // Konversi ke DTO Sederhana
        List<com.example.admin.dto.AnggotaDto> result = new ArrayList<>();
        for (Mahasiswa m : listMahasiswa) {
            result.add(new com.example.admin.dto.AnggotaDto(m.getNama(), m.getNpm()));
        }
        
        return ResponseEntity.ok(result);
    }
}