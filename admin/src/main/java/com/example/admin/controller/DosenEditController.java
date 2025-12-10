package com.example.admin.controller;

import com.example.admin.entity.Dosen;
import com.example.admin.entity.Kelas;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.DosenService;
import com.example.admin.service.KelasService;
import com.example.admin.service.TugasBesarService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dosen")
@RequiredArgsConstructor
@Slf4j
public class DosenEditController {
    
    private final DosenService dosenService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    
    /**
     * Halaman buat/edit tugas besar
     */
    @GetMapping("/edit")
    public String dosenEdit(@RequestParam Integer kelasId,
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
            
            return "dosenEdit";
            
        } catch (Exception e) {
            log.error("Error loading dosen edit page: {}", e.getMessage(), e);
            model.addAttribute("error", "Terjadi kesalahan saat memuat halaman");
            return "error";
        }
    }
    
    @PostMapping("/save-tugas")
    public String saveTugasBesar(@RequestParam("kelasId") Integer kelasId,
                                 @RequestParam("namaTugas") String namaTugas,
                                 @RequestParam("deskripsi") String deskripsi,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        // Cek session
        Integer idDosen = (Integer) session.getAttribute("idDosen");
        if (idDosen == null) {
            return "redirect:/login";
        }
        
    
}