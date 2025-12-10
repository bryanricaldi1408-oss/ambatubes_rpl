package com.example.admin.repository;

import com.example.admin.entity.Kelas;
import com.example.admin.dto.ClassDisplayDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface KelasRepository extends JpaRepository<Kelas, Integer> {
    
       @Query("SELECT new com.example.admin.dto.ClassDisplayDto(" +
           "k.idKelas, k.mataKuliah.namaMk, k.namaKelas, k.semester, k.dosen.nama) " +
           "FROM Kelas k")
       List<ClassDisplayDto> findAllClassDisplay();

        @Query("SELECT k FROM Kelas k WHERE k.mataKuliah.kodeMk = :kodeMk AND k.namaKelas = :namaKelas AND k.semester = :semester")
        Optional<Kelas> findByCompositeKey(@Param("kodeMk") String kodeMk, 
                                        @Param("namaKelas") String namaKelas, 
                                        @Param("semester") String semester);
        
        @Query("SELECT k.idKelas FROM Kelas k WHERE k.mataKuliah.kodeMk = :kodeMk AND k.namaKelas = :namaKelas AND k.semester = :semester")
        Optional<Integer> findIdKelasByCompositeKey(@Param("kodeMk") String kodeMk, 
                                               @Param("namaKelas") String namaKelas, 
                                               @Param("semester") String semester);
}