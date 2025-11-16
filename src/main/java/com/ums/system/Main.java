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

    Admin admin = adminService.getAdminByEmail(email);
    if (admin != null && admin.getPassword().equals(password)) {
        System.out.println("\nLogin successful! Welcome " + admin.getName());
        return admin;
    }

    Instructor instructor = instructorService.getInstructorByEmail(email);
    if (instructor != null && instructor.getPassword().equals(password)) {
        System.out.println("\nLogin successful! Welcome " + instructor.getName());
        return instructor;
    }

    Student student = studentService.getStudentByEmail(email);
    if (student != null && student.getPassword().equals(password)) {
        System.out.println("\nLogin successful! Welcome " + student.getName());
        return student;
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
        System.out.println("7. Update Student Level");
        System.out.println("8. Logout");
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
                updateStudentLevel();
                break;
            case "8":
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
                createQuiz(instructor);
                break;
            case "2":
                viewAssignedCourses(instructor);
                break;
            case "3":
                viewAllQuizzes(instructor);
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

    System.out.println("\n--- Available Instructors ---");
    List<Instructor> instructors = instructorService.getAllInstructors();
    if (instructors.isEmpty()) {
        System.out.println("No instructors available. Please create an instructor first.");
        return;
    }
    for (Instructor instructor : instructors) {
        System.out.println("ID: " + instructor.getId() + " | Name: " + instructor.getName() +
                         " | Department: " + instructor.getDepartment());
    }

    System.out.print("\nInstructor ID: ");
    String instructorIdStr = scanner.nextLine().trim();

    try {
        int instructorId = Integer.parseInt(instructorIdStr);
        Course course = new Course(code, name, level, major, lectureTime, null, null, instructorId);
        boolean success = courseService.addCourse(course);
        if (success) {
            System.out.println("Course created successfully!");
        }
    } catch (NumberFormatException e) {
        System.out.println("Error: Invalid instructor ID format.");
    } catch (Exception e) {
        System.out.println("Error creating course: " + e.getMessage());
    }
}

