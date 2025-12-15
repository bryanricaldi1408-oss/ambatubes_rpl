package com.example.admin.service;

import com.example.admin.entity.Kelompok;
import com.example.admin.entity.NilaiKelompok;
import com.example.admin.entity.NilaiMahasiswa;
import com.example.admin.repository.KelompokRepository;
import com.example.admin.repository.NilaiKelompokRepository;
import com.example.admin.repository.NilaiMahasiswaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenilaianService {

    private final KelompokRepository kelompokRepository;
    private final NilaiKelompokRepository nilaiKelompokRepository;
    private final NilaiMahasiswaRepository nilaiMahasiswaRepository;
    private final com.example.admin.repository.AnggotaKelompokRepository anggotaKelompokRepository;

    @Transactional
    public void simpanNilai(Integer idTubes, List<Map<String, Object>> nilaiList) {
        for (Map<String, Object> item : nilaiList) {
            log.info("simpanNilai() item: {}", item);
            
            String npm = (String) item.get("npm");
            String kelompokName = (String) item.get("kelompok");
            Integer idKegiatan = item.get("idKegiatan") == null ? null : ((Number) item.get("idKegiatan")).intValue();
            Number nilaiNum = (Number) item.get("nilai");
            Double nilai = nilaiNum == null ? null : nilaiNum.doubleValue();
            String keterangan = (String) item.get("keterangan");

            
            if (kelompokName == null || idKegiatan == null || nilai == null) {
                log.warn("Skipping malformed nilai entry (missing required fields): {}", item);
                continue;
            }

            Kelompok kelompok = kelompokRepository.findByIdTubesAndNamaKelompok(idTubes, kelompokName);
            if (kelompok == null) {
                continue; //klo klompok g nemu
            }

            if (npm != null) {
                log.info("Processing perorangan: npm={} kelompok={} idKegiatan={} nilai={}", npm, kelompokName, idKegiatan, nilai);
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
            } else {
                // Treat as kelompok-level input: set NilaiKelompok and propagate to members
                log.info("Processing kelompok-level for kelompok={} idKegiatan={} nilai={}", kelompokName, idKegiatan, nilai);
                NilaiKelompok nilaiKelompok = nilaiKelompokRepository.findByIdKelompokAndIdKegiatan(kelompok.getIdKelompok(), idKegiatan);
                if (nilaiKelompok == null) {
                    nilaiKelompok = new NilaiKelompok();
                    nilaiKelompok.setIdKelompok(kelompok.getIdKelompok());
                    nilaiKelompok.setIdKegiatan(idKegiatan);
                }

                nilaiKelompok.setNilai(nilai);
                nilaiKelompok.setKeterangan(keterangan);
                nilaiKelompok = nilaiKelompokRepository.save(nilaiKelompok);

                // Propagate nilai to each anggota as NilaiMahasiswa
                List<com.example.admin.entity.Mahasiswa> anggota = anggotaKelompokRepository.findMahasiswaByKelompok(kelompok.getIdKelompok());
                for (com.example.admin.entity.Mahasiswa m : anggota) {
                    NilaiMahasiswa nm = nilaiMahasiswaRepository.findByNpmAndIdNilaiKelompok(m.getNpm(), nilaiKelompok.getIdNilaiKelompok());
                    if (nm == null) {
                        nm = new NilaiMahasiswa();
                        nm.setNpm(m.getNpm());
                        nm.setIdNilaiKelompok(nilaiKelompok.getIdNilaiKelompok());
                    }
                    nm.setNilai(nilai);
                    nm.setKeterangan(keterangan);
                    nilaiMahasiswaRepository.save(nm);
                }
            }
        }
    }
}
