package com.maraacademy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maraacademy.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}