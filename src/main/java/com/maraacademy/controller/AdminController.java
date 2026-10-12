package com.maraacademy.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maraacademy.config.AuthHelper;
import com.maraacademy.entity.Admin;
import com.maraacademy.repository.AdminRepository;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminRepository adminRepository;
    private final AuthHelper auth;

    public AdminController(AdminRepository adminRepository, AuthHelper auth) {
        this.adminRepository = adminRepository;
        this.auth = auth;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin loginRequest) {

        Admin admin = loginRequest.getEmail() == null
                ? null
                : adminRepository.findByEmail(loginRequest.getEmail().trim()).orElse(null);

        if (admin == null || !auth.matches(loginRequest.getPassword(), admin.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid admin email or password");
        }

        // Old plain-text password -> convert to a BCrypt hash on first login
        if (!auth.isHashed(admin.getPassword())) {
            admin.setPassword(auth.hash(loginRequest.getPassword()));
        }

        if (admin.getToken() == null) {
            admin.setToken(auth.newToken());
        }
        adminRepository.save(admin);

        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", admin.getId());
        view.put("email", admin.getEmail());
        view.put("token", admin.getToken());
        return ResponseEntity.ok(view);
    }
}
