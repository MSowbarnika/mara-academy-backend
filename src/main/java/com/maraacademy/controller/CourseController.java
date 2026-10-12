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
import com.maraacademy.entity.Course;
import com.maraacademy.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final AuthHelper auth;

    public CourseController(CourseService courseService, AuthHelper auth) {
        this.courseService = courseService;
        this.auth = auth;
    }

    // Public
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // Public
    @GetMapping("/{id}")
    public Course getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // Admin only
    @PostMapping
    public ResponseEntity<?> addCourse(
            @RequestBody Course course,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(courseService.addCourse(course));
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCourse(
            @PathVariable Long id,
            @RequestBody Course course,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCourse(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        courseService.deleteCourse(id);
        return ResponseEntity.ok("Course deleted successfully");
    }
}
