package com.example.admin.controller;

import com.example.admin.entity.Mahasiswa;
// 1. IMPORT SERVICE ADMIN (Sesuaikan package-nya jika perlu)
import com.example.admin.service.AdminService; 
import com.example.admin.service.MahasiswaService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    // 2. INJECT KEDUA SERVICE
    private final MahasiswaService mahasiswaService;
    private final AdminService adminService; // Tambahkan ini

    @GetMapping("/")
    public String showLoginPage() {
        return "login"; // Pakai satu tampilan login saja (login.html)
    }

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, 
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        
        log.info("Login attempt - Email: {}", email);
        
        try {
            // === LOGIKA 1: CEK APAKAH DIA ADMIN? ===
            // Kita cek admin dulu, karena biasanya jumlah admin lebih sedikit
            if (adminService.validateAdmin(email, password)) {
                // Set session untuk Admin
                session.setAttribute("userRole", "admin");
                session.setAttribute("userEmail", email);
                
                log.info("Login successful as ADMIN: {}", email);
                return "redirect:/adminHome"; // Arahkan ke rute AdminController
            }

            // === LOGIKA 2: JIKA BUKAN ADMIN, CEK APAKAH DIA MAHASISWA? ===
            if (mahasiswaService.validateMahasiswa(email, password)) {
                Mahasiswa mahasiswa = mahasiswaService.findMahasiswaByEmail(email);
                
                if (mahasiswa != null) {
                    session.setAttribute("userRole", "mahasiswa");
                    session.setAttribute("mahasiswa", mahasiswa);
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userNpm", mahasiswa.getNpm());
                    
                    log.info("Login successful as MAHASISWA: {}", mahasiswa.getNama());
                    return "redirect:/mahasiswa/home";
                }
            }
            
            // === JIKA KEDUANYA GAGAL ===
            model.addAttribute("error", "Login gagal! Email atau password salah.");
            return "login";
            
        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage());
            model.addAttribute("error", "Terjadi kesalahan sistem.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    
}