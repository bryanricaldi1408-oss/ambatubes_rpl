package com.example.admin.controller;

import com.example.admin.service.KelasService;
import com.example.admin.service.DatabaseImportService;
import com.example.admin.service.DosenService;
import com.example.admin.service.ExcelParseKelasService;
import com.example.admin.service.ImportResult;
import com.example.admin.service.MahasiswaService;
import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.dto.ExcelImportData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/addClasses")
    public String showAddClassesPage() {
        return "addClasses";
    }

    @Autowired
    private ExcelParseKelasService ExcelParseKelasService;
    
    @Autowired
    private DatabaseImportService databaseImportService;

    @PostMapping("/uploadClasses")
    public String uploadClasses(@RequestParam("file") MultipartFile file, 
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/addClasses";
        }
        
        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            redirectAttributes.addFlashAttribute("error", "Only Excel files (.xlsx) are allowed");
            return "redirect:/addClasses";
        }
        
        try {
            // Parse Excel file
            ExcelImportData excelData = ExcelParseKelasService.parseExcelFile(file);
            
            // Import to database
            ImportResult result = databaseImportService.importExcelData(excelData);
            
            if (result.isSuccess()) {
                redirectAttributes.addFlashAttribute("success", 
                    "Data imported successfully! " +
                    "Mata Kuliah: " + result.getMataKuliahStats().get("imported") + " new, " +
                    "Dosen: " + result.getDosenStats().get("imported") + " new, " +
                    "Mahasiswa: " + result.getMahasiswaStats().get("imported") + " new, " +
                    "Kelas: " + result.getKelasStats().get("imported") + " new");
            } else {
                redirectAttributes.addFlashAttribute("error", result.getMessage());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing file: " + e.getMessage());
        }
        
        return "redirect:/adminHome";
    }
}