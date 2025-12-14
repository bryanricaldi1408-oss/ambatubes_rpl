package com.example.admin.service;

import com.example.admin.entity.Kelompok;
import com.example.admin.entity.Kelompok;
import com.example.admin.repository.AnggotaKelompokRepository;
import com.example.admin.repository.KelompokRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KelompokService {

    private final KelompokRepository kelompokRepository;
    private final AnggotaKelompokRepository anggotaKelompokRepository;
    private final com.example.admin.repository.TugasBesarRepository tugasBesarRepository;
    private final com.example.admin.repository.PengambilanKelasRepository pengambilanKelasRepository;

    @Transactional
    public List<Kelompok> generateKelompok(Integer idTubes, Integer jumlahGrup, Integer maxAnggota, boolean isAutoAssign) {
        // 1. Hapus anggota yang sudah ada untuk tubes ini (bersih-bersih)
        anggotaKelompokRepository.deleteByIdTubes(idTubes);

        // 2. Hapus kelompok yang sudah ada untuk tubes ini (bersih-bersih)
        kelompokRepository.deleteByIdTubes(idTubes);
        
        // 3. Buat kelompok baru (Empty groups are always created)
        List<Kelompok> newGroups = new ArrayList<>();
        char groupName = 'A';
        
        for (int i = 0; i < jumlahGrup; i++) {
            Kelompok k = new Kelompok();
            k.setIdTubes(idTubes);
            k.setNamaKelompok(String.valueOf(groupName));
            k.setJumlahAnggota(maxAnggota); // Ini kapasitas max
            
            newGroups.add(k);
            groupName++;
        }
        
        // Simpan kelompok dulu untuk dapat ID
        newGroups = kelompokRepository.saveAll(newGroups);

        // 4. Jika Auto Assign, distribusi mahasiswa
        if (isAutoAssign) {
             // Ambil data Kelas ID dari Tugas Besar
            com.example.admin.entity.TugasBesar tubes = tugasBesarRepository.findById(idTubes).orElseThrow(() -> new RuntimeException("Tugas Besar not found"));
            Integer kelasId = tubes.getIdKelas();

            // Ambil semua mahasiswa di kelas tersebut
            List<com.example.admin.entity.PengambilanKelas> listMahasiswa = pengambilanKelasRepository.findByKelasWithMahasiswa(kelasId);
            List<com.example.admin.entity.Mahasiswa> students = new ArrayList<>();
            for (com.example.admin.entity.PengambilanKelas pk : listMahasiswa) {
                students.add(pk.getMahasiswa());
            }

            // Acak urutan mahasiswa
            java.util.Collections.shuffle(students);
            
            // Distribusi mahasiswa ke kelompok (Round Robin)
            List<com.example.admin.entity.AnggotaKelompok> newMembers = new ArrayList<>();
            int groupIndex = 0;
            
            for (com.example.admin.entity.Mahasiswa s : students) {
                Kelompok targetGroup = newGroups.get(groupIndex);
                
                com.example.admin.entity.AnggotaKelompok member = new com.example.admin.entity.AnggotaKelompok();
                member.setIdKelompok(targetGroup.getIdKelompok());
                member.setNpm(s.getNpm());
                
                newMembers.add(member);
                
                // Pindah ke grup berikutnya
                groupIndex = (groupIndex + 1) % jumlahGrup;
            }

            // Simpan anggota
            anggotaKelompokRepository.saveAll(newMembers);
        }

        return newGroups;
    }

    public List<Kelompok> getByTubesId(Integer idTubes) {
        return kelompokRepository.findByIdTubes(idTubes);
    }

    public Kelompok findByIdTubesAndNama(Integer idTubes, String namaKelompok) {
        return kelompokRepository.findByIdTubesAndNamaKelompok(idTubes, namaKelompok);
    }

    @Transactional
    public void deleteKelompok(Integer idKelompok) {
        // 1. Hapus anggotanya dulu (Cascade)
        anggotaKelompokRepository.deleteByIdKelompok(idKelompok);
        
        // 2. Hapus kelompoknya
        kelompokRepository.deleteById(idKelompok);
    }
}
