package com.example.admin.repository;

import com.example.admin.entity.Kelas;
import com.example.admin.dto.ClassDisplayDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KelasRepository extends JpaRepository<Kelas, Integer> {
    
       @Query("SELECT new com.example.admin.dto.ClassDisplayDto(" +
           "k.idKelas, k.mataKuliah.namaMk, k.namaKelas, k.semester, k.dosen.nama) " +
           "FROM Kelas k")
       List<ClassDisplayDto> findAllClassDisplay();
}