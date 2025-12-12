package com.example.admin.service;
import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.entity.Jadwal;
import com.example.admin.entity.Kegiatan;
import com.example.admin.repository.JadwalRepository;
import com.example.admin.repository.KegiatanRepository;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelParseJadwalService {
    
    private final JadwalRepository jadwalRepository;
    private final KegiatanRepository kegiatanRepository;
    
    public ExcelParseJadwalService(JadwalRepository jadwalRepository, 
                              KegiatanRepository kegiatanRepository) {
        this.jadwalRepository = jadwalRepository;
        this.kegiatanRepository = kegiatanRepository;
    }
    
    @Transactional
    public List<JadwalNilaiDto> parseAndSaveExcel(MultipartFile file, Integer idTubes) throws IOException {
        List<JadwalNilaiDto> parsedData = new ArrayList<>();
        
        // First, delete existing data for this idTubes
        deleteExistingData(idTubes);
        
        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row (row index 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Cell deadlineCell = row.getCell(0);
                Cell kegiatanCell = row.getCell(1);
                
                if (deadlineCell == null || kegiatanCell == null) continue;
                
                // Parse deadline from Excel cell
                LocalDateTime deadline = parseDeadlineCell(deadlineCell);
                String namaKegiatan = kegiatanCell.getStringCellValue().trim();
                
                if (namaKegiatan.isEmpty()) continue;
                
                // Save to database
                Jadwal savedJadwal = saveJadwal(deadline, idTubes);
                saveKegiatan(namaKegiatan, savedJadwal.getIdJadwal());
                
                // Add to parsed data DTO
                parsedData.add(new JadwalNilaiDto(deadline, namaKegiatan, null, null));
            }
            
            workbook.close();
        }
        
        return parsedData;
    }
    
    private LocalDateTime parseDeadlineCell(Cell cell) {
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                // Excel date format
                return cell.getDateCellValue().toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime();
            } else {
                // Try parsing as string
                String dateString = cell.getStringCellValue().trim();
                // Try multiple date formats
                DateTimeFormatter[] formatters = {
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"),
                    DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss"),
                    DateTimeFormatter.ISO_LOCAL_DATE_TIME
                };
                
                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDateTime.parse(dateString, formatter);
                    } catch (Exception e) {
                        // Try next format
                    }
                }
                
                // If we can't parse, use current time
                return LocalDateTime.now();
            }
        } catch (Exception e) {
            // Log error and return current time as fallback
            return LocalDateTime.now();
        }
    }
    
    private Jadwal saveJadwal(LocalDateTime deadline, Integer idTubes) {
        Jadwal jadwal = new Jadwal();
        jadwal.setDeadline(deadline);
        jadwal.setIdTubes(idTubes);
        return jadwalRepository.save(jadwal);
    }
    
    private Kegiatan saveKegiatan(String namaKegiatan, Integer idJadwal) {
        Kegiatan kegiatan = new Kegiatan();
        kegiatan.setNamaKegiatan(namaKegiatan);
        kegiatan.setIdJadwal(idJadwal);
        return kegiatanRepository.save(kegiatan);
    }
    
    @Transactional
    public void deleteExistingData(Integer idTubes) {
        // Find all jadwal for this tubes
        List<Jadwal> existingJadwal = jadwalRepository.findByIdTubes(idTubes);
        
        if (!existingJadwal.isEmpty()) {
            // Get all idJadwal
            List<Integer> idJadwalList = existingJadwal.stream()
                    .map(Jadwal::getIdJadwal)
                    .toList();
            
            // Delete kegiatan first (due to foreign key constraint)
            kegiatanRepository.deleteByIdJadwalIn(idJadwalList);
            
            // Delete jadwal
            jadwalRepository.deleteByIdTubes(idTubes);
        }
    }
    
    public List<JadwalNilaiDto> getParsedDataForTubes(Integer idTubes) {
        List<JadwalNilaiDto> result = new ArrayList<>();
        
        List<Jadwal> jadwalList = jadwalRepository.findByIdTubes(idTubes);
        for (Jadwal jadwal : jadwalList) {
            List<Kegiatan> kegiatanList = kegiatanRepository.findByIdJadwal(jadwal.getIdJadwal());
            for (Kegiatan kegiatan : kegiatanList) {
                result.add(new JadwalNilaiDto(
                    jadwal.getDeadline(),
                    kegiatan.getNamaKegiatan(),
                    null,
                    null
                ));
            }
        }
        
        return result;
    }
}