package com.maraacademy.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maraacademy.entity.Course;
import com.maraacademy.repository.CourseRepository;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long id, Course course) {

        Course existingCourse = courseRepository.findById(id).orElse(null);

        if (existingCourse != null) {
            existingCourse.setCourseName(course.getCourseName());
            existingCourse.setDescription(course.getDescription());
            existingCourse.setLevel(course.getLevel());
            existingCourse.setTotalLessons(course.getTotalLessons());
            existingCourse.setDuration(course.getDuration());
            existingCourse.setFee(course.getFee());
            existingCourse.setImageUrl(course.getImageUrl());
            existingCourse.setSixMonthFee(course.getSixMonthFee());
            existingCourse.setOtherFee(course.getOtherFee());
            existingCourse.setOtherSixMonthFee(course.getOtherSixMonthFee());
            existingCourse.setProcedures(course.getProcedures());
            existingCourse.setSyllabus(course.getSyllabus());

            return courseRepository.save(existingCourse);
        }

        return null;
    }

    public void deleteCourse(Long id) {
        courseRepository.deleteById(id);
    }
}