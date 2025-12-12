package com.example.admin.service;

import com.example.admin.entity.Kelompok;
import com.example.admin.entity.NilaiKelompok;
import com.example.admin.entity.NilaiMahasiswa;
import com.example.admin.repository.KelompokRepository;
import com.example.admin.repository.NilaiKelompokRepository;
import com.example.admin.repository.NilaiMahasiswaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PenilaianService {

    private final KelompokRepository kelompokRepository;
    private final NilaiKelompokRepository nilaiKelompokRepository;
    private final NilaiMahasiswaRepository nilaiMahasiswaRepository;

    @Transactional
    public void simpanNilai(Integer idTubes, List<Map<String, Object>> nilaiList) {
        for (Map<String, Object> item : nilaiList) {
            // Expected keys: npm, kelompok (letter), idKegiatan (Integer), nilai (Number), keterangan (String)
            String npm = (String) item.get("npm");
            String kelompokName = (String) item.get("kelompok");
            Integer idKegiatan = item.get("idKegiatan") == null ? null : ((Number) item.get("idKegiatan")).intValue();
            Number nilaiNum = (Number) item.get("nilai");
            Double nilai = nilaiNum == null ? null : nilaiNum.doubleValue();
            String keterangan = (String) item.get("keterangan");

            if (npm == null || kelompokName == null || idKegiatan == null || nilai == null) {
                continue; // skip malformed
            }

            Kelompok kelompok = kelompokRepository.findByIdTubesAndNamaKelompok(idTubes, kelompokName);
            if (kelompok == null) {
                continue; // cannot find kelompok
            }

            // Find or create NilaiKelompok for this kelompok-kegiatan
            NilaiKelompok nilaiKelompok = nilaiKelompokRepository.findByIdKelompokAndIdKegiatan(kelompok.getIdKelompok(), idKegiatan);
            if (nilaiKelompok == null) {
                nilaiKelompok = new NilaiKelompok();
                nilaiKelompok.setIdKelompok(kelompok.getIdKelompok());
                nilaiKelompok.setIdKegiatan(idKegiatan);
                nilaiKelompok = nilaiKelompokRepository.save(nilaiKelompok);
            }

            // Find existing NilaiMahasiswa for this npm and nilaiKelompok
            NilaiMahasiswa nm = nilaiMahasiswaRepository.findByNpmAndIdNilaiKelompok(npm, nilaiKelompok.getIdNilaiKelompok());
            if (nm == null) {
                nm = new NilaiMahasiswa();
                nm.setNpm(npm);
                nm.setIdNilaiKelompok(nilaiKelompok.getIdNilaiKelompok());
            }

            nm.setNilai(nilai);
            nm.setKeterangan(keterangan);
            nilaiMahasiswaRepository.save(nm);
        }
    }
}
