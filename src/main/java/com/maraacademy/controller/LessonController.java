package com.maraacademy.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maraacademy.config.AuthHelper;
import com.maraacademy.entity.Lesson;
import com.maraacademy.service.LessonService;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {

    private final LessonService lessonService;
    private final AuthHelper auth;

    public LessonController(LessonService lessonService, AuthHelper auth) {
        this.lessonService = lessonService;
        this.auth = auth;
    }

    // Public
    @GetMapping("/course/{courseId}")
    public List<Lesson> getLessonsByCourse(@PathVariable Long courseId) {
        return lessonService.getLessonsByCourse(courseId);
    }

    // Public
    @GetMapping("/{id}")
    public Lesson getLessonById(@PathVariable Long id) {
        return lessonService.getLessonById(id);
    }

    // Admin only
    @PostMapping
    public ResponseEntity<?> addLesson(
            @RequestBody Lesson lesson,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(lessonService.addLesson(lesson));
    }

    // Admin only
    @PostMapping("/bulk")
    public ResponseEntity<?> addLessons(
            @RequestBody List<Lesson> lessons,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(
                lessons.stream().map(lessonService::addLesson).toList());
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(
            @PathVariable Long id,
            @RequestBody Lesson lesson,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(lessonService.updateLesson(id, lesson));
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLesson(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        lessonService.deleteLesson(id);
        return ResponseEntity.ok("Lesson deleted successfully");
    }
}
