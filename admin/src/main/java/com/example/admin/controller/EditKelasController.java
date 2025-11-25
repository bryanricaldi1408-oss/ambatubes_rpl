package com.example.admin.controller;

import com.example.admin.service.KelasService;
import com.example.admin.service.DosenService;
import com.example.admin.service.MahasiswaService;
import com.example.admin.service.PengajaranKelasService;
import com.example.admin.service.PengambilanKelasService;
import com.example.admin.dto.TambahMahasiswaDto;
import com.example.admin.dto.ClassDisplayDto;
import com.example.admin.dto.TambahDosenDto;
import com.example.admin.entity.Dosen;
import com.example.admin.entity.Mahasiswa;
import com.example.admin.entity.PengajaranKelas;
import com.example.admin.entity.PengambilanKelas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/editKelas")
public class EditKelasController {

    @Autowired
    private KelasService kelasService;
    
    @Autowired
    private DosenService dosenService;
    
    @Autowired
    private MahasiswaService mahasiswaService;

    @Autowired
    private PengajaranKelasService pengajaranKelasService;

    @Autowired
    private PengambilanKelasService pengambilanKelasService;

    @GetMapping("/{classId}")
    public String showEditKelasPage(@PathVariable("classId") Integer classId, Model model) {
        // Get class details
        ClassDisplayDto classDetails = kelasService.getClassById(classId);
        
        // Get lecturers teaching this class
        List<Dosen> dosenList = dosenService.getDosenByKelas(classId);
        
        // Get students taking this class
        List<Mahasiswa> mahasiswaList = mahasiswaService.getMahasiswaByKelas(classId);
        
        model.addAttribute("classId", classId);
        model.addAttribute("classDetails", classDetails);
        model.addAttribute("dosenList", dosenList);
        model.addAttribute("mahasiswaList", mahasiswaList);
        
        return "editKelas";
    }

    @GetMapping("/tambahDosen/{classId}")
    public String showTambahDosenPage(@PathVariable("classId") Integer classId, Model model) {
        // Get class details for context
        ClassDisplayDto classDetails = kelasService.getClassById(classId);
        
        model.addAttribute("classId", classId);
        model.addAttribute("classDetails", classDetails);
        model.addAttribute("tambahDosenDto", new TambahDosenDto());
        
        return "tambahDosen";
    }

    @PostMapping("/tambahDosen/{classId}")
    public String processTambahDosen(@PathVariable("classId") Integer classId,
                                   @ModelAttribute TambahDosenDto tambahDosenDto,
                                   RedirectAttributes redirectAttributes) {
        try {
            // Check if dosen already exists
            if (dosenService.existsByNik(tambahDosenDto.getNik())) {
                // Dosen exists, check if already teaching this class
                if (pengajaranKelasService.existsByNikAndIdKelas(tambahDosenDto.getNik(), classId)) {
                    redirectAttributes.addFlashAttribute("error", "Dosen sudah mengajar kelas ini");
                } else {
                    // Dosen exists but not teaching this class - create relationship
                    PengajaranKelas pengajaranKelas = new PengajaranKelas();
                    pengajaranKelas.setNik(tambahDosenDto.getNik());
                    pengajaranKelas.setIdKelas(classId);
                    pengajaranKelasService.save(pengajaranKelas);
                    redirectAttributes.addFlashAttribute("success", "Dosen berhasil ditambahkan ke kelas");
                }
            } else {
                // Dosen doesn't exist - create new dosen and relationship
                Dosen newDosen = new Dosen();
                newDosen.setNik(tambahDosenDto.getNik());
                newDosen.setNama(tambahDosenDto.getNamaLengkap());
                
                // Save the new dosen
                dosenService.saveDosen(newDosen);
                
                // Create teaching relationship
                PengajaranKelas pengajaranKelas = new PengajaranKelas();
                pengajaranKelas.setNik(tambahDosenDto.getNik());
                pengajaranKelas.setIdKelas(classId);
                pengajaranKelasService.save(pengajaranKelas);
                
                redirectAttributes.addFlashAttribute("success", "Dosen baru berhasil dibuat dan ditambahkan ke kelas");
            }
            
            return "redirect:/editKelas/" + classId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            return "redirect:/editKelas/tambahDosen/" + classId;
        }
    }

    @GetMapping("/tambahMahasiswa/{classId}")
    public String showTambahMahasiswaPage(@PathVariable("classId") Integer classId, Model model) {
        // Get class details for context
        ClassDisplayDto classDetails = kelasService.getClassById(classId);
        
        model.addAttribute("classId", classId);
        model.addAttribute("classDetails", classDetails);
        model.addAttribute("tambahMahasiswaDto", new TambahMahasiswaDto()); // Add empty DTO for form binding
        
        return "tambahMahasiswa";
    }

    @PostMapping("/tambahMahasiswa/{classId}")
    public String processTambahMahasiswa(@PathVariable("classId") Integer classId,
                                    @ModelAttribute TambahMahasiswaDto tambahMahasiswaDto,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Check if mahasiswa already exists
            if (mahasiswaService.existsByNpm(tambahMahasiswaDto.getNpm())) {
                // Mahasiswa exists, check if already enrolled in this class
                if (pengambilanKelasService.existsByNpmAndIdKelas(tambahMahasiswaDto.getNpm(), classId)) {
                    redirectAttributes.addFlashAttribute("error", "Mahasiswa sudah terdaftar di kelas ini");
                } else {
                    // Mahasiswa exists but not enrolled in this class - create relationship
                    PengambilanKelas pengambilanKelas = new PengambilanKelas();
                    pengambilanKelas.setNpm(tambahMahasiswaDto.getNpm());
                    pengambilanKelas.setIdKelas(classId);
                    pengambilanKelasService.save(pengambilanKelas);
                    redirectAttributes.addFlashAttribute("success", "Mahasiswa berhasil ditambahkan ke kelas");
                }
            } else {
                // Mahasiswa doesn't exist - create new mahasiswa and relationship
                Mahasiswa newMahasiswa = new Mahasiswa();
                newMahasiswa.setNpm(tambahMahasiswaDto.getNpm());
                newMahasiswa.setNama(tambahMahasiswaDto.getNamaLengkap());
                
                // Save the new mahasiswa
                mahasiswaService.saveMahasiswa(newMahasiswa);
                
                // Create enrollment relationship
                PengambilanKelas pengambilanKelas = new PengambilanKelas();
                pengambilanKelas.setNpm(tambahMahasiswaDto.getNpm());
                pengambilanKelas.setIdKelas(classId);
                pengambilanKelasService.save(pengambilanKelas);
                
                redirectAttributes.addFlashAttribute("success", "Mahasiswa baru berhasil dibuat dan ditambahkan ke kelas");
            }
            
            return "redirect:/editKelas/" + classId;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Terjadi kesalahan: " + e.getMessage());
            return "redirect:/editKelas/tambahMahasiswa/" + classId;
        }
    }
}