package com.example.admin.controller;

import com.example.admin.entity.Dosen;
import com.example.admin.entity.Kelas;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.DosenService;
import com.example.admin.service.KelasService;
import com.example.admin.service.TugasBesarService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/dosen")
@RequiredArgsConstructor
public class DosenEditController {
    
    private final DosenService dosenService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    
    // ==================== STEP 1: DAFTAR TUGAS ====================
    @GetMapping("/tubes")
    public String listTugas(@RequestParam Integer kelasId,
                          HttpSession session,
                          Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("kelasId", kelasId); // Tambah ini
            
            return "dosenTubes";  // Render dosenTubes.html
            
        } catch (Exception e) {
            model.addAttribute("error", "Kelas tidak ditemukan");
            return "error";
        }
    }
    
    // ==================== STEP 1: FORM BUAT TUGAS (NEW/EDIT) ====================
    @GetMapping("/edit")
    public String formTugas(@RequestParam Integer kelasId,
                          @RequestParam(required = false) Integer idTubes, // Opsional untuk edit
                          HttpSession session,
                          Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("kelasId", kelasId);
            
            // Jika ada idTubes, berarti mode EDIT
            if (idTubes != null) {
                TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
                model.addAttribute("tugasBesar", tugasBesar);
                model.addAttribute("idTubes", idTubes);
                model.addAttribute("isEditMode", true);
            } else {
                model.addAttribute("isEditMode", false);
            }
            
            return "dosenEdit";  // Render dosenEdit.html
            
        } catch (Exception e) {
            model.addAttribute("error", "Kelas tidak ditemukan");
            return "error";
        }
    }
    
    // ==================== SIMPAN TUGAS BARU KE DATABASE ====================
    @PostMapping("/save-tugas")
    public String saveTugas(@RequestParam Integer kelasId,
                        @RequestParam String namaTugas,
                        @RequestParam(required = false) String deskripsi,
                        HttpSession session,
                        RedirectAttributes ra) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            // Simpan ke database
            TugasBesar tugas = tugasBesarService.createTugasBesar(kelasId, namaTugas, deskripsi);
            
            if (tugas == null || tugas.getIdTubes() == null) {
                ra.addFlashAttribute("error", "Gagal menyimpan tugas");
                return "redirect:/dosen/edit?kelasId=" + kelasId;
            }
            
            // Redirect ke step 2 dengan parameter
            ra.addFlashAttribute("success", "Tugas berhasil disimpan!");
            return "redirect:/dosen/upload-jadwal?kelasId=" + kelasId + "&idTubes=" + tugas.getIdTubes();
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dosen/edit?kelasId=" + kelasId;
        }
    }
    
    // ==================== UPDATE TUGAS YANG SUDAH ADA ====================
    @PostMapping("/update-tugas")
    public String updateTugas(@RequestParam Integer kelasId,
                           @RequestParam Integer idTubes,
                           @RequestParam String namaTugas,
                           @RequestParam(required = false) String deskripsi,
                           RedirectAttributes ra) {
        
        try {
            // Update tugas yang sudah ada
            tugasBesarService.updateTugasBesar(idTubes, namaTugas, deskripsi);
            
            ra.addFlashAttribute("success", "Tugas berhasil diupdate!");
            // Kembali ke halaman yang sama atau ke step berikutnya
            return "redirect:/dosen/edit?kelasId=" + kelasId + "&idTubes=" + idTubes;
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dosen/edit?kelasId=" + kelasId + "&idTubes=" + idTubes;
        }
    }
    
    // ==================== STEP 2: UPLOAD JADWAL ====================
    @GetMapping("/upload-jadwal")
    public String showUploadJadwal(@RequestParam Integer kelasId,
                                @RequestParam Integer idTubes,
                                HttpSession session,
                                Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            
            return "uploadJadwal"; // template: uploadJadwal.html
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/upload-jadwal-file")
    @ResponseBody
    public Map<String, Object> uploadJadwalFile(@RequestParam Integer kelasId,
                                            @RequestParam Integer idTubes,
                                            @RequestParam MultipartFile jadwalFile,
                                            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            response.put("redirect", "/login");
            return response;
        }
        
        try {
            // Validasi file
            if (jadwalFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "File tidak boleh kosong");
                return response;
            }
            
            // Validasi ekstensi
            String fileName = jadwalFile.getOriginalFilename();
            if (fileName != null && !fileName.toLowerCase().endsWith(".xlsx")) {
                response.put("success", false);
                response.put("message", "Hanya file .xlsx yang diperbolehkan");
                return response;
            }
            
            // Validasi ukuran (max 10MB)
            if (jadwalFile.getSize() > 10 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Ukuran file maksimal 10MB");
                return response;
            }
            
            // TODO: Simpan file ke storage/database
            // String filePath = fileService.saveJadwalFile(jadwalFile, idTubes);
            // tugasBesarService.updateJadwalPath(idTubes, filePath);
            
            response.put("success", true);
            response.put("message", "Jadwal berhasil diupload!");
            response.put("redirect", "/dosen/upload-rubrik?kelasId=" + kelasId + "&idTubes=" + idTubes);
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    // ==================== STEP 3: UPLOAD RUBRIK ====================
    @GetMapping("/upload-rubrik")
    public String showUploadRubrik(@RequestParam Integer kelasId,
                                @RequestParam Integer idTubes,
                                HttpSession session,
                                Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            
            return "uploadRubrik"; // template: uploadRubrik.html
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/upload-rubrik-file")
    @ResponseBody
    public Map<String, Object> uploadRubrikFile(@RequestParam Integer kelasId,
                                            @RequestParam Integer idTubes,
                                            @RequestParam MultipartFile rubrikFile,
                                            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            response.put("redirect", "/login");
            return response;
        }
        
        try {
            // Validasi file
            if (rubrikFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "File tidak boleh kosong");
                return response;
            }
            
            // Validasi ekstensi
            String fileName = rubrikFile.getOriginalFilename();
            if (fileName != null && 
                !fileName.toLowerCase().endsWith(".pdf") && 
                !fileName.toLowerCase().endsWith(".docx") &&
                !fileName.toLowerCase().endsWith(".doc")) {
                response.put("success", false);
                response.put("message", "Hanya file PDF/DOC/DOCX yang diperbolehkan");
                return response;
            }
            
            // Validasi ukuran (max 5MB)
            if (rubrikFile.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("message", "Ukuran file maksimal 5MB");
                return response;
            }
            
            // TODO: Simpan file rubrik
            // String filePath = fileService.saveRubrikFile(rubrikFile, idTubes);
            // tugasBesarService.updateRubrikPath(idTubes, filePath);
            
            response.put("success", true);
            response.put("message", "Rubrik berhasil diupload!");
            response.put("redirect", "/dosen/buat-kelompok?kelasId=" + kelasId + "&idTubes=" + idTubes);
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    // ==================== STEP 4: BUAT KELOMPOK ====================
    @GetMapping("/buat-kelompok")
    public String showBuatKelompok(@RequestParam Integer kelasId,
                                @RequestParam Integer idTubes,
                                HttpSession session,
                                Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
            
            // Ambil daftar mahasiswa di kelas (jika ada service)
            // List<Mahasiswa> mahasiswaList = mahasiswaService.getByKelasId(kelasId);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            // model.addAttribute("mahasiswaList", mahasiswaList);
            
            return "buatKelompok"; // template: buatKelompok.html
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/simpan-kelompok")
    @ResponseBody
    public Map<String, Object> simpanKelompok(@RequestParam Integer kelasId,
                                            @RequestParam Integer idTubes,
                                            @RequestParam String kelompokData,
                                            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            response.put("redirect", "/login");
            return response;
        }
        
        try {
            // TODO: Parse JSON kelompokData dan simpan ke database
            // kelompokService.simpanKelompok(idTubes, kelompokData);
            
            response.put("success", true);
            response.put("message", "Kelompok berhasil disimpan!");
            response.put("redirect", "/dosen/penilaian?kelasId=" + kelasId + "&idTubes=" + idTubes);
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    // ==================== STEP 5: PENILAIAN ====================
    @GetMapping("/penilaian")
    public String showPenilaian(@RequestParam Integer kelasId,
                             @RequestParam Integer idTubes,
                             HttpSession session,
                             Model model) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            Kelas kelas = kelasService.findById(kelasId);
            TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
            
            // Ambil data kelompok untuk tugas ini
            // List<Kelompok> kelompokList = kelompokService.getByTugasId(idTubes);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            // model.addAttribute("kelompokList", kelompokList);
            
            return "penilaian"; // template: penilaian.html
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/simpan-nilai")
    @ResponseBody
    public Map<String, Object> simpanNilai(@RequestParam Integer kelasId,
                                        @RequestParam Integer idTubes,
                                        @RequestParam String nilaiData,
                                        HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            response.put("redirect", "/login");
            return response;
        }
        
        try {
            // TODO: Parse JSON nilaiData dan simpan ke database
            // penilaianService.simpanNilai(idTubes, nilaiData);
            
            response.put("success", true);
            response.put("message", "Nilai berhasil disimpan!");
            response.put("redirect", "/dosen/tubes?kelasId=" + kelasId);
            
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    // ==================== HAPUS TUGAS ====================
    @GetMapping("/delete-tugas")
    public String deleteTugas(@RequestParam Integer kelasId,
                           @RequestParam Integer idTubes,
                           HttpSession session,
                           RedirectAttributes ra) {
        
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        if (dosen == null) {
            return "redirect:/login";
        }
        
        try {
            tugasBesarService.deleteTugasBesar(idTubes);
            ra.addFlashAttribute("success", "Tugas berhasil dihapus");
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Gagal menghapus tugas: " + e.getMessage());
        }
        
        return "redirect:/dosen/tubes?kelasId=" + kelasId;
    }
    
    // ==================== GET TUGAS BY ID (AJAX) ====================
    @GetMapping("/get-tugas")
    @ResponseBody
    public Map<String, Object> getTugas(@RequestParam Integer idTubes,
                                     HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            return response;
        }
        
        try {
            TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
            response.put("success", true);
            response.put("tugas", tugasBesar);
            return response;
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return response;
        }
    }
}