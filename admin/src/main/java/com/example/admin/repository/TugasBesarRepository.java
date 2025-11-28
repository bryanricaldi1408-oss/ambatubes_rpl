package com.example.admin.repository;

import com.example.admin.entity.TugasBesar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TugasBesarRepository extends JpaRepository<TugasBesar, Integer> {
    // JpaRepository otomatis menyediakan method findById(Integer id)
}