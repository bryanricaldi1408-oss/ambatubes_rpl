package com.example.admin.repository;

import com.example.admin.entity.Kelas;
import com.example.admin.entity.PengajaranKelas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PengajaranKelasRepository extends JpaRepository<PengajaranKelas, Integer> {
    
    @Query("SELECT pk FROM PengajaranKelas pk JOIN FETCH pk.dosen WHERE pk.idKelas = :idKelas")
    List<PengajaranKelas> findByKelasWithDosen(@Param("idKelas") Integer idKelas);
    
    List<PengajaranKelas> findByIdKelas(Integer idKelas);
    
    @Query("SELECT pk FROM PengajaranKelas pk WHERE pk.nik = :nik AND pk.idKelas = :idKelas")
    PengajaranKelas findByNikAndIdKelas(@Param("nik") String nik, @Param("idKelas") Integer idKelas);
    
    @Query("SELECT COUNT(pk) > 0 FROM PengajaranKelas pk JOIN pk.kelas k JOIN k.mataKuliah mk " +
           "WHERE pk.nik = :nik AND mk.kodeMk = :kodeMk AND k.namaKelas = :namaKelas AND k.semester = :semester")
    boolean existsByCompositeKey(@Param("nik") String nik, 
                                @Param("kodeMk") String kodeMk, 
                                @Param("namaKelas") String namaKelas, 
                                @Param("semester") String semester);

     @Query("SELECT pk.kelas FROM PengajaranKelas pk WHERE pk.dosen.nik = :nik")
    List<Kelas> findKelasByDosenNik(@Param("nik") String nik);

    @Query("SELECT pk.kelas FROM PengajaranKelas pk " +
           "JOIN pk.kelas.mataKuliah mk " +
           "WHERE pk.dosen.nik = :nik " +
           "AND (LOWER(pk.kelas.namaKelas) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(pk.kelas.semester) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(mk.namaMk) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Kelas> searchKelasByDosenAndKeyword(@Param("nik") String nik, 
                                             @Param("keyword") String keyword);
}