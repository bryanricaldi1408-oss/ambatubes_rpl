package com.example.admin.service;

import lombok.Data;
import java.util.HashMap;
import java.util.Map;

@Data
public class ImportResult {
    private boolean success;
    private String message;
    
    private Map<String, Integer> mataKuliahStats = new HashMap<>();
    private Map<String, Integer> dosenStats = new HashMap<>();
    private Map<String, Integer> mahasiswaStats = new HashMap<>();
    private Map<String, Integer> kelasStats = new HashMap<>();
    private Map<String, Integer> pengajaranStats = new HashMap<>();
    private Map<String, Integer> pengambilanStats = new HashMap<>();
    
    public ImportResult() {
        // Initialize all stats with 0
        mataKuliahStats.put("imported", 0);
        mataKuliahStats.put("skipped", 0);
        dosenStats.put("imported", 0);
        dosenStats.put("skipped", 0);
        mahasiswaStats.put("imported", 0);
        mahasiswaStats.put("skipped", 0);
        kelasStats.put("imported", 0);
        kelasStats.put("skipped", 0);
        pengajaranStats.put("imported", 0);
        pengajaranStats.put("skipped", 0);
        pengambilanStats.put("imported", 0);
        pengambilanStats.put("skipped", 0);
    }
}