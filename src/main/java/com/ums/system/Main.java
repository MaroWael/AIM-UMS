package com.ums.system;

import com.ums.system.dao.*;
import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.utils.DatabaseConnection;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Connect to DB
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Initialize DAOs and Services
            StudentDAOImpl studentDAO = new StudentDAOImpl(conn);
            InstructorDAOImpl instructorDAO = new InstructorDAOImpl(conn);
            CourseServiceImpl courseService = new CourseServiceImpl(conn);

            QuestionDAOImpl questionDAO = new QuestionDAOImpl(conn);
            QuizDAOImpl quizDAO = new QuizDAOImpl(conn, questionDAO);

            StudentService studentService = new StudentServiceImpl(conn);
            InstructorService instructorService = new InstructorServiceImpl(conn);
            QuizService quizService = new QuizServiceImpl(conn);

            // Insert Admin (skip if already exists)
            try (var ps = conn.prepareStatement(
                    "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)"
            )) {
                ps.setString(1, "Admin User");
                ps.setString(2, "admin@ums.edu");
                ps.setString(3, "admin123");
                ps.setString(4, Role.ADMIN.toString());
                ps.executeUpdate();
                System.out.println("Admin added");
            } catch (Exception ignored) {
                System.out.println("Admin already exists.");
            }

            // Insert Instructors
            Instructor inst1 = new Instructor("Dr. Omar Farouk", "omar@ums.edu", "omar123",
                    Role.INSTRUCTOR, Department.AI, null);
            Instructor inst2 = new Instructor("Dr. Salma Nabil", "salma@ums.edu", "salma123",
                    Role.INSTRUCTOR, Department.CS, null);
            instructorDAO.insert(inst1);
            instructorDAO.insert(inst2);
            System.out.println("Instructors added");

            // Insert Students
            Student st1 = new Student("Marwan Wael", "marwan@student.edu", "12345",
                    Role.STUDENT, 2, "Software Engineering", null, 0,
                    Department.AI, 3.9);
            Student st2 = new Student("Nour Ahmed", "nour@student.edu", "54321",
                    Role.STUDENT, 1, "Computer Science", null, 0,
                    Department.CS, 3.5);
            studentDAO.insert(st1);
            studentDAO.insert(st2);
            System.out.println("Students added");

            //  Insert Courses
            Course c1 = new Course("AI101", "Intro to AI", "1", "AI", "Mon 9:00 AM", null, null, 2);
            Course c2 = new Course("CS201", "Data Structures", "2", "CS", "Wed 11:00 AM", null, null, 3);
            Course c3 = new Course("CS301", "Operating Systems", "3", "CS", "Thu 1:00 PM", null, null, 3);
            courseService.addCourse(c1);
            courseService.addCourse(c2);
            courseService.addCourse(c3);
            System.out.println("Courses added");

            // -------------------------------
            // Service-layer Tests
            // -------------------------------
            System.out.println("\n--- Service Layer Tests ---");

            List<Course> allCourses = courseService.getAllCourses();
            System.out.println("Courses in DB: " + allCourses);

            Course found = courseService.getCourseByCode("AI101");
            System.out.println("Course AI101: " + found);

            List<Student> students = studentService.getAllStudents();
            System.out.println("Students: " + students);

            List<Instructor> instructors = instructorService.getAllInstructors();
            System.out.println("Instructors: " + instructors);

            // Create a Quiz with Questions
            Question q1 = new Question(0, "What does AI stand for?",
                    Arrays.asList("Artificial Intelligence", "Applied Info", "Auto Industry", "None"), 0);
            Question q2 = new Question(0, "Which field uses neural networks?",
                    Arrays.asList("AI", "Biology", "History", "Law"), 0);

            // FIX: include courseCode “AI101”
            Quiz newQuiz = new Quiz(0, "Intro to AI Quiz", "AI101", Arrays.asList(q1, q2));
            quizService.createQuiz(newQuiz);
            System.out.println("aQuiz created via service");

            // List quizzes
            List<Quiz> quizzes = quizService.getAllQuizzes();
            System.out.println("All quizzes: " + quizzes);

            List<Quiz> aiQuizzes = quizService.getQuizzesByCourseCode("AI101");
            System.out.println("AI101 quizzes: " + aiQuizzes);

            // Print questions for each quiz
            for (Quiz q : quizzes) {
                System.out.println("Quiz: " + q.getTitle());
                if (q.getQuestions() != null) {
                    for (Question qq : q.getQuestions()) {
                        System.out.println("  Q: " + qq.getText() + " | options=" + qq.getOptions());
                    }
                }
            }

            System.out.println("\nService-layer testing completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
