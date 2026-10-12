package com.maraacademy.controller;

import java.time.LocalDate;
import java.util.List;

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
import com.maraacademy.entity.Course;
import com.maraacademy.entity.Enrollment;
import com.maraacademy.entity.User;
import com.maraacademy.repository.CourseRepository;
import com.maraacademy.repository.EnrollmentRepository;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final AuthHelper auth;

    public EnrollmentController(EnrollmentRepository enrollmentRepository,
                                CourseRepository courseRepository,
                                AuthHelper auth) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.auth = auth;
    }

    // true when the plan end date is already over (old enrollments without a date never expire)
    private boolean isExpired(Enrollment enrollment) {
        String validUntil = enrollment.getValidUntil();
        if (validUntil == null || validUntil.isBlank()) {
            return false;
        }
        try {
            return LocalDate.parse(validUntil).isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    // Logged-in student buys a plan (4 or 6 months) - demo payment
    @PostMapping
    public ResponseEntity<?> enrollCourse(
            @RequestBody Enrollment request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        User me = auth.currentUser(authHeader);

        if (me == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Please login to enroll");
        }

        Course course = request.getCourseId() == null
                ? null
                : courseRepository.findById(request.getCourseId()).orElse(null);

        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found");
        }

        int months = request.getValidityMonths() == null ? 4 : request.getValidityMonths();

        if (months != 4 && months != 6) {
            return ResponseEntity.badRequest().body("Plan must be 4 or 6 months");
        }

        String region = request.getRegion() == null
                ? "INDIA"
                : request.getRegion().trim().toUpperCase();

        if (!region.equals("INDIA") && !region.equals("OTHER")) {
            return ResponseEntity.badRequest().body("Region must be INDIA or OTHER");
        }

        // The fee always comes from the server, not from the browser
        int fee;
        if (region.equals("OTHER")) {
            fee = months == 6 ? course.getOtherSixMonthFee() : course.getOtherFee();
        } else {
            fee = months == 6 ? course.getSixMonthFee() : course.getFee();
        }

        if ((months == 6 || region.equals("OTHER")) && fee <= 0) {
            return ResponseEntity.badRequest().body("This plan is not available for this course");
        }

        Enrollment existing = enrollmentRepository.findByUserId(me.getId())
                .stream()
                .filter(item -> course.getCourseId().equals(item.getCourseId()))
                .findFirst()
                .orElse(null);

        if (existing != null && !isExpired(existing)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Already enrolled in this course");
        }

        // A new enrollment, or a renewal of an expired one
        Enrollment enrollment = existing != null
                ? existing
                : new Enrollment(me.getId(), course.getCourseId(), course.getCourseName());

        enrollment.setAmount(fee);
        enrollment.setValidityMonths(months);
        enrollment.setRegion(region);
        enrollment.setValidUntil(LocalDate.now().plusMonths(months).toString());

        if (fee > 0) {
            String method = request.getPaymentMethod();
            if (method == null || method.isBlank()) {
                return ResponseEntity.badRequest().body("Please select a payment method");
            }
            enrollment.setPaymentMethod(method.trim());
            enrollment.setPaymentStatus("Paid (demo)");
        } else {
            enrollment.setPaymentMethod("FREE");
            enrollment.setPaymentStatus("Free");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentRepository.save(enrollment));
    }

    // Admin only
    @GetMapping
    public ResponseEntity<?> getAllEnrollments(
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        List<Enrollment> enrollments = enrollmentRepository.findAll();
        return ResponseEntity.ok(enrollments);
    }

    // The student himself (or an admin)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserCourses(
            @PathVariable Long userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        User me = auth.currentUser(authHeader);
        boolean allowed = auth.isAdmin(adminToken)
                || (me != null && me.getId().equals(userId));

        if (!allowed) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Please login again");
        }
        return ResponseEntity.ok(enrollmentRepository.findByUserId(userId));
    }
}
