package com.example.admin.repository;

import com.example.admin.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {
    
    // Cari semua tugas berdasarkan kelasId (menggunakan idKelas langsung)
    @Query("SELECT t FROM TugasBesar t WHERE t.idKelas = :kelasId")
    List<TugasBesar> findByKelasId(@Param("kelasId") Integer kelasId);
    
    // Cari tugas berdasarkan kelasId dan nama
    @Query("SELECT t FROM TugasBesar t WHERE t.idKelas = :kelasId AND t.namaTugas = :namaTugas")
    Optional<TugasBesar> findByKelasIdAndNama(@Param("kelasId") Integer kelasId, @Param("namaTugas") String namaTugas);
    
    // Cari tugas terbaru berdasarkan kelasId
    @Query("SELECT t FROM TugasBesar t WHERE t.idKelas = :kelasId ORDER BY t.tanggalDibuat DESC, t.idTubes DESC")
    Optional<TugasBesar> findLatestByKelasId(@Param("kelasId") Integer kelasId);
    
    // Cari tugas dengan relasi Kelas yang sudah di-load
    @Query("SELECT t FROM TugasBesar t LEFT JOIN FETCH t.kelas WHERE t.idKelas = :kelasId")
    List<TugasBesar> findByKelasIdWithKelas(@Param("kelasId") Integer kelasId);
}