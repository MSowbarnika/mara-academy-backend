package com.maraacademy.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    private String courseName;

    private String description;

    private String level;

    private int totalLessons;

    private String duration;

    // Course fee in rupees (0 = free). Integer so old rows with NULL still load.
    private Integer fee;

    // Optional picture of the course (image link)
    private String imageUrl;

    // Fee for the 6 month plan (the normal `fee` is the 4 month plan). 0 = no 6 month plan.
    private Integer sixMonthFee;

    // Fees for students from other countries (4 and 6 month plans). 0 = not available.
    private Integer otherFee;

    private Integer otherSixMonthFee;

    // One point per line
    @Column(length = 3000)
    private String procedures;

    // One topic per line
    @Column(length = 3000)
    private String syllabus;

    public Course() {
    }

    public Course(String courseName, String description,
                  String level, int totalLessons,
                  String duration) {
        this.courseName = courseName;
        this.description = description;
        this.level = level;
        this.totalLessons = totalLessons;
        this.duration = duration;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getTotalLessons() {
        return totalLessons;
    }

    public void setTotalLessons(int totalLessons) {
        this.totalLessons = totalLessons;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getFee() {
        return fee == null ? 0 : fee;
    }

    public void setFee(Integer fee) {
        this.fee = fee;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getSixMonthFee() {
        return sixMonthFee == null ? 0 : sixMonthFee;
    }

    public void setSixMonthFee(Integer sixMonthFee) {
        this.sixMonthFee = sixMonthFee;
    }

    public String getProcedures() {
        return procedures;
    }

    public void setProcedures(String procedures) {
        this.procedures = procedures;
    }

    public String getSyllabus() {
        return syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.syllabus = syllabus;
    }

    public Integer getOtherFee() {
        return otherFee == null ? 0 : otherFee;
    }

    public void setOtherFee(Integer otherFee) {
        this.otherFee = otherFee;
    }

    public Integer getOtherSixMonthFee() {
        return otherSixMonthFee == null ? 0 : otherSixMonthFee;
    }

    public void setOtherSixMonthFee(Integer otherSixMonthFee) {
        this.otherSixMonthFee = otherSixMonthFee;
    }
}