private static void deleteCourse() {
    System.out.println("\n--- Delete Course ---");
    viewAllCourses();
    System.out.print("Enter Course Code to delete: ");
    String code = scanner.nextLine().trim();
    Course course = courseService.getCourseByCode(code);
    if (course == null) {
        System.out.println("course not found with Course Code: " + code);
        return;
    }

    System.out.println("\n--- Course Information ---");
    System.out.println("Course Code: " + course.getCode());
    System.out.println("Course Name: " + course.getCourseName());
    System.out.println("Major: " + course.getMajor());
    System.out.println("Level: " + course.getLevel() );
    System.out.println("Lecture Time: " + course.getLectureTime());

    System.out.print("\nAre you sure you want to delete this Course? (yes/no): ");
    String CourseConfirm = scanner.nextLine().trim().toLowerCase();

    if (CourseConfirm.equals("yes") || CourseConfirm.equals("y")) {
        courseService.deleteCourse(code);
        System.out.println("Course deleted successfully!");
    } else {
        System.out.println("Course deletion cancelled.");
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
                boolean adminCreated = adminService.addAdmin(admin);
                if (adminCreated) {
                    System.out.println("Admin created successfully!");
                }
                break;
            case "2":
                System.out.print("Department (CS/IS/IT/AI): ");
                String deptStr = scanner.nextLine().trim().toUpperCase();
                Department dept = Department.valueOf(deptStr);
                Instructor instructor = new Instructor(0, name, email, password, Role.INSTRUCTOR, dept);
                boolean instructorCreated = instructorService.addInstructor(instructor);
                if (instructorCreated) {
                    System.out.println("Instructor created successfully!");
                }
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
                boolean studentCreated = studentService.addStudent(student);
                if (studentCreated) {
                    System.out.println("Student created successfully!");
                }
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
                Admin admin = adminService.getAdminById(id);
                if (admin == null) {
                    System.out.println(" Admin not found with ID: " + id);
                    return;
                }

                System.out.println("\n--- Admin Information ---");
                System.out.println("ID: " + admin.getId());
                System.out.println("Name: " + admin.getName());
                System.out.println("Email: " + admin.getEmail());
                System.out.println("Role: " + admin.getRole());

                System.out.print("\nAre you sure you want to delete this admin? (yes/no): ");
                String adminConfirm = scanner.nextLine().trim().toLowerCase();

                if (adminConfirm.equals("yes") || adminConfirm.equals("y")) {
                    adminService.deleteAdmin(id);
                    System.out.println("Admin deleted successfully!");
                } else {
                    System.out.println("Admin deletion cancelled.");
                }
                break;

            case "2":
                Instructor instructor = instructorService.getInstructorById(id);
                if (instructor == null) {
                    System.out.println("Instructor not found with ID: " + id);
                    return;
                }

                System.out.println("\n--- Instructor Information ---");
                System.out.println("ID: " + instructor.getId());
                System.out.println("Name: " + instructor.getName());
                System.out.println("Email: " + instructor.getEmail());
                System.out.println("Role: " + instructor.getRole());
                System.out.println("Department: " + instructor.getDepartment());

                System.out.print("\n Are you sure you want to delete this instructor? (yes/no): ");
                String instructorConfirm = scanner.nextLine().trim().toLowerCase();

                if (instructorConfirm.equals("yes") || instructorConfirm.equals("y")) {
                    instructorService.deleteInstructor(id);
                    System.out.println("Instructor deleted successfully!");
                } else {
                    System.out.println("Instructor deletion cancelled.");
                }
                break;

            case "3":
                Student student = studentService.getStudentById(id);
                if (student == null) {
                    System.out.println("Student not found with ID: " + id);
                    return;
                }

                System.out.println("\n--- Student Information ---");
                System.out.println("ID: " + student.getId());
                System.out.println("Name: " + student.getName());
                System.out.println("Email: " + student.getEmail());
                System.out.println("Role: " + student.getRole());
                System.out.println("Level: " + student.getLevel());
                System.out.println("Major: " + student.getMajor());
                System.out.println("Department: " + student.getDepartmentName());
                System.out.println("Grade: " + student.getGrade());

                System.out.print("\nAre you sure you want to delete this student? (yes/no): ");
                String studentConfirm = scanner.nextLine().trim().toLowerCase();

                if (studentConfirm.equals("yes") || studentConfirm.equals("y")) {
                    studentService.deleteStudent(id);
                    System.out.println(" Student deleted successfully!");
                } else {
                    System.out.println(" Student deletion cancelled.");
                }
                break;

            default:
                System.out.println("Invalid choice!");
        }
    } catch (NumberFormatException e) {
        System.out.println(" Error: Invalid ID format.");
    } catch (Exception e) {
        System.out.println("Error deleting user: " + e.getMessage());
    }
}

