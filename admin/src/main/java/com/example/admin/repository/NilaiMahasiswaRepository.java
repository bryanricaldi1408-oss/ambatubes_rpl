package com.example.admin.repository;

import com.example.admin.dto.JadwalNilaiDto;
import com.example.admin.entity.NilaiMahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NilaiMahasiswaRepository extends JpaRepository<NilaiMahasiswa, Integer> {

    @Query("SELECT new com.example.admin.dto.JadwalNilaiDto(" +
           "k.idKegiatan, j.deadline, k.namaKegiatan, nm.nilai, nm.keterangan) " +
           "FROM NilaiMahasiswa nm " +
           "JOIN nm.nilaiKelompok nk " +   // Join ke Nilai Kelompok
           "JOIN nk.kegiatan k " +         // Join ke Kegiatan
           "JOIN k.jadwal j " +            // Join ke Jadwal
           "WHERE nm.npm = :npm AND j.idTubes = :idTubes")
    List<JadwalNilaiDto> findJadwalDanNilai(@Param("npm") String npm, 
                                            @Param("idTubes") Integer idTubes);

       NilaiMahasiswa findByNpmAndIdNilaiKelompok(String npm, Integer idNilaiKelompok);
}