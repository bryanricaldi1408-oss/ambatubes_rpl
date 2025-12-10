package com.example.admin.controller;

import com.example.admin.entity.Dosen;
import com.example.admin.entity.Kelas;
import com.example.admin.service.DosenService;
import com.example.admin.service.KelasService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dosen")
@RequiredArgsConstructor
@Slf4j
public class DosenTubesController {
    
    private final DosenService dosenService;
    private final KelasService kelasService;
    
    /**
     * Halaman tugas besar untuk dosen
     */
    @GetMapping("/tubes")
    public String dosenTubes(@RequestParam Integer kelasId,
                           HttpSession session,
                           Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            // Ambil data kelas berdasarkan ID
            Kelas kelas = kelasService.findById(kelasId);
            
            if (kelas == null) {
                model.addAttribute("error", "Kelas tidak ditemukan");
                return "error";
            }
            
            // Verifikasi bahwa dosen adalah pengajar di kelas ini
            boolean isDosenPengajar = dosenService.getKelasByDosenNik(dosen.getNik())
                    .stream()
                    .anyMatch(k -> k.getIdKelas().equals(kelasId));
            
            if (!isDosenPengajar) {
                model.addAttribute("error", "Anda tidak memiliki akses ke kelas ini");
                return "error";
            }
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            
            return "dosenTubes";
            
        } catch (Exception e) {
            log.error("Error loading dosen tubes page: {}", e.getMessage(), e);
            model.addAttribute("error", "Terjadi kesalahan saat memuat halaman");
            return "error";
        }
    }
}