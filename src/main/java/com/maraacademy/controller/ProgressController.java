package com.maraacademy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maraacademy.config.AuthHelper;
import com.maraacademy.entity.LessonProgress;
import com.maraacademy.entity.User;
import com.maraacademy.repository.LessonProgressRepository;

// Lesson progress is saved in the database, so a student continues from the same
// place on any phone or laptop after login.
@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final LessonProgressRepository progressRepository;
    private final AuthHelper auth;

    public ProgressController(LessonProgressRepository progressRepository, AuthHelper auth) {
        this.progressRepository = progressRepository;
        this.auth = auth;
    }

    // The logged-in student finished a lesson
    @PostMapping
    public ResponseEntity<?> saveProgress(
            @RequestBody LessonProgress request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User me = auth.currentUser(authHeader);

        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please login again");
        }

        if (request.getCourseId() == null || request.getLessonNumber() < 1) {
            return ResponseEntity.badRequest().body("Course and lesson number are required");
        }

        // The student id always comes from the login token, not from the browser
        if (!progressRepository.existsByUserIdAndCourseIdAndLessonNumber(
                me.getId(), request.getCourseId(), request.getLessonNumber())) {

            progressRepository.save(
                    new LessonProgress(me.getId(), request.getCourseId(), request.getLessonNumber()));
        }

        return ResponseEntity.ok("Saved");
    }

    // The student himself (or an admin) reads the finished lessons
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserProgress(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        User me = auth.currentUser(authHeader);
        boolean allowed = auth.isAdmin(adminToken)
                || (me != null && me.getId().equals(userId));

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please login again");
        }

        return ResponseEntity.ok(progressRepository.findByUserId(userId));
    }
}
