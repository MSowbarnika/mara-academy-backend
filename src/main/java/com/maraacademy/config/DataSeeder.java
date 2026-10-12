package com.maraacademy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.maraacademy.entity.Admin;
import com.maraacademy.entity.Course;
import com.maraacademy.entity.Lesson;
import com.maraacademy.repository.AdminRepository;
import com.maraacademy.repository.CourseRepository;
import com.maraacademy.repository.LessonRepository;

/**
 * Runs on startup:
 *  - creates the first admin (only if ADMIN_EMAIL and ADMIN_PASSWORD are set and no admin exists)
 *  - adds sample courses and lessons when the course table is empty
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    private final AdminRepository adminRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final AuthHelper auth;

    public DataSeeder(AdminRepository adminRepository,
                      CourseRepository courseRepository,
                      LessonRepository lessonRepository,
                      AuthHelper auth) {
        this.adminRepository = adminRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.auth = auth;
    }

    @Override
    public void run(String... args) {

        if (adminRepository.count() == 0
                && !adminEmail.isBlank()
                && !adminPassword.isBlank()) {

            Admin admin = new Admin(adminEmail.trim(), auth.hash(adminPassword));
            admin.setToken(auth.newToken());
            adminRepository.save(admin);
        }

        if (courseRepository.count() == 0) {
            addCourse("Keyboard Class - Level 1",
                    "Learn keyboard basics: notes, hand position, scales and your first songs.",
                    "Beginner", "2 Months", 600, 800,
                    "This package has 20 video lessons, from the very basics to simple songs.\n"
                + "Lessons use western notes and are made for complete beginners.\n"
                + "Watch each video and practise along with it on your keyboard.\n"
                + "Every lesson video is about 20 to 40 minutes long.\n"
                + "You can watch the lessons any time until your plan validity ends.\n",
                    "Keyboard introduction\n"
                + "Posture and hand position\n"
                + "Names of the keys and Middle C\n"
                + "The seven notes\n"
                + "Finger numbers\n"
                + "Note values and rhythm\n"
                + "C major scale\n"
                + "Sharps and flats\n"
                + "Reading simple notes\n"
                + "Basic chords\n"
                + "Playing a song with chords\n"
                + "Revision and practice test\n");

            addCourse("Keyboard Class - Level 2",
                    "Chords, rhythm patterns and playing complete songs with confidence.",
                    "Intermediate", "2 Months", 900, 1200,
                    "This package has 20 video lessons for students who finished Level 1.\n"
                + "Lessons use western notes at an intermediate level.\n"
                + "Watch each video and practise along with it on your keyboard.\n"
                + "Every lesson video is about 20 to 40 minutes long.\n"
                + "You can watch the lessons any time until your plan validity ends.\n",
                    "Revision of Level 1\n"
                + "Major scales\n"
                + "Minor scales\n"
                + "Triads and inversions\n"
                + "Major and minor chords\n"
                + "Chord progressions\n"
                + "Left hand accompaniment patterns\n"
                + "Rhythm and time signatures\n"
                + "Key signatures\n"
                + "Playing by ear\n"
                + "Playing songs with both hands\n"
                + "Final revision and test\n");

            addCourse("Flute Class",
                    "Breathing, finger positions and playing simple melodies on the flute.",
                    "Beginner", "2 Months", 800, 1000,
                    "This package has 20 video lessons for beginners.\n"
                + "Lessons start with breathing and sound, then move to notes and songs.\n"
                + "Watch each video and practise along with it on your flute.\n"
                + "Every lesson video is about 10 to 20 minutes long.\n"
                + "You can watch the lessons any time until your plan validity ends.\n",
                    "Flute introduction\n"
                + "Parts of the flute and holding it\n"
                + "Breathing basics\n"
                + "Making the first sound\n"
                + "Long tones\n"
                + "Finger positions\n"
                + "First notes\n"
                + "Simple note patterns\n"
                + "Tonguing technique\n"
                + "Rhythm and counting\n"
                + "Scales\n"
                + "Easy melodies and a complete song\n");
        }
    }

    private void addCourse(String name, String description,
                           String level, String duration, int fee, int sixMonthFee,
                           String procedures, String syllabus) {

        String[] titles = {
                "Introduction",
                "Basics and posture",
                "First exercise",
                "Practice session",
                "Play your first song"
        };

        Course course = new Course(name, description, level, titles.length, duration);
        course.setFee(fee);
        course.setSixMonthFee(sixMonthFee);
        course.setOtherFee(fee + 500);
        course.setOtherSixMonthFee(sixMonthFee + 600);
        course.setProcedures(procedures);
        course.setSyllabus(syllabus);
        course = courseRepository.save(course);

        for (int i = 0; i < titles.length; i++) {
            // videoUrl is empty: admin adds a YouTube link from the Admin > Lessons page
            lessonRepository.save(new Lesson(course.getCourseId(), titles[i], "", i + 1));
        }
    }
}
