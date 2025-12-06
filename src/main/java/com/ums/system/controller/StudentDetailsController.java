package com.ums.system.controller;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.dao.EnrollmentDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;

public class StudentDetailsController {

    @FXML private Label studentNameLabel;
    @FXML private Label studentIdLabel;
    @FXML private Label fullNameLabel;
    @FXML private Label emailLabel;
    @FXML private Label levelLabel;
    @FXML private Label majorLabel;
    @FXML private Label departmentLabel;
    @FXML private Label gradeLabel;
    @FXML private Label paymentStatusLabel;
    @FXML private Label coursesCountLabel;
    @FXML private Label averageScoreLabel;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Course, String> courseMajorColumn;
    @FXML private TableColumn<Course, String> courseInstructorColumn;

    @FXML private TableView<QuizResult> quizResultsTable;
    @FXML private TableColumn<QuizResult, String> quizTitleColumn;
    @FXML private TableColumn<QuizResult, String> quizCourseColumn;
    @FXML private TableColumn<QuizResult, Integer> quizScoreColumn;
    @FXML private TableColumn<QuizResult, Integer> quizCorrectColumn;
    @FXML private TableColumn<QuizResult, Integer> quizTotalColumn;
    @FXML private TableColumn<QuizResult, String> quizDateColumn;

    private StudentService studentService;
    private InstructorService instructorService;
    private EnrollmentDAO enrollmentDAO;
    private QuizResultService quizResultService;
    private PaymentService paymentService;

    private Student currentStudent;
    private Stage previousStage;

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        studentService = serviceLocator.getStudentService();
        instructorService = serviceLocator.getInstructorService();
        enrollmentDAO = serviceLocator.getEnrollmentDAO();
        quizResultService = serviceLocator.getQuizResultService();
        paymentService = new PaymentServiceImpl(serviceLocator.getConnection());

        setupCoursesTable();
        setupQuizResultsTable();
    }

    public void setStudent(Student student, Stage previousStage) {
        this.currentStudent = student;
        this.previousStage = previousStage;
        loadStudentData();
    }

    private void setupCoursesTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));

        courseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            try {
                Instructor instructor = instructorService.getInstructorById(instructorId);
                if (instructor != null) {
                    return new javafx.beans.property.SimpleStringProperty(instructor.getName());
                }
            } catch (Exception e) {
                // Ignore
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
    }

    private void setupQuizResultsTable() {
        // Quiz title
        quizTitleColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            return new javafx.beans.property.SimpleStringProperty(
                quiz != null ? quiz.getTitle() : "N/A"
            );
        });

        quizCourseColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            if (quiz != null && quiz.getCourseCode() != null) {
                return new javafx.beans.property.SimpleStringProperty(quiz.getCourseCode());
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        quizScoreColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            int rawScore = result.getScore();
            int totalQuestions = quiz != null && quiz.getQuestions() != null ?
                quiz.getQuestions().size() : 0;
            int percentage = (totalQuestions > 0) ? (rawScore * 100 / totalQuestions) : 0;
            return new javafx.beans.property.SimpleIntegerProperty(percentage).asObject();
        });

        quizCorrectColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(result.getScore()).asObject();
        });

        quizTotalColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            int totalQuestions = quiz != null && quiz.getQuestions() != null ?
                quiz.getQuestions().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(totalQuestions).asObject();
        });

        quizDateColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
    }

    private void loadStudentData() {
        if (currentStudent == null) {
            return;
        }

        try {
            Student student = studentService.getStudentById(currentStudent.getId());
            if (student != null) {
                this.currentStudent = student;
            }

            studentNameLabel.setText(currentStudent.getName());
            studentIdLabel.setText(String.valueOf(currentStudent.getId()));
            fullNameLabel.setText(currentStudent.getName());
            emailLabel.setText(currentStudent.getEmail());
            levelLabel.setText("Level " + currentStudent.getLevel() + " (" + getLevelName(currentStudent.getLevel()) + ")");
            majorLabel.setText(currentStudent.getMajor());
            departmentLabel.setText(currentStudent.getDepartmentName() != null ?
                currentStudent.getDepartmentName().toString() : "N/A");
            gradeLabel.setText(String.format("%.2f%%", currentStudent.getGrade()));

            boolean hasPaid = paymentService.hasUserPaidForLevel(
                currentStudent.getId(),
                currentStudent.getLevel()
            );
            if (hasPaid) {
                paymentStatusLabel.setText("✅ PAID");
                paymentStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            } else {
                paymentStatusLabel.setText("❌ NOT PAID");
                paymentStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }

            List<Course> courses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            coursesTable.setItems(coursesList);
            coursesCountLabel.setText(String.valueOf(courses.size()) + " courses");

            List<QuizResult> results = quizResultService.getResultsByStudentId(currentStudent.getId());
            ObservableList<QuizResult> resultsList = FXCollections.observableArrayList(results);
            quizResultsTable.setItems(resultsList);

            averageScoreLabel.setText(String.format("Average: %.2f%%", currentStudent.getGrade()));

            System.out.println("Loaded details for student: " + currentStudent.getName());

        } catch (Exception e) {
            showError("Error loading student details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getLevelName(int level) {
        switch (level) {
            case 1: return "Freshman";
            case 2: return "Sophomore";
            case 3: return "Junior";
            case 4: return "Senior";
            default: return "Unknown";
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage currentStage = (Stage) studentNameLabel.getScene().getWindow();

            currentStage.close();

            if (previousStage != null) {
                previousStage.show();
            }

        } catch (Exception e) {
            showError("Error returning to previous screen: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Student Details Error");
        alert.setContentText(message);
        alert.showAndWait();
    }
}

