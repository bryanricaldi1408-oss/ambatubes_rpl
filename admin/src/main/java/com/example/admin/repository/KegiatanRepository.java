package com.example.admin.repository;

import com.example.admin.entity.Kegiatan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface KegiatanRepository extends JpaRepository<Kegiatan, Integer> {
    List<Kegiatan> findByIdJadwal(Integer idJadwal);
    List<Kegiatan> deleteByIdJadwalIn(List<Integer> idJadwalList);
}