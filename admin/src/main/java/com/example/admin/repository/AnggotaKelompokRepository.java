package com.example.admin.repository;

import com.example.admin.entity.AnggotaKelompok;
import com.example.admin.entity.Mahasiswa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnggotaKelompokRepository extends JpaRepository<AnggotaKelompok, Integer> {

    // 1. Hitung jumlah (Tetap sama)
    @Query("SELECT COUNT(a) FROM AnggotaKelompok a WHERE a.idKelompok = :idKelompok")
    int countAnggotaByKelompok(@Param("idKelompok") Integer idKelompok);

    // 2. PERBAIKAN: Gunakan JOIN agar data mahasiswa pasti terambil
    @Query("SELECT m FROM AnggotaKelompok ak JOIN ak.mahasiswa m WHERE ak.idKelompok = :idKelompok")
    List<Mahasiswa> findMahasiswaByKelompok(@Param("idKelompok") Integer idKelompok);

    // 3. Cek posisi user sekarang
    @Query("SELECT ak FROM AnggotaKelompok ak JOIN ak.kelompok k WHERE ak.npm = :npm AND k.idTubes = :idTubes")
    Optional<AnggotaKelompok> findByNpmAndIdTubes(@Param("npm") String npm, @Param("idTubes") Integer idTubes);

    // 4. Hapus data lama (Wajib ada @Transactional & @Modifying)
    @Modifying
    @Transactional
    @Query("DELETE FROM AnggotaKelompok ak WHERE ak.npm = :npm AND ak.idKelompok IN (SELECT k.idKelompok FROM Kelompok k WHERE k.idTubes = :idTubes)")
    void deleteByNpmAndIdTubes(@Param("npm") String npm, @Param("idTubes") Integer idTubes);

    // 5. Hapus semua anggota berdasarkan idTubes (untuk reset kelompok)
    @Modifying
    @Transactional
    @Query("DELETE FROM AnggotaKelompok ak WHERE ak.idKelompok IN (SELECT k.idKelompok FROM Kelompok k WHERE k.idTubes = :idTubes)")
    void deleteByIdTubes(@Param("idTubes") Integer idTubes);

    // 6. Hapus anggota spesifik dari kelompok
    @Modifying
    @Transactional
    @Query("DELETE FROM AnggotaKelompok ak WHERE ak.npm = :npm AND ak.idKelompok = :idKelompok")
    void deleteByNpmAndIdKelompok(@Param("npm") String npm, @Param("idKelompok") Integer idKelompok);

    // 7. Hapus semua anggota dalam satu kelompok (Cascade Delete Manual)
    @Modifying
    @Transactional
    @Query("DELETE FROM AnggotaKelompok ak WHERE ak.idKelompok = :idKelompok")
    void deleteByIdKelompok(@Param("idKelompok") Integer idKelompok);
}