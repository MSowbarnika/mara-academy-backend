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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maraacademy.config.AuthHelper;
import com.maraacademy.entity.DemoVideo;
import com.maraacademy.repository.DemoVideoRepository;

@RestController
@RequestMapping("/api/demos")
public class DemoVideoController {

    private final DemoVideoRepository demoRepository;
    private final AuthHelper auth;

    public DemoVideoController(DemoVideoRepository demoRepository, AuthHelper auth) {
        this.demoRepository = demoRepository;
        this.auth = auth;
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    // Returns an error text, or null when the demo video is valid
    private String validate(DemoVideo demo) {
        if (blank(demo.getCategory())
                || !(demo.getCategory().equalsIgnoreCase("KEYBOARD")
                        || demo.getCategory().equalsIgnoreCase("FLUTE"))) {
            return "Category must be KEYBOARD or FLUTE";
        }
        if (blank(demo.getTitle())) {
            return "Title is required";
        }
        if (blank(demo.getVideoUrl())) {
            return "Video link is required";
        }
        return null;
    }

    // Public: all demos, or only one category (?category=KEYBOARD)
    @GetMapping
    public List<DemoVideo> getDemos(
            @RequestParam(name = "category", required = false) String category) {

        if (blank(category)) {
            return demoRepository.findAllByOrderByCategoryAscSortOrderAscIdAsc();
        }
        return demoRepository.findByCategoryIgnoreCaseOrderBySortOrderAscIdAsc(category.trim());
    }

    // Admin only
    @PostMapping
    public ResponseEntity<?> addDemo(
            @RequestBody DemoVideo demo,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        String problem = validate(demo);
        if (problem != null) {
            return ResponseEntity.badRequest().body(problem);
        }

        demo.setId(null);
        demo.setCategory(demo.getCategory().trim().toUpperCase());
        demo.setTitle(demo.getTitle().trim());
        demo.setVideoUrl(demo.getVideoUrl().trim());

        return ResponseEntity.ok(demoRepository.save(demo));
    }

    // Admin only
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDemo(
            @PathVariable Long id,
            @RequestBody DemoVideo demo,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        DemoVideo existing = demoRepository.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Demo video not found");
        }

        String problem = validate(demo);
        if (problem != null) {
            return ResponseEntity.badRequest().body(problem);
        }

        existing.setCategory(demo.getCategory().trim().toUpperCase());
        existing.setTitle(demo.getTitle().trim());
        existing.setVideoUrl(demo.getVideoUrl().trim());
        existing.setSortOrder(demo.getSortOrder());

        return ResponseEntity.ok(demoRepository.save(existing));
    }

    // Admin only
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDemo(
            @PathVariable Long id,
            @RequestHeader(value = "X-Admin-Token", required = false) String adminToken) {

        if (!auth.isAdmin(adminToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }

        demoRepository.deleteById(id);
        return ResponseEntity.ok("Demo video deleted");
    }
}
