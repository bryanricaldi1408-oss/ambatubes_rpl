package com.example.admin.repository;

import com.example.admin.entity.Dosen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DosenRepository extends JpaRepository<Dosen, String> {
}