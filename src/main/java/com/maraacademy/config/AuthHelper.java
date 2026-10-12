package com.maraacademy.config;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.maraacademy.entity.User;
import com.maraacademy.repository.AdminRepository;
import com.maraacademy.repository.UserRepository;

/**
 * Small helper for passwords and token checks.
 * Students send:  Authorization: Bearer <token>
 * Admins send:    X-Admin-Token: <token>
 */
@Component
public class AuthHelper {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder encoder;

    public AuthHelper(UserRepository userRepository,
                      AdminRepository adminRepository,
                      BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.encoder = encoder;
    }

    // Logged-in student from the Authorization header (or null)
    public User currentUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return null;
        }
        return userRepository.findByToken(token).orElse(null);
    }

    // true only if the X-Admin-Token belongs to an admin
    public boolean isAdmin(String adminToken) {
        if (adminToken == null || adminToken.isBlank()) {
            return false;
        }
        return adminRepository.findByToken(adminToken.trim()).isPresent();
    }

    public String hash(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public boolean isHashed(String stored) {
        return stored != null && stored.startsWith("$2");
    }

    // Works for BCrypt hashes and also for old plain-text passwords
    public boolean matches(String rawPassword, String stored) {
        if (rawPassword == null || stored == null) {
            return false;
        }
        if (isHashed(stored)) {
            return encoder.matches(rawPassword, stored);
        }
        return stored.equals(rawPassword);
    }

    public String newToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    // What we send to the browser. Never includes the password.
    public Map<String, Object> userView(User user, boolean includeToken) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", user.getId());
        view.put("name", user.getName());
        view.put("email", user.getEmail());
        view.put("phone", user.getPhone());
        if (includeToken) {
            view.put("token", user.getToken());
        }
        return view;
    }
}
