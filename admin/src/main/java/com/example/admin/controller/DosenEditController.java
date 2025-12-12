package com.example.admin.controller;

import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.entity.Dosen;
import com.example.admin.entity.Kelas;
import com.example.admin.entity.TugasBesar;
import com.example.admin.service.DosenService;
import com.example.admin.service.KelasService;
import com.example.admin.service.RubrikService;
import com.example.admin.service.TugasBesarService;
import com.example.admin.service.ExcelParseJadwalService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

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
    private final RubrikService rubrikService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final ExcelParseJadwalService ExcelParseJadwalService;
    private final com.example.admin.service.KelompokService kelompokService;

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
            List<TugasBesar> listTubes = tugasBesarService.getAllTugasByKelas(kelasId);

            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("kelasId", kelasId);
            model.addAttribute("listTubes", listTubes); // Add this line
            
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
            
            // Parse and save Excel data
            List<JadwalNilaiDto> parsedData = ExcelParseJadwalService.parseAndSaveExcel(jadwalFile, idTubes);
            
            // Store in session for potential page refresh
            session.setAttribute("parsedJadwalData_" + idTubes, parsedData);
            
            // Format dates for display
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            List<Map<String, Object>> formattedData = parsedData.stream()
                .map(dto -> {
                    Map<String, Object> row = new HashMap<>();
                    row.put("deadline", dto.getDeadline().format(formatter));
                    row.put("namaKegiatan", dto.getNamaKegiatan());
                    row.put("nilai", "-");
                    row.put("keterangan", "-");
                    return row;
                })
                .collect(Collectors.toList());
            
            response.put("success", true);
            response.put("message", "Jadwal berhasil diupload! " + parsedData.size() + " entri diproses.");
            response.put("data", formattedData);
            response.put("dataCount", parsedData.size());
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace(); // For debugging
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    @PostMapping("/delete-jadwal")
    @ResponseBody
    public Map<String, Object> deleteJadwal(@RequestParam Integer idTubes, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            response.put("redirect", "/login");
            return response;
        }
        
        try {
            // Delete the data from database
            ExcelParseJadwalService.deleteExistingData(idTubes);
            
            // Remove from session
            session.removeAttribute("parsedJadwalData_" + idTubes);
            
            response.put("success", true);
            response.put("message", "Jadwal berhasil dihapus");
            return response;
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }

    @GetMapping("/check-existing-jadwal")
    @ResponseBody
    public Map<String, Object> checkExistingJadwal(@RequestParam Integer idTubes, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("hasData", false);
            return response;
        }
        
        try {
            // Check if there's data in session first
            String sessionKey = "parsedJadwalData_" + idTubes;
            List<JadwalNilaiDto> sessionData = (List<JadwalNilaiDto>) session.getAttribute(sessionKey);
            
            if (sessionData != null && !sessionData.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                List<Map<String, Object>> formattedData = sessionData.stream()
                    .map(dto -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("deadline", dto.getDeadline().format(formatter));
                        row.put("namaKegiatan", dto.getNamaKegiatan());
                        row.put("nilai", "-");
                        row.put("keterangan", "-");
                        return row;
                    })
                    .collect(Collectors.toList());
                
                response.put("hasData", true);
                response.put("data", formattedData);
                response.put("dataCount", sessionData.size());
                return response;
            }
            
            // If no session data, check database
            List<JadwalNilaiDto> dbData = ExcelParseJadwalService.getParsedDataForTubes(idTubes);
            if (!dbData.isEmpty()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
                List<Map<String, Object>> formattedData = dbData.stream()
                    .map(dto -> {
                        Map<String, Object> row = new HashMap<>();
                        row.put("deadline", dto.getDeadline().format(formatter));
                        row.put("namaKegiatan", dto.getNamaKegiatan());
                        row.put("nilai", "-");
                        row.put("keterangan", "-");
                        return row;
                    })
                    .collect(Collectors.toList());
                
                response.put("hasData", true);
                response.put("data", formattedData);
                response.put("dataCount", dbData.size());
                return response;
            }
            
            response.put("hasData", false);
            return response;
            
        } catch (Exception e) {
            response.put("hasData", false);
            response.put("error", e.getMessage());
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
            // Basic validations
            if (rubrikFile.isEmpty()) {
                response.put("success", false);
                response.put("message", "File tidak boleh kosong");
                return response;
            }
            
            String fileName = rubrikFile.getOriginalFilename();
            if (fileName == null || !fileName.toLowerCase().endsWith(".pdf")) {
                response.put("success", false);
                response.put("message", "Hanya file PDF yang diperbolehkan");
                return response;
            }
            
            if (rubrikFile.getSize() > 10 * 1024 * 1024) { // 10MB
                response.put("success", false);
                response.put("message", "Ukuran file maksimal 10MB");
                return response;
            }
            
            // Store file - FIXED: Use instance method
            String filePath = rubrikService.storeFile(rubrikFile, idTubes);
            
            // Store file path in session for potential use
            session.setAttribute("rubrikFilePath_" + idTubes, filePath);
            
            response.put("success", true);
            response.put("message", "Rubrik berhasil diupload!");
            response.put("filePath", filePath);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace(); // Add this for debugging
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
            
            return "dosenBuatKelompok"; // template: dosenBuatKelompok.html
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    @PostMapping("/generate-kelompok")
    @ResponseBody
    public Map<String, Object> generateKelompok(@RequestParam Integer kelasId,
                                            @RequestParam Integer idTubes,
                                            @RequestParam Integer jumlahGrup,
                                            @RequestParam Integer jumlahAnggota,
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
            // Generate kelompok using service
            List<com.example.admin.entity.Kelompok> groups = kelompokService.generateKelompok(idTubes, jumlahGrup, jumlahAnggota);
            
            // Format response matching frontend expectation
            List<Map<String, Object>> groupsData = groups.stream().map(g -> {
                Map<String, Object> map = new HashMap<>();
                map.put("nama", "Kelompok " + g.getNamaKelompok());
                map.put("jumlahAnggota", 0); // Baru dibuat, pasti 0
                map.put("maxAnggota", g.getJumlahAnggota());
                map.put("anggota", new ArrayList<>());
                return map;
            }).collect(Collectors.toList());

            response.put("success", true);
            response.put("message", "Kelompok berhasil digenerate!");
            response.put("groups", groupsData);
            
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