package com.ums.system.controller;

import com.ums.system.App;
import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;
import java.util.Optional;

/**
 * InstructorController - Handles Instructor panel functionality
 * Provides access to instructor features from CLI
 */
public class InstructorController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    // My Courses Tab
    @FXML private TableView<Course> myCoursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Course, String> courseMajorColumn;
    @FXML private TableColumn<Course, String> courseLectureTimeColumn;
    @FXML private TableColumn<Course, Integer> courseStudentsColumn;
    @FXML private TableColumn<Course, Integer> courseQuizzesColumn;

    // All Courses Tab
    @FXML private TableView<Course> allCoursesTable;
    @FXML private TableColumn<Course, Integer> allCourseIdColumn;
    @FXML private TableColumn<Course, String> allCourseNameColumn;
    @FXML private TableColumn<Course, String> allCourseCodeColumn;

    // Students Tab
    @FXML private ComboBox<Course> courseCombo;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;
    @FXML private TableColumn<Student, String> studentLevelColumn;

    // Quizzes Tab
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, Integer> quizCourseColumn;
    @FXML private TableColumn<Quiz, Integer> quizQuestionsColumn;
    @FXML private TextField quizTitleField;
    @FXML private ComboBox<Course> quizCourseCombo;

    // Quiz Results Tab
    @FXML private TableView<QuizResult> resultsTable;
    @FXML private TableColumn<QuizResult, Integer> resultIdColumn;
    @FXML private TableColumn<QuizResult, Integer> resultStudentColumn;
    @FXML private TableColumn<QuizResult, Integer> resultQuizColumn;
    @FXML private TableColumn<QuizResult, Double> resultScoreColumn;
    @FXML private ComboBox<Quiz> resultQuizCombo;

    // Services
    private Instructor currentInstructor;
    private CourseService courseService;
    private StudentService studentService;
    private QuizService quizService;
    private QuizResultService quizResultService;
    private EnrollmentDAO enrollmentDAO;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Get services from ServiceLocator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        studentService = serviceLocator.getStudentService();
        quizService = serviceLocator.getQuizService();
        quizResultService = serviceLocator.getQuizResultService();
        enrollmentDAO = serviceLocator.getEnrollmentDAO();

        // Set up tables
        setupMyCoursesTable();
        setupAllCoursesTable();
        setupStudentsTable();
        setupQuizzesTable();
        setupResultsTable();
    }

    /**
     * Set current instructor user
     */
    public void setUser(Instructor instructor) {
        this.currentInstructor = instructor;
        welcomeLabel.setText("Welcome, " + instructor.getName() + "!");
        userInfoLabel.setText("Role: Instructor | ID: " + instructor.getId() +
                            " | Department: " + instructor.getDepartment());

        // Load initial data
        loadMyCourses();
        loadAllCourses();
        loadMyQuizzes();
    }

    /**
     * Setup tables
     */
    private void setupMyCoursesTable() {
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        courseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        courseStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        courseQuizzesColumn.setCellValueFactory(new PropertyValueFactory<>("quizCount"));
    }

    private void setupAllCoursesTable() {
        allCourseIdColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        allCourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        allCourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
    }

    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
    }

    private void setupQuizzesTable() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quizCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        // Note: Quiz doesn't have totalQuestions property - using questions list size
        quizQuestionsColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            int count = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
    }

    private void setupResultsTable() {
        // QuizResult has student and quiz objects, not IDs
        resultIdColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(result.hashCode()).asObject();
        });
        resultStudentColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            int id = result.getStudent() != null ? result.getStudent().getId() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(id).asObject();
        });
        resultQuizColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            int id = result.getQuiz() != null ? result.getQuiz().getId() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(id).asObject();
        });
        resultScoreColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            return new javafx.beans.property.SimpleDoubleProperty(result.getScore()).asObject();
        });
    }

    // ==================== MY COURSES TAB ====================

    @FXML
    private void loadMyCourses() {
        try {
            List<Course> courses = courseService.getCoursesByInstructorId(currentInstructor.getId());
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            myCoursesTable.setItems(coursesList);

            // Update combo boxes
            courseCombo.setItems(coursesList);
            quizCourseCombo.setItems(coursesList);

            System.out.println("Loaded " + courses.size() + " courses for instructor");
        } catch (Exception e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    // ==================== ALL COURSES TAB ====================

    @FXML
    private void loadAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            allCoursesTable.setItems(coursesList);
            System.out.println("Loaded " + courses.size() + " courses");
        } catch (Exception e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    // ==================== STUDENTS TAB ====================

    @FXML
    private void handleLoadStudents() {
        Course selectedCourse = courseCombo.getValue();
        if (selectedCourse == null) {
            showError("Please select a course first!");
            return;
        }

        try {
            List<Student> students = enrollmentDAO.getStudentsByCourseCode(selectedCourse.getCode());
            ObservableList<Student> studentsList = FXCollections.observableArrayList(students);
            studentsTable.setItems(studentsList);
            System.out.println("Loaded " + students.size() + " students for course: " + selectedCourse.getCourseName());
        } catch (Exception e) {
            showError("Error loading students: " + e.getMessage());
        }
    }

    // ==================== QUIZZES TAB ====================

    @FXML
    private void loadMyQuizzes() {
        try {
            List<Quiz> quizzes = quizService.getQuizzesByInstructor(currentInstructor.getId());
            ObservableList<Quiz> quizzesList = FXCollections.observableArrayList(quizzes);
            quizzesTable.setItems(quizzesList);

            // Update results combo
            resultQuizCombo.setItems(quizzesList);

            System.out.println("Loaded " + quizzes.size() + " quizzes");
        } catch (Exception e) {
            showError("Error loading quizzes: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateQuiz() {
        String title = quizTitleField.getText().trim();
        Course selectedCourse = quizCourseCombo.getValue();

        if (title.isEmpty() || selectedCourse == null) {
            showError("Please enter quiz title and select a course!");
            return;
        }

        try {
            // Create quiz with constructor (id, title, courseCode, questions)
            Quiz quiz = new Quiz(0, title, selectedCourse.getCode(), null);

            quizService.createQuiz(quiz, currentInstructor.getId());
            showInfo("Quiz created successfully! You can now add questions.");

            quizTitleField.clear();
            loadMyQuizzes();

        } catch (Exception e) {
            showError("Error creating quiz: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteQuiz() {
        Quiz selected = quizzesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a quiz to delete!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Quiz");
        confirm.setContentText("Are you sure you want to delete: " + selected.getTitle() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                quizService.deleteQuiz(selected.getId());
                showInfo("Quiz deleted successfully!");
                loadMyQuizzes();
            } catch (Exception e) {
                showError("Error deleting quiz: " + e.getMessage());
            }
        }
    }

    // ==================== QUIZ RESULTS TAB ====================

    @FXML
    private void handleLoadResults() {
        Quiz selectedQuiz = resultQuizCombo.getValue();
        if (selectedQuiz == null) {
            showError("Please select a quiz first!");
            return;
        }

        try {
            List<QuizResult> results = quizResultService.getResultsByQuizId(selectedQuiz.getId());
            ObservableList<QuizResult> resultsList = FXCollections.observableArrayList(results);
            resultsTable.setItems(resultsList);
            System.out.println("Loaded " + results.size() + " results for quiz: " + selectedQuiz.getTitle());
        } catch (Exception e) {
            showError("Error loading results: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewAllResults() {
        try {
            // Get all results for instructor's quizzes
            List<Quiz> myQuizzes = quizService.getQuizzesByInstructor(currentInstructor.getId());
            ObservableList<QuizResult> allResults = FXCollections.observableArrayList();

            for (Quiz quiz : myQuizzes) {
                List<QuizResult> results = quizResultService.getResultsByQuizId(quiz.getId());
                allResults.addAll(results);
            }

            resultsTable.setItems(allResults);
            System.out.println("Loaded " + allResults.size() + " total results");
        } catch (Exception e) {
            showError("Error loading all results: " + e.getMessage());
        }
    }

    // ==================== UTILITY METHODS ====================

    @FXML
    private void handleLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Logout");
        confirm.setHeaderText("Confirm Logout");
        confirm.setContentText("Are you sure you want to logout?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            App.showLoginScreen();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
