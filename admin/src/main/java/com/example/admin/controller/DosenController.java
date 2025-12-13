package com.example.admin.controller;

import com.example.admin.entity.Dosen;
import com.example.admin.entity.Kelas;
import com.example.admin.service.DosenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dosen")
@RequiredArgsConstructor
@Slf4j
public class DosenController {
    
    private final DosenService dosenService;
    
    // ==========================================
    // DASHBOARD & PROFILE DOSEN
    // ==========================================
    
    /**
     * Dashboard utama dosen dengan search dan filter
     */
    @GetMapping("/home")
    public String home(@RequestParam(required = false) String search,
                    @RequestParam(required = false) Boolean inProgress,
                    @RequestParam(required = false) Boolean past,
                    @RequestParam(required = false) String semester,
                    HttpSession session, 
                    Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        List<Kelas> kelasList;
        
        // Ambil semua kelas untuk dosen ini terlebih dahulu
        List<Kelas> allKelas = dosenService.getKelasByDosenNik(dosen.getNik());
        
        // Filter berdasarkan search keyword
        if (search != null && !search.trim().isEmpty()) {
            kelasList = dosenService.searchKelas(dosen.getNik(), search);
            model.addAttribute("searchQuery", search.trim());
        } else {
            kelasList = allKelas;
        }
        
        // Apply filters
        kelasList = applyFilters(kelasList, inProgress, past, semester);
        
        // Tambahkan status ke setiap kelas untuk ditampilkan di HTML
        kelasList.forEach(kelas -> {
            Map<String, Object> kelasWithStatus = new HashMap<>();
            // Anda bisa menambahkan properti status di sini jika perlu
        });
        
        // Ekstrak daftar semester unik untuk dropdown
        Set<String> semesterList = allKelas.stream()
                .map(Kelas::getSemester)
                .filter(s -> s != null && !s.trim().isEmpty())
                .collect(Collectors.toSet());
        
        model.addAttribute("dosen", dosen);
        model.addAttribute("kelasList", kelasList);
        model.addAttribute("semesterList", semesterList);
        model.addAttribute("inProgressFilter", inProgress != null && inProgress);
        model.addAttribute("pastFilter", past != null && past);
        model.addAttribute("semesterFilter", semester);
        
        return "dosenHome";
    }
    
    /**
     * Helper method untuk apply filters
     */
    private List<Kelas> applyFilters(List<Kelas> kelasList, Boolean inProgress, Boolean past, String semester) {
        if (kelasList == null) {
            return new ArrayList<>();
        }
        
        return kelasList.stream()
                .filter(kelas -> {
                    // Filter semester
                    if (semester != null && !semester.isEmpty() && kelas.getSemester() != null) {
                        if (!kelas.getSemester().equals(semester)) {
                            return false;
                        }
                    }
                    
                    // Filter status (in-progress/past) berdasarkan semester
                    String status = determineClassStatus(kelas.getSemester());
                    
                    if (inProgress != null && inProgress && past != null && past) {
                        // Jika kedua checkbox dicentang, tampilkan semua
                        return true;
                    } else if (inProgress != null && inProgress) {
                        // Hanya tampilkan in-progress
                        return "in-progress".equals(status);
                    } else if (past != null && past) {
                        // Hanya tampilkan past
                        return "past".equals(status);
                    }
                    
                    // Jika tidak ada filter status, tampilkan semua
                    return true;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Helper method untuk menentukan status kelas berdasarkan semester
     */
    private String determineClassStatus(String semester) {
        if (semester == null || semester.trim().isEmpty()) {
            return "in-progress";
        }
        
        try {
            // Ekstrak tahun dari string semester (misal: "Genap 2023/2024")
            String lowerSemester = semester.toLowerCase();
            
            // Cari pola tahun 4 digit
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\d{4}");
            java.util.regex.Matcher matcher = pattern.matcher(semester);
            
            if (matcher.find()) {
                int classYear = Integer.parseInt(matcher.group());
                int currentYear = Year.now().getValue();
                
                // Jika semester mengandung "pendek" atau tahun >= tahun sekarang, anggap in-progress
                if (lowerSemester.contains("pendek")) {
                    return "in-progress";
                }
                
                return classYear >= currentYear ? "in-progress" : "past";
            }
        } catch (Exception e) {
            log.warn("Error determining class status for semester: {}", semester, e);
        }
        
        // Default ke in-progress jika tidak bisa ditentukan
        return "in-progress";
    }
}