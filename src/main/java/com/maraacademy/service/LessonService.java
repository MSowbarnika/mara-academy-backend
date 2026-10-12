package com.maraacademy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maraacademy.entity.Lesson;
import com.maraacademy.repository.LessonRepository;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public List<Lesson> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByLessonNumberAsc(courseId);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id).orElse(null);
    }

    public Lesson addLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(Long id, Lesson lesson) {

        Lesson existingLesson =
                lessonRepository.findById(id).orElse(null);

        if (existingLesson != null) {

            existingLesson.setCourseId(
                    lesson.getCourseId()
            );

            existingLesson.setLessonTitle(
                    lesson.getLessonTitle()
            );

            existingLesson.setVideoUrl(
                    lesson.getVideoUrl()
            );

            existingLesson.setLessonNumber(
                    lesson.getLessonNumber()
            );

            return lessonRepository.save(existingLesson);
        }

        return null;
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }
}