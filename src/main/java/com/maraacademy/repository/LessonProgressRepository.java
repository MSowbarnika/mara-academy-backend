package com.maraacademy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maraacademy.entity.LessonProgress;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    List<LessonProgress> findByUserId(Long userId);

    boolean existsByUserIdAndCourseIdAndLessonNumber(Long userId, Long courseId, int lessonNumber);
}
