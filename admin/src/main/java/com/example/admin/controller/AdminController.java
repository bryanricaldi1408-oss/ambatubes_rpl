package com.example.admin.controller;

import com.example.admin.service.KelasService;
import com.example.admin.service.DosenService;
import com.example.admin.service.MahasiswaService;
import com.example.admin.dto.ClassDisplayDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class AdminController {

     @Autowired
    private KelasService kelasService;
    
    @Autowired
    private DosenService dosenService;
    
    @Autowired
    private MahasiswaService mahasiswaService;

    @GetMapping("/adminHome")
    public String showAdminHome(Model model) {
        List<ClassDisplayDto> classes = kelasService.getAllClassesForDisplay();
        List<String> uniqueSemesters = kelasService.getUniqueSemesters();
        
        model.addAttribute("classes", classes);
        model.addAttribute("uniqueSemesters", uniqueSemesters);
        return "adminHome";
    }
}