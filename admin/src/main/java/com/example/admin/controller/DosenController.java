package com.example.admin.controller;

import com.example.admin.entity.Dosen;
import com.example.admin.service.DosenService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

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
     * Dashboard utama dosen
     */
    @GetMapping("/home")
    public String home(HttpSession session, Model model) {
        // Cek session
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        String userRole = (String) session.getAttribute("userRole");
        
        if (!"dosen".equals(userRole) || dosen == null) {
            log.warn("Unauthorized access to dosen home");
            return "redirect:/login";
        }
        
        model.addAttribute("dosen", dosen);
        log.info("Dosen {} accessed dashboard", dosen.getNama());
        return "dosenHome";
    }
}