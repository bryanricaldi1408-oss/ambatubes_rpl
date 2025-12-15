package com.example.admin.repository;

import com.example.admin.entity.Jadwal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JadwalRepository extends JpaRepository<Jadwal, Integer> {
    List<Jadwal> findByIdTubes(Integer idTubes);
    void deleteByIdTubes(Integer idTubes);
}