private static void updateStudentLevel() {
    System.out.println("\n--- Update Student Level ---");
    List<Student> students = studentService.getAllStudents();
    if (students.isEmpty()) {
        System.out.println("No students found in the system.");
        return;
    }

    System.out.println("\n--- Available Students ---");
    for (Student s : students) {
        System.out.println("ID: " + s.getId() + " | Name: " + s.getName() +
                         " | Email: " + s.getEmail() + " | Current Level: " + s.getLevel() +
                         " | Major: " + s.getMajor() + " | Department: " + s.getDepartmentName());
    }

    System.out.print("\nEnter Student ID: ");
    String idStr = scanner.nextLine().trim();

    try {
        int studentId = Integer.parseInt(idStr);

        Student student = studentService.getStudentById(studentId);
        if (student == null) {
            System.out.println("Student not found with ID: " + studentId);
            return;
        }

        System.out.println("\n--- Student Information ---");
        System.out.println("ID: " + student.getId());
        System.out.println("Name: " + student.getName());
        System.out.println("Email: " + student.getEmail());
        System.out.println("Current Level: " + student.getLevel());
        System.out.println("Major: " + student.getMajor());
        System.out.println("Department: " + student.getDepartmentName());

        System.out.print("\nEnter New Level (1-4): ");
        String levelStr = scanner.nextLine().trim();
        int newLevel = Integer.parseInt(levelStr);

        System.out.print("\nAre you sure you want to update the level from " +
                        student.getLevel() + " to " + newLevel + "? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            boolean success = adminService.updateStudentLevel(studentId, newLevel);
            if (!success) {
                System.out.println("Failed to update student level.");
            }
        } else {
            System.out.println("Update cancelled.");
        }

    } catch (NumberFormatException e) {
        System.out.println("Error: Invalid number format.");
    } catch (Exception e) {
        System.out.println("Error updating student level: " + e.getMessage());
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

private static void createQuiz(Instructor instructor) {
    System.out.println("\n--- Create Quiz ---");

    System.out.println("\n--- Your Assigned Courses ---");
    List<Course> assignedCourses = courseService.getCoursesByInstructorId(instructor.getId());

    if (assignedCourses.isEmpty()) {
        System.out.println("You have no assigned courses. Cannot create quiz.");
        return;
    }

    for (Course course : assignedCourses) {
        System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName());
    }

    System.out.print("\nQuiz Title: ");
    String title = scanner.nextLine().trim();
    System.out.print("Course Code: ");
    String courseCode = scanner.nextLine().trim();

    Course selectedCourse = courseService.getCourseByCode(courseCode);
    if (selectedCourse == null) {
        System.out.println("Course with code " + courseCode + " does not exist.");
        return;
    }

    if (selectedCourse.getInstructorId() != instructor.getId()) {
        System.out.println("You can only create quizzes for courses assigned to you.");
        return;
    }

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
        boolean success = quizService.createQuiz(quiz, instructor.getId());
        if (success) {
            System.out.println("Quiz created successfully!");
        }
    } catch (Exception e) {
        System.out.println("Error creating quiz: " + e.getMessage());
    }
}

private static void viewAssignedCourses(Instructor instructor) {
    System.out.println("\n--- My Assigned Courses ---");
    List<Course> assignedCourses = courseService.getCoursesByInstructorId(instructor.getId());

    if (assignedCourses.isEmpty()) {
        System.out.println("No courses assigned to you.");
    } else {
        for (Course course : assignedCourses) {
            System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName() +
                             " | Level: " + course.getLevel() + " | Time: " + course.getLectureTime());
        }
    }
}

private static void viewAllQuizzes(Instructor instructor) {
    System.out.println("\n--- My Quizzes ---");
    List<Quiz> quizzes = quizService.getQuizzesByInstructor(instructor.getId());
    if (quizzes.isEmpty()) {
        System.out.println("No quizzes found for your courses.");
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

    System.out.println("Courses available for your level (" + student.getLevel() + ") and major (" + student.getMajor() + "):");
    List<Course> allCourses = courseService.getAllCourses();
    List<Course> eligibleCourses = new ArrayList<>();

    String studentLevel = String.valueOf(student.getLevel());
    for (Course course : allCourses) {
        if (course.getLevel().equals(studentLevel) &&
            course.getMajor().equalsIgnoreCase(student.getMajor())) {
            eligibleCourses.add(course);
            System.out.println("Code: " + course.getCode() + " | Name: " + course.getCourseName() +
                             " | Level: " + course.getLevel() + " | Major: " + course.getMajor());
        }
    }

    if (eligibleCourses.isEmpty()) {
        System.out.println("No courses available for your level and major.");
        return;
    }

    System.out.print("\nEnter Course Code to register: ");
    String courseCode = scanner.nextLine().trim();

    try {
        enrollmentDAO.enrollStudentInCourse(student.getId(), courseCode);
    } catch (Exception e) {
        System.out.println(" Error registering for course: " + e.getMessage());
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
                    System.out.println("Correct!");
                } else {
                    System.out.println("Incorrect. The correct answer was: " + options.get(question.getCorrectOptionIndex()));
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
