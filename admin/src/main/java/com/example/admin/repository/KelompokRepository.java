package com.example.admin.repository;

import com.example.admin.entity.Kelompok;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface KelompokRepository extends JpaRepository<Kelompok, Integer> {
    List<Kelompok> findByIdTubes(Integer idTubes);
}