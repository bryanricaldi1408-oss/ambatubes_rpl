package com.example.admin.repository;

import com.example.admin.entity.MahasiswaCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MahasiswaCredentialsRepository extends JpaRepository<MahasiswaCredentials, String> {
    MahasiswaCredentials findByEmail(String email);
}