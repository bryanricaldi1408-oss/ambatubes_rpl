package com.example.admin.repository;

import com.example.admin.entity.NilaiKelompok;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NilaiKelompokRepository extends JpaRepository<NilaiKelompok, Integer> {
    NilaiKelompok findByIdKelompokAndIdKegiatan(Integer idKelompok, Integer idKegiatan);
}
