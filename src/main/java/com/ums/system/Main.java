package com.ums.system;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.utils.DatabaseConnection;
import com.ums.system.dao.*;

import java.sql.Connection;
import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Connection conn;
    private static AdminService adminService;
    private static InstructorService instructorService;
    private static CourseService courseService;
    private static StudentService studentService;
    private static QuizService quizService;
    private static EnrollmentDAO enrollmentDAO;
    private static QuizResultService quizResultService;

    public static void main(String[] args) {
        System.out.println("===================================");
        System.out.println("Welcome to University Management System");
        System.out.println("===================================\n");

        try {
            conn = DatabaseConnection.getInstance().getConnection();
            if (conn == null) {
                System.out.println("Error: Unable to connect to database. Exiting...");
                return;
            }

            adminService = new AdminServiceImpl(conn);
            instructorService = new InstructorServiceImpl(conn);
            courseService = new CourseServiceImpl(conn);
            studentService = new StudentServiceImpl(conn);
            quizService = new QuizServiceImpl(conn);
            enrollmentDAO = new EnrollmentDAOImpl(conn);
            quizResultService = new QuizResultServiceImpl(conn);

            User loggedInUser = login();

            if (loggedInUser != null) {
                showMenuByRole(loggedInUser);
            } else {
                System.out.println("Login failed. Exiting...");
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                DatabaseConnection.getInstance().closeConnection();
            } catch (Exception e) {
                // Ignore
            }
            scanner.close();
        }
    }

    private static User login() {
        System.out.println("\n--- Login ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        List<Admin> admins = adminService.getAllAdmins();
        for (Admin admin : admins) {
            if (admin.getEmail().equalsIgnoreCase(email) && admin.getPassword().equals(password)) {
                System.out.println("\nLogin successful! Welcome " + admin.getName());
                return admin;
            }
        }

        List<Instructor> instructors = instructorService.getAllInstructors();
        for (Instructor instructor : instructors) {
            if (instructor.getEmail().equalsIgnoreCase(email) && instructor.getPassword().equals(password)) {
                System.out.println("\nLogin successful! Welcome " + instructor.getName());
                return instructor;
            }
        }

        List<Student> students = studentService.getAllStudents();
        for (Student student : students) {
            if (student.getEmail().equalsIgnoreCase(email) && student.getPassword().equals(password)) {
                System.out.println("\nLogin successful! Welcome " + student.getName());
                return student;
            }
        }

        System.out.println("Invalid email or password!");
        return null;
    }

    private static void showMenuByRole(User user) {
        Role role = user.getRole();

        switch (role) {
            case ADMIN:
                showAdminMenu((Admin) user);
                break;
            case INSTRUCTOR:
                showInstructorMenu((Instructor) user);
                break;
            case STUDENT:
                showStudentMenu((Student) user);
                break;
            default:
                System.out.println("Unknown role!");
        }
    }

    private static void showAdminMenu(Admin admin) {
        boolean running = true;
        while (running) {
            System.out.println("\n========== ADMIN MENU ==========");
            System.out.println("1. Create Course");
            System.out.println("2. Delete Course");
            System.out.println("3. Create User");
            System.out.println("4. Delete User");
            System.out.println("5. View All Courses");
            System.out.println("6. View All Users");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createCourse();
                    break;
                case "2":
                    deleteCourse();
                    break;
                case "3":
                    createUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "5":
                    viewAllCourses();
                    break;
                case "6":
                    viewAllUsers();
                    break;
                case "7":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void showInstructorMenu(Instructor instructor) {
        boolean running = true;
        while (running) {
            System.out.println("\n========== INSTRUCTOR MENU ==========");
            System.out.println("1. Create Quiz");
            System.out.println("2. View Assigned Courses");
            System.out.println("3. View All Quizzes");
            System.out.println("4. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createQuiz();
                    break;
                case "2":
                    viewAssignedCourses(instructor);
                    break;
                case "3":
                    viewAllQuizzes();
                    break;
                case "4":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    private static void showStudentMenu(Student student) {
        boolean running = true;
        while (running) {
            System.out.println("\n========== STUDENT MENU ==========");
            System.out.println("1. Register for Course");
            System.out.println("2. View My Courses");
            System.out.println("3. View Course Details");
            System.out.println("4. Take Quiz");
            System.out.println("5. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerForCourse(student);
                    break;
                case "2":
                    viewMyCourses(student);
                    break;
                case "3":
                    viewCourseDetails(student);
                    break;
                case "4":
                    takeQuiz(student);
                    break;
                case "5":
                    System.out.println("Logging out...");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    // ==================== Admin Functions ====================

    private static void createCourse() {
        System.out.println("\n--- Create Course ---");
        System.out.print("Course Code: ");
        String code = scanner.nextLine().trim();
        System.out.print("Course Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Level: ");
        String level = scanner.nextLine().trim();
        System.out.print("Major: ");
        String major = scanner.nextLine().trim();
        System.out.print("Lecture Time: ");
        String lectureTime = scanner.nextLine().trim();
        System.out.print("Instructor ID: ");
        String instructorIdStr = scanner.nextLine().trim();

        try {
            int instructorId = Integer.parseInt(instructorIdStr);
            Course course = new Course(code, name, level, major, lectureTime, null, null, instructorId);
            courseService.addCourse(course);
            System.out.println("Course created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating course: " + e.getMessage());
        }
    }

    private static void deleteCourse() {
        System.out.println("\n--- Delete Course ---");
        viewAllCourses();
        System.out.print("Enter Course Code to delete: ");
        String code = scanner.nextLine().trim();

        try {
            courseService.deleteCourse(code);
            System.out.println("Course deleted successfully!");
        } catch (Exception e) {
            System.out.println("Error deleting course: " + e.getMessage());
        }
    }

    private static void createUser() {
        System.out.println("\n--- Create User ---");
        System.out.println("Select User Type:");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Student");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        try {
            switch (choice) {
                case "1":
                    Admin admin = new Admin(0, name, email, password, Role.ADMIN);
                    adminService.addAdmin(admin);
                    System.out.println("Admin created successfully!");
                    break;
                case "2":
                    System.out.print("Department (CS/IS/IT/AI): ");
                    String deptStr = scanner.nextLine().trim().toUpperCase();
                    Department dept = Department.valueOf(deptStr);
                    Instructor instructor = new Instructor(0, name, email, password, Role.INSTRUCTOR, dept);
                    instructorService.addInstructor(instructor);
                    System.out.println("Instructor created successfully!");
                    break;
                case "3":
                    System.out.print("Level: ");
                    int level = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Major: ");
                    String major = scanner.nextLine().trim();
                    System.out.print("Department (CS/IS/IT/AI): ");
                    String studentDeptStr = scanner.nextLine().trim().toUpperCase();
                    Department studentDept = Department.valueOf(studentDeptStr);
                    Student student = new Student(0, name, email, password, Role.STUDENT, level, major, null, 0, studentDept, 0.0);
                    studentService.addStudent(student);
                    System.out.println("Student created successfully!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- Delete User ---");
        System.out.println("Select User Type:");
        System.out.println("1. Admin");
        System.out.println("2. Instructor");
        System.out.println("3. Student");
        System.out.print("Choice: ");
        String choice = scanner.nextLine().trim();

        System.out.print("Enter User ID to delete: ");
        String idStr = scanner.nextLine().trim();

        try {
            int id = Integer.parseInt(idStr);
            switch (choice) {
                case "1":
                    adminService.deleteAdmin(id);
                    System.out.println("Admin deleted successfully!");
                    break;
                case "2":
                    instructorService.deleteInstructor(id);
                    System.out.println("Instructor deleted successfully!");
                    break;
                case "3":
                    studentService.deleteStudent(id);
                    System.out.println("Student deleted successfully!");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    private static void viewAllCourses() {
        System.out.println("\n--- All Courses ---");
        List<Course> courses = courseService.getAllCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses found.");
        } else {
            for (Course course : courses) {
                System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName() +
                                 " | Level: " + course.getLevel() + " | Major: " + course.getMajor());
            }
        }
    }

    private static void viewAllUsers() {
        System.out.println("\n--- All Users ---");

        System.out.println("\nAdmins:");
        List<Admin> admins = adminService.getAllAdmins();
        for (Admin admin : admins) {
            System.out.println("ID: " + admin.getId() + " | Name: " + admin.getName() + " | Email: " + admin.getEmail());
        }

        System.out.println("\nInstructors:");
        List<Instructor> instructors = instructorService.getAllInstructors();
        for (Instructor instructor : instructors) {
            System.out.println("ID: " + instructor.getId() + " | Name: " + instructor.getName() + " | Email: " + instructor.getEmail());
        }

        System.out.println("\nStudents:");
        List<Student> students = studentService.getAllStudents();
        for (Student student : students) {
            System.out.println("ID: " + student.getId() + " | Name: " + student.getName() + " | Email: " + student.getEmail());
        }
    }

    // ==================== Instructor Functions ====================

    private static void createQuiz() {
        System.out.println("\n--- Create Quiz ---");
        System.out.print("Quiz Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Course Code: ");
        String courseCode = scanner.nextLine().trim();

        List<Question> questions = new ArrayList<>();
        System.out.print("Number of questions: ");
        String numStr = scanner.nextLine().trim();

        try {
            int numQuestions = Integer.parseInt(numStr);

            for (int i = 0; i < numQuestions; i++) {
                System.out.println("\nQuestion " + (i + 1) + ":");
                System.out.print("Question text: ");
                String questionText = scanner.nextLine().trim();

                List<String> choices = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    System.out.print("Choice " + (j + 1) + ": ");
                    choices.add(scanner.nextLine().trim());
                }

                System.out.print("Correct answer index (0-3): ");
                int correctIndex = Integer.parseInt(scanner.nextLine().trim());

                Question question = new Question(0, questionText, choices, correctIndex);
                questions.add(question);
            }

            Quiz quiz = new Quiz(0, title, courseCode, questions);
            quizService.createQuiz(quiz);
            System.out.println("Quiz created successfully!");
        } catch (Exception e) {
            System.out.println("Error creating quiz: " + e.getMessage());
        }
    }

    private static void viewAssignedCourses(Instructor instructor) {
        System.out.println("\n--- My Assigned Courses ---");
        List<Course> allCourses = courseService.getAllCourses();
        boolean found = false;

        for (Course course : allCourses) {
            if (course.getInstructorId() == instructor.getId()) {
                System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName() +
                                 " | Level: " + course.getLevel() + " | Time: " + course.getLectureTime());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No courses assigned to you.");
        }
    }

    private static void viewAllQuizzes() {
        System.out.println("\n--- All Quizzes ---");
        List<Quiz> quizzes = quizService.getAllQuizzes();
        if (quizzes.isEmpty()) {
            System.out.println("No quizzes found.");
        } else {
            for (Quiz quiz : quizzes) {
                System.out.println("ID: " + quiz.getId() + " | Title: " + quiz.getTitle() +
                                 " | Course: " + quiz.getCourseCode());
            }
        }
    }

    // ==================== Student Functions ====================

    private static void registerForCourse(Student student) {
        System.out.println("\n--- Register for Course ---");
        viewAllCourses();
        System.out.print("Enter Course Code to register: ");
        String courseCode = scanner.nextLine().trim();

        try {
            enrollmentDAO.enrollStudentInCourse(student.getId(), courseCode);
            System.out.println("Successfully registered for course!");
        } catch (Exception e) {
            System.out.println("Error registering for course: " + e.getMessage());
        }
    }

    private static void viewMyCourses(Student student) {
        System.out.println("\n--- My Courses ---");
        try {
            List<Course> myCourses = enrollmentDAO.getCoursesByStudentId(student.getId());
            if (myCourses.isEmpty()) {
                System.out.println("You are not registered in any courses.");
            } else {
                for (Course course : myCourses) {
                    System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName() +
                                     " | Level: " + course.getLevel() + " | Time: " + course.getLectureTime());
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching courses: " + e.getMessage());
        }
    }

    private static void viewCourseDetails(Student student) {
        System.out.println("\n--- Course Details ---");
        viewMyCourses(student);
        System.out.print("\nEnter Course Code to view details: ");
        String courseCode = scanner.nextLine().trim();

        try {
            Course course = courseService.getCourseByCode(courseCode);
            if (course != null) {
                System.out.println("\n=== Course Details ===");
                System.out.println("Code: " + course.getCode());
                System.out.println("Name: " + course.getCourseName());
                System.out.println("Level: " + course.getLevel());
                System.out.println("Major: " + course.getMajor());
                System.out.println("Lecture Time: " + course.getLectureTime());
                System.out.println("Instructor ID: " + course.getInstructorId());

                List<Quiz> quizzes = quizService.getQuizzesByCourseCode(courseCode);
                System.out.println("\nQuizzes:");
                if (quizzes.isEmpty()) {
                    System.out.println("No quizzes available for this course.");
                } else {
                    for (Quiz quiz : quizzes) {
                        System.out.println("  - " + quiz.getTitle());
                    }
                }
            } else {
                System.out.println("Course not found.");
            }
        } catch (Exception e) {
            System.out.println("Error fetching course details: " + e.getMessage());
        }
    }

    private static void takeQuiz(Student student) {
        System.out.println("\n--- Take Quiz ---");
        List<Course> myCourses;
        try {
            myCourses = enrollmentDAO.getCoursesByStudentId(student.getId());
            if (myCourses.isEmpty()) {
                System.out.println("You are not registered in any courses. Cannot take quiz.");
                return;
            }
        } catch (Exception e) {
            System.out.println("Error fetching courses: " + e.getMessage());
            return;
        }

        System.out.println("Available Quizzes:");
        List<Quiz> availableQuizzes = new ArrayList<>();
        for (Course course : myCourses) {
            List<Quiz> quizzes = quizService.getQuizzesByCourseCode(course.getCode());
            availableQuizzes.addAll(quizzes);
        }

        if (availableQuizzes.isEmpty()) {
            System.out.println("No quizzes available for your courses.");
            return;
        }

        for (int i = 0; i < availableQuizzes.size(); i++) {
            Quiz quiz = availableQuizzes.get(i);
            System.out.println((i + 1) + ". " + quiz.getTitle() + " (Course: " + quiz.getCourseCode() + ")");
        }

        System.out.print("Select a quiz to take (1-" + availableQuizzes.size() + "): ");
        try {
            int quizIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

            if (quizIndex < 0 || quizIndex >= availableQuizzes.size()) {
                System.out.println("Invalid quiz selection.");
                return;
            }

            Quiz selectedQuiz = availableQuizzes.get(quizIndex);
            System.out.println("\n=== Starting Quiz: " + selectedQuiz.getTitle() + " ===");

            List<Question> questions = selectedQuiz.getQuestions();
            int score = 0;
            Map<Question, String> answers = new HashMap<>();

            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                System.out.println("\nQuestion " + (i + 1) + ": " + question.getText());
                List<String> options = question.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    System.out.println((j + 1) + ". " + options.get(j));
                }

                System.out.print("Your answer (1-" + options.size() + "): ");
                int answerIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;

                if (answerIndex >= 0 && answerIndex < options.size()) {
                    String chosenAnswer = options.get(answerIndex);
                    answers.put(question, chosenAnswer);

                    if (answerIndex == question.getCorrectOptionIndex()) {
                        score++;
                        System.out.println("✓ Correct!");
                    } else {
                        System.out.println("✗ Incorrect. The correct answer was: " + options.get(question.getCorrectOptionIndex()));
                    }
                } else {
                    System.out.println("Invalid answer. Marking as incorrect.");
                    answers.put(question, "Invalid");
                }
            }

            QuizResult result = new QuizResult(student, selectedQuiz, score, answers);
            quizResultService.saveResult(result);
            System.out.println("\n=== Quiz Completed! ===");
            System.out.println("Your score: " + score + "/" + questions.size() + " (" + (score * 100 / questions.size()) + "%)");
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (Exception e) {
            System.out.println("Error during quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
