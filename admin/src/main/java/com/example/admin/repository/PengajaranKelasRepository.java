package com.example.admin.repository;

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
}