package com.example.admin.repository;

import com.example.admin.entity.DosenCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DosenCredentialsRepository extends JpaRepository<DosenCredentials, String> {
    DosenCredentials findByEmail(String email);
}