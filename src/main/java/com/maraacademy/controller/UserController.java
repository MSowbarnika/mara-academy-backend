package com.maraacademy.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maraacademy.config.AuthHelper;
import com.maraacademy.entity.User;
import com.maraacademy.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AuthHelper auth;

    public UserController(UserRepository userRepository, AuthHelper auth) {
        this.userRepository = userRepository;
        this.auth = auth;
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    // =========================
    // GET ALL STUDENTS (admin only, no passwords, no tokens)
    // =========================
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        List<Map<String, Object>> students = userRepository.findAll()
                .stream()
                .map(user -> auth.userView(user, false))
                .toList();

        return ResponseEntity.ok(students);
    }

    // =========================
    // REGISTER
    // =========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User request) {

        if (blank(request.getName()) || blank(request.getEmail()) || blank(request.getPassword())) {
            return ResponseEntity.badRequest().body("Name, email and password are required");
        }

        if (request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        String email = request.getEmail().trim();

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
        }

        User user = new User(
                request.getName().trim(),
                email,
                auth.hash(request.getPassword()),
                request.getPhone());
        user.setToken(auth.newToken());

        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(auth.userView(saved, true));
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {

        User user = null;

        if (!blank(loginRequest.getEmail())) {
            for (User candidate : userRepository.findAllByEmail(loginRequest.getEmail().trim())) {
                if (auth.matches(loginRequest.getPassword(), candidate.getPassword())) {
                    user = candidate;
                    break;
                }
            }
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        // Old plain-text password -> convert to a BCrypt hash on first login
        if (!auth.isHashed(user.getPassword())) {
            user.setPassword(auth.hash(loginRequest.getPassword()));
        }

        if (user.getToken() == null) {
            user.setToken(auth.newToken());
        }
        userRepository.save(user);

        return ResponseEntity.ok(auth.userView(user, true));
    }

    // =========================
    // UPDATE PROFILE (only the logged-in student himself)
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestBody User updated,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User me = auth.currentUser(authHeader);

        if (me == null || !me.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please login again");
        }

        if (!blank(updated.getName())) {
            me.setName(updated.getName().trim());
        }

        if (!blank(updated.getEmail())) {
            String newEmail = updated.getEmail().trim();
            if (!newEmail.equals(me.getEmail()) && userRepository.existsByEmail(newEmail)) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
            }
            me.setEmail(newEmail);
        }

        if (!blank(updated.getPhone())) {
            me.setPhone(updated.getPhone().trim());
        }

        userRepository.save(me);

        return ResponseEntity.ok(auth.userView(me, true));
    }

    // =========================
    // CHANGE PASSWORD (only the logged-in student himself)
    // =========================
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(
            @PathVariable Long id,
            @RequestBody User passwordRequest,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User me = auth.currentUser(authHeader);

        if (me == null || !me.getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please login again");
        }

        if (!auth.matches(passwordRequest.getPassword(), me.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Current password is incorrect");
        }

        String newPassword = passwordRequest.getNewPassword();

        if (blank(newPassword)) {
            return ResponseEntity.badRequest().body("New password is required");
        }

        if (newPassword.length() < 6) {
            return ResponseEntity.badRequest().body("Password must be at least 6 characters");
        }

        me.setPassword(auth.hash(newPassword));
        userRepository.save(me);

        return ResponseEntity.ok(auth.userView(me, true));
    }
}
