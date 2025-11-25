package com.example.admin.service;

import com.example.admin.entity.Admin;
import com.example.admin.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public boolean validateAdmin(String email, String password) {
        Optional<Admin> admin = adminRepository.findByEmailAndPassword(email, password);
        return admin.isPresent();
    }

    public Optional<Admin> getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }
}