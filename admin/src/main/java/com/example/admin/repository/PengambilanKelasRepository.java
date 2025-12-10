package com.example.admin.repository;

import com.example.admin.entity.PengambilanKelas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PengambilanKelasRepository extends JpaRepository<PengambilanKelas, Integer> {
    
    @Query("SELECT pk FROM PengambilanKelas pk JOIN FETCH pk.mahasiswa WHERE pk.idKelas = :idKelas")
    List<PengambilanKelas> findByKelasWithMahasiswa(@Param("idKelas") Integer idKelas);
    
    List<PengambilanKelas> findByIdKelas(Integer idKelas);
    
    @Query("SELECT pk FROM PengambilanKelas pk WHERE pk.npm = :npm AND pk.idKelas = :idKelas")
    PengambilanKelas findByNpmAndIdKelas(@Param("npm") String npm, @Param("idKelas") Integer idKelas);
    
    @Query("SELECT COUNT(pk) > 0 FROM PengambilanKelas pk JOIN pk.kelas k JOIN k.mataKuliah mk " +
           "WHERE pk.npm = :npm AND mk.kodeMk = :kodeMk AND k.namaKelas = :namaKelas AND k.semester = :semester")
    boolean existsByCompositeKey(@Param("npm") String npm, 
                                @Param("kodeMk") String kodeMk, 
                                @Param("namaKelas") String namaKelas, 
                                @Param("semester") String semester);
}