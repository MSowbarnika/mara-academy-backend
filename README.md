# 🎵 Mara Academy - Online Music Classes (Backend)

Spring Boot REST API for an online music learning platform (Keyboard and Flute classes).
Students register, choose a plan, pay the fee (demo payment), and watch lessons. The progress is saved in the database, so a student continues from the same lesson on any phone or laptop.

> This is a learning / portfolio project. Course names, fees and videos are sample data.

**Frontend repo:** https://github.com/MSowbarnika/mara-academy-frontend

## Features
- Student register and login. Passwords are stored as BCrypt hashes
- Token based access: students send `Authorization: Bearer <token>`, admins send `X-Admin-Token`
- Role based access: only the admin can add, edit or delete courses, lessons and demo videos
- Courses with 4 month and 6 month plans, each with an India fee and an Other Country fee
- Enrollment with demo payment (UPI / Card / Net Banking). The fee is calculated on the server, so the browser cannot change it
- Plan validity: every enrollment has a "valid until" date, and an expired plan can be renewed
- Lesson progress saved per student in the database, with "continue from where you stopped"
- Free Keyboard and Flute demo videos managed by the admin
- Admin can add all lesson video links of a course at once
- A student can read and change only his own profile, password, enrollments and progress
- Sample courses and lessons are added automatically on the first run

## Tech Stack
Java 17, Spring Boot, Spring Data JPA, MySQL, BCrypt (spring-security-crypto), Maven, Docker

## API Endpoints
| Method | Endpoint | Access | Description |
|--------|----------|--------|-------------|
| POST | `/api/users/register` | Public | Create student account |
| POST | `/api/users/login` | Public | Login, returns token |
| GET | `/api/users` | Admin | List students |
| PUT | `/api/users/{id}` | Student (self) | Update profile |
| PUT | `/api/users/{id}/password` | Student (self) | Change password |
| POST | `/api/admin/login` | Public | Admin login, returns token |
| GET | `/api/courses`, `/api/courses/{id}` | Public | View courses |
| POST / PUT / DELETE | `/api/courses`, `/api/courses/{id}` | Admin | Manage courses |
| GET | `/api/lessons/course/{courseId}`, `/api/lessons/{id}` | Public | View lessons |
| POST / PUT / DELETE | `/api/lessons`, `/api/lessons/bulk`, `/api/lessons/{id}` | Admin | Manage lessons |
| GET | `/api/demos?category=KEYBOARD` | Public | Free demo videos (KEYBOARD or FLUTE) |
| POST / PUT / DELETE | `/api/demos`, `/api/demos/{id}` | Admin | Manage demo videos |
| POST | `/api/enrollments` | Student | Buy a plan (4 or 6 months, India or Other) |
| GET | `/api/enrollments/user/{userId}` | Student (self) / Admin | My enrollments |
| GET | `/api/enrollments` | Admin | All enrollments |
| POST | `/api/progress` | Student | Save a finished lesson |
| GET | `/api/progress/user/{userId}` | Student (self) / Admin | Finished lessons |

## Run Locally
1. Install MySQL and keep it running. The database `mara_academy` and its tables are created automatically on the first run.
2. Create `src/main/resources/application-local.properties` (it is git-ignored):
```
spring.datasource.password=YOUR_MYSQL_PASSWORD
app.admin.email=admin@example.com
app.admin.password=choose-a-strong-password
```
3. Run `MaraAcademyApplication` from your IDE, or use `./mvnw spring-boot:run`.
4. The API runs at `http://localhost:8080`.

## Environment Variables (for deployment)
| Variable | Purpose |
|----------|---------|
| `DB_URL` | JDBC URL of the MySQL database |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `CORS_ORIGINS` | Allowed frontend URL(s), comma separated |
| `ADMIN_EMAIL` | First admin email (created only if no admin exists) |
| `ADMIN_PASSWORD` | First admin password |

## Author
**Sowbarnika M** - Java Full Stack Developer (fresher)
