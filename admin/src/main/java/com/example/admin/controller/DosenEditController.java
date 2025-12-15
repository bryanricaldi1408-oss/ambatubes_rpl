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
import com.example.admin.entity.PengambilanKelas;
import com.example.admin.entity.AnggotaKelompok;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.*;
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
    
    private final RubrikService rubrikService;
    private final KelasService kelasService;
    private final TugasBesarService tugasBesarService;
    private final ExcelParseJadwalService ExcelParseJadwalService;
    private final com.example.admin.service.KelompokService kelompokService;
    private final com.example.admin.service.PenilaianService penilaianService;
    private final com.example.admin.repository.AnggotaKelompokRepository anggotaKelompokRepository;
    private final com.example.admin.repository.PengambilanKelasRepository pengambilanKelasRepository;
    private final com.example.admin.repository.NilaiKelompokRepository nilaiKelompokRepository;
    private final com.example.admin.repository.NilaiMahasiswaRepository nilaiMahasiswaRepository;

    // ===============step 1:bagian daftar tubes===============
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
            model.addAttribute("listTubes", listTubes); 
            
            return "dosenTubes";
            
        } catch (Exception e) {
            model.addAttribute("error", "Kelas tidak ditemukan");
            return "error";
        }
    }
    
    //bikin tubes baru dengan nama dan desk
    @GetMapping("/edit")
    public String formTugas(@RequestParam Integer kelasId,
                          @RequestParam(required = false) Integer idTubes,
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
            
            if (idTubes != null) {
                TugasBesar tugasBesar = tugasBesarService.getTugasById(idTubes);
                model.addAttribute("tugasBesar", tugasBesar);
                model.addAttribute("idTubes", idTubes);
                model.addAttribute("isEditMode", true);
            } else {
                model.addAttribute("isEditMode", false);
            }
            
            return "dosenEdit";
            
        } catch (Exception e) {
            model.addAttribute("error", "Kelas tidak ditemukan");
            return "error";
        }
    }
    
    // tugasnya simpen dulu
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
            TugasBesar tugas = tugasBesarService.createTugasBesar(kelasId, namaTugas, deskripsi);
            
            if (tugas == null || tugas.getIdTubes() == null) {
                ra.addFlashAttribute("error", "Gagal menyimpan tugas");
                return "redirect:/dosen/edit?kelasId=" + kelasId;
            }
            
            ra.addFlashAttribute("success", "Tugas berhasil disimpan!");
            return "redirect:/dosen/upload-jadwal?kelasId=" + kelasId + "&idTubes=" + tugas.getIdTubes();
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dosen/edit?kelasId=" + kelasId;
        }
    }
    
    //klo mau diupdate sebelom ke step 2
    @PostMapping("/update-tugas")
    public String updateTugas(@RequestParam Integer kelasId,
                           @RequestParam Integer idTubes,
                           @RequestParam String namaTugas,
                           @RequestParam(required = false) String deskripsi,
                           RedirectAttributes ra) {
        
        try {
            tugasBesarService.updateTugasBesar(idTubes, namaTugas, deskripsi);
            
            ra.addFlashAttribute("success", "Tugas berhasil diupdate!");
            return "redirect:/dosen/edit?kelasId=" + kelasId + "&idTubes=" + idTubes;
            
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/dosen/edit?kelasId=" + kelasId + "&idTubes=" + idTubes;
        }
    }
    
    //================stef 2: upload jadwal================
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
            List<com.example.admin.entity.Kelompok> kelompokList = kelompokService.getByTubesId(idTubes);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            model.addAttribute("kelompokList", kelompokList);
            
            return "uploadJadwal";
            
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
            
            //parse excelnya
            List<JadwalNilaiDto> parsedData = ExcelParseJadwalService.parseAndSaveExcel(jadwalFile, idTubes);
            
            //masukin ke session
            session.setAttribute("parsedJadwalData_" + idTubes, parsedData);
            
            //perlu format tertentu nanti pas buat tabelnya
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
            e.printStackTrace();
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
            //ilangin dr db
            ExcelParseJadwalService.deleteExistingData(idTubes);
            
            //ilangin dr sessionnya
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

    //di check ada jadwalnya udh ad blom biar masih ngasi liat kalo misalnya keluar
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
            // ada di session g
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
            
            // klo g ada cek database
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
    
    // ==================== step 3: upload rubrik ====================
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
            List<com.example.admin.entity.Kelompok> kelompokList = kelompokService.getByTubesId(idTubes);
            
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            model.addAttribute("kelompokList", kelompokList);
            
            return "uploadRubrik"; 
            
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
            //mirip2 kek upload excel lah
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
            
            if (rubrikFile.getSize() > 10 * 1024 * 1024) { 
                response.put("success", false);
                response.put("message", "Ukuran file maksimal 10MB");
                return response;
            }
            
            //method untuk nyimpen di folder static/uploads
            String filePath = rubrikService.storeFile(rubrikFile, idTubes);
            
            //simpen seesion
            session.setAttribute("rubrikFilePath_" + idTubes, filePath);
            
            response.put("success", true);
            response.put("message", "Rubrik berhasil diupload!");
            response.put("filePath", filePath);
            
            return response;
            
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }
    
    // ==================== step 4 buat kelompok ====================
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
                    
            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            
            //klompok yg udh kebentuk masukin tbael
            List<com.example.admin.entity.Kelompok> groups = kelompokService.getByTubesId(idTubes);
            List<Map<String, Object>> formattedGroups = new ArrayList<>();
            
            for (com.example.admin.entity.Kelompok k : groups) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", k.getIdKelompok());
                map.put("nama", "Kelompok " + k.getNamaKelompok());
                map.put("maxAnggota", k.getJumlahAnggota());
                
                //ambil anggotanya siapa aj
                List<com.example.admin.entity.Mahasiswa> members = anggotaKelompokRepository.findMahasiswaByKelompok(k.getIdKelompok());
                map.put("jumlahAnggota", members.size());
                
                List<Map<String, String>> memberList = new ArrayList<>();
                for (com.example.admin.entity.Mahasiswa m : members) {
                    Map<String, String> mMap = new HashMap<>();
                    mMap.put("name", m.getNama());
                    mMap.put("npm", m.getNpm());
                    memberList.add(mMap);
                }
                map.put("anggota", memberList);
                
                formattedGroups.add(map);
            }
            
            model.addAttribute("existingGroups", formattedGroups);
            model.addAttribute("hasExistingGroups", !formattedGroups.isEmpty());
            
            return "dosenBuatKelompok";
            
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
                                            @RequestParam(defaultValue = "true") Boolean isAutoAssign,
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
            //bikin klompok
            List<com.example.admin.entity.Kelompok> groups = kelompokService.generateKelompok(idTubes, jumlahGrup, jumlahAnggota, isAutoAssign);
            List<Map<String, Object>> groupsData = new ArrayList<>();
            
            for (com.example.admin.entity.Kelompok g : groups) {
                Map<String, Object> map = new HashMap<>();
                map.put("nama", "Kelompok " + g.getNamaKelompok());
                map.put("id", g.getIdKelompok());
                map.put("maxAnggota", g.getJumlahAnggota());
                
                // Fetch members for this group
                List<com.example.admin.entity.Mahasiswa> members = anggotaKelompokRepository.findMahasiswaByKelompok(g.getIdKelompok());
                map.put("jumlahAnggota", members.size());
                
                List<Map<String, String>> memberList = new ArrayList<>();
                for (com.example.admin.entity.Mahasiswa m : members) {
                    Map<String, String> mMap = new HashMap<>();
                    mMap.put("name", m.getNama());
                    mMap.put("npm", m.getNpm());
                    memberList.add(mMap);
                }
                map.put("anggota", memberList);
                
                groupsData.add(map);
            }

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

        //buat tambah member ke kelompok
    @PostMapping("/kelompok/add-member")
    @ResponseBody
    public Map<String, Object> addMember(@RequestParam String npm, @RequestParam Integer idKelompok) {
        Map<String, Object> response = new HashMap<>();
        try {
            com.example.admin.entity.Kelompok kelompok = kelompokService.findById(idKelompok);
            if (kelompok == null) {
                response.put("success", false);
                response.put("message", "Kelompok tidak ditemukan.");
                return response;
            }
            //cek udh penuh blom kalo udh g tambahin
            int currentMembers = anggotaKelompokRepository.countAnggotaByKelompok(idKelompok);
            if (currentMembers >= kelompok.getJumlahAnggota()) {
                response.put("success", false);
                response.put("message", "Gagal: Kelompok sudah penuh (Max: " + kelompok.getJumlahAnggota() + ").");
                return response;
            }

            //klo udh tambah simpen
            AnggotaKelompok newMember = new AnggotaKelompok();
            newMember.setIdKelompok(idKelompok);
            newMember.setNpm(npm);
            anggotaKelompokRepository.save(newMember);
            
            response.put("success", true);
            response.put("message", "Anggota berhasil ditambahkan.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
        }
        return response;
    }

        //klo udh selesai masalah klompoknya bakal di redirect ke halaman selanjutnya
    @PostMapping("/selesai-kelompok")
    @ResponseBody
    public Map<String, Object> selesaiKelompok(@RequestParam Integer kelasId,
                                                @RequestParam Integer idTubes) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Konfigurasi kelompok selesai.");
        response.put("redirect", "/dosen/edit?kelasId=" + kelasId + "&idTubes=" + idTubes);
        return response;
    }

    //ngilangin member dr klompok
    @PostMapping("/kelompok/remove-member")
    @ResponseBody
    public Map<String, Object> removeMember(@RequestParam String npm, @RequestParam Integer idKelompok) {
        Map<String, Object> response = new HashMap<>();
        try {
            anggotaKelompokRepository.deleteByNpmAndIdKelompok(npm, idKelompok);
            response.put("success", true);
            response.put("message", "Anggota berhasil dihapus.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Gagal menghapus: " + e.getMessage());
        }
        return response;
    }

    //method buat cari anggota yg blom masuk klompok
    @GetMapping("/kelompok/available-students")
    @ResponseBody
    public List<Map<String, String>> getAvailableStudents(@RequestParam Integer idTubes, @RequestParam Integer kelasId) {
        //cari anggota di kelas itu
        List<PengambilanKelas> allStudentsInClass = pengambilanKelasRepository.findByKelasWithMahasiswa(kelasId);
        
        //cari anggota yg udh di kelompok
        List<com.example.admin.entity.Kelompok> groups = kelompokService.getByTubesId(idTubes);
        Set<String> studentsInGroups = new HashSet<>();
        for(com.example.admin.entity.Kelompok k : groups) {
            List<com.example.admin.entity.Mahasiswa> members = anggotaKelompokRepository.findMahasiswaByKelompok(k.getIdKelompok());
            for(com.example.admin.entity.Mahasiswa m : members) {
                studentsInGroups.add(m.getNpm());
            }
        }
        
        //return yg blom masuk
        List<Map<String, String>> available = new ArrayList<>();
        for(PengambilanKelas pk : allStudentsInClass) {
            if(!studentsInGroups.contains(pk.getMahasiswa().getNpm())) {
                Map<String, String> s = new HashMap<>();
                s.put("npm", pk.getMahasiswa().getNpm());
                s.put("name", pk.getMahasiswa().getNama());
                available.add(s);
            }
        }
        return available;
    }

        //delete kelompok klo dosen salah input
    @DeleteMapping("/delete-kelompok/{id}")
    @ResponseBody
    public Map<String, Object> deleteKelompok(@PathVariable Integer id, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Dosen dosen = (Dosen) session.getAttribute("dosen");
        
        if (dosen == null) {
            response.put("success", false);
            response.put("message", "Session expired");
            return response;
        }

        try {
            kelompokService.deleteKelompok(id);
            response.put("success", true);
            response.put("message", "Kelompok berhasil dihapus");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Gagal menghapus kelompok: " + e.getMessage());
        }
        
        return response;
    }

    
    // ==================== step 5: penilaian ====================
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
            
            // Ambil data kelompok
            List<com.example.admin.entity.Kelompok> kelompokList = kelompokService.getByTubesId(idTubes);
            // Ambil data jadwal/kegiatan 
            List<com.example.admin.dto.JadwalNilaiDto> jadwalList = ExcelParseJadwalService.getParsedDataForTubes(idTubes);

            model.addAttribute("dosen", dosen);
            model.addAttribute("kelas", kelas);
            model.addAttribute("tugasBesar", tugasBesar);
            model.addAttribute("idTubes", idTubes);
            model.addAttribute("kelasId", kelasId);
            model.addAttribute("kelompokList", kelompokList);
            model.addAttribute("jadwalList", jadwalList);
            
            return "dosenPenilaian";
            
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
    
    //nilainya disimpen klo udh dikasi
    @PostMapping("/simpan-nilai")
    @ResponseBody
    public Map<String, Object> simpanNilai(@RequestBody Map<String, Object> payload,
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
            Integer idTubes = null;
            Integer kelasId = null;

            if (payload.get("idTubes") instanceof Number) {
                idTubes = ((Number) payload.get("idTubes")).intValue();
            }
            if (payload.get("kelasId") instanceof Number) {
                kelasId = ((Number) payload.get("kelasId")).intValue();
            }

            Object nilaiObj = payload.get("nilaiData");
            List<java.util.Map<String, Object>> list = new ArrayList<>();
            if (nilaiObj instanceof List) {
                list = (List<java.util.Map<String, Object>>) nilaiObj;
            }

            if (idTubes == null) {
                response.put("success", false);
                response.put("message", "idTubes is required");
                return response;
            }

            penilaianService.simpanNilai(idTubes, list);

            response.put("success", true);
            response.put("message", "Nilai berhasil disimpan!");
            response.put("redirect", "/dosen/tubes?kelasId=" + (kelasId == null ? "" : kelasId));

            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());
            return response;
        }
    }



    //ngasi nilai kelompok
    @GetMapping("/penilaian/kelompok-nilai")
    @ResponseBody
    public List<Map<String, Object>> getKelompokNilai(@RequestParam Integer idTubes,
                                                      @RequestParam Integer idKegiatan,
                                                      @RequestParam String namaKelompok) {
        List<Map<String, Object>> result = new ArrayList<>();

        //dapetin dulu klompoknnya
        com.example.admin.entity.Kelompok kelompok = kelompokService.findByIdTubesAndNama(idTubes, namaKelompok);
        if (kelompok == null) {
            return result;
        }

        //udd pernah ada nilainya blom
        com.example.admin.entity.NilaiKelompok nk = nilaiKelompokRepository.findByIdKelompokAndIdKegiatan(kelompok.getIdKelompok(), idKegiatan);

        //ambil anggotanya
        List<com.example.admin.entity.Mahasiswa> members = anggotaKelompokRepository.findMahasiswaByKelompok(kelompok.getIdKelompok());
        for (com.example.admin.entity.Mahasiswa m : members) {
            Map<String, Object> map = new HashMap<>();
            map.put("npm", m.getNpm());
            map.put("name", m.getNama());

            Double nilai = null;
            String keterangan = null;
            Double groupNilai = null;
            String groupKeterangan = null;
            if (nk != null) {
                groupNilai = nk.getNilai();
                groupKeterangan = nk.getKeterangan();
                com.example.admin.entity.NilaiMahasiswa nm = nilaiMahasiswaRepository.findByNpmAndIdNilaiKelompok(m.getNpm(), nk.getIdNilaiKelompok());
                if (nm != null) {
                    nilai = nm.getNilai();
                    keterangan = nm.getKeterangan();
                }
            }

            map.put("nilai", nilai);
            map.put("keterangan", keterangan == null ? "" : keterangan);
            map.put("groupNilai", groupNilai);
            map.put("groupKeterangan", groupKeterangan == null ? "" : groupKeterangan);
            result.add(map);
        }

        return result;
    }
    
    // udh g dipake harusnya
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
    
    // udh g dipake harusnya
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