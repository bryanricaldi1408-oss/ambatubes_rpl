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
    private final com.example.admin.repository.NilaiKelompokRepository nilaiKelompokRepository;
    private final com.example.admin.repository.NilaiMahasiswaRepository nilaiMahasiswaRepository;

    @Transactional
    public List<Kelompok> generateKelompok(Integer idTubes, Integer jumlahGrup, Integer maxAnggota, boolean isAutoAssign) {
        // 1. Hapus anggota yang sudah ada untuk tubes ini
        anggotaKelompokRepository.deleteByIdTubes(idTubes);

        // 2. Hapus kelompok yang sudah ada untuk tubes ini 
        kelompokRepository.deleteByIdTubes(idTubes);
        
        // 3. Buat kelompok baru 
        List<Kelompok> newGroups = new ArrayList<>();
        char groupName = 'A';
        
        for (int i = 0; i < jumlahGrup; i++) {
            Kelompok k = new Kelompok();
            k.setIdTubes(idTubes);
            k.setNamaKelompok(String.valueOf(groupName));
            k.setJumlahAnggota(maxAnggota); 
            
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
            
            // Distribusi mahasiswa ke kelompok (Round Robin with Capacity Check)
            List<com.example.admin.entity.AnggotaKelompok> newMembers = new ArrayList<>();
            int groupIndex = 0;
            
            // Track current member count for each group
            int[] groupMemberCounts = new int[jumlahGrup];
            
            for (com.example.admin.entity.Mahasiswa s : students) {
                // Find a group with available space
                int attempts = 0;
                boolean assigned = false;
                
                while (attempts < jumlahGrup) {
                    if (groupMemberCounts[groupIndex] < maxAnggota) {
                        Kelompok targetGroup = newGroups.get(groupIndex);
                        
                        com.example.admin.entity.AnggotaKelompok member = new com.example.admin.entity.AnggotaKelompok();
                        member.setIdKelompok(targetGroup.getIdKelompok());
                        member.setNpm(s.getNpm());
                        
                        newMembers.add(member);
                        groupMemberCounts[groupIndex]++;
                        assigned = true;
                        
                        // Move to next group for round-robin
                        groupIndex = (groupIndex + 1) % jumlahGrup;
                        break;
                    }
                    
                    // Try next group
                    groupIndex = (groupIndex + 1) % jumlahGrup;
                    attempts++;
                }
                
                if (!assigned) {
                    // All groups are full. Stop assigning.
                    break; 
                }
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

    public com.example.admin.entity.Kelompok findById(Integer idKelompok) {
        return kelompokRepository.findById(idKelompok).orElse(null);
    }

    @Transactional
    public void deleteKelompok(Integer idKelompok) {
        // 1. Hapus Nilai Mahasiswa terkait
        List<com.example.admin.entity.NilaiKelompok> nilaiKelompokList = nilaiKelompokRepository.findByIdKelompok(idKelompok);
        if (!nilaiKelompokList.isEmpty()) {
            List<Integer> ids = new ArrayList<>();
            for (com.example.admin.entity.NilaiKelompok nk : nilaiKelompokList) {
                ids.add(nk.getIdNilaiKelompok());
            }
            nilaiMahasiswaRepository.deleteByIdNilaiKelompokIn(ids);
        }

        // 2. Hapus Nilai Kelompok
        nilaiKelompokRepository.deleteByIdKelompok(idKelompok);

        // 3. Hapus anggotanya
        anggotaKelompokRepository.deleteByIdKelompok(idKelompok);
        
        // 4. Hapus kelompoknya
        kelompokRepository.deleteById(idKelompok);
    }
}
