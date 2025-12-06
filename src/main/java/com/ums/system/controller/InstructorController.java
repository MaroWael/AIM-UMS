package com.ums.system.controller;

import com.ums.system.App;
import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.dao.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class InstructorController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    @FXML private TableView<Course> myCoursesTable;
    @FXML private TableColumn<Course, String> courseIdColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Course, String> courseMajorColumn;
    @FXML private TableColumn<Course, String> courseLectureTimeColumn;
    @FXML private TableColumn<Course, Integer> courseStudentsColumn;
    @FXML private TableColumn<Course, Integer> courseQuizzesColumn;

    @FXML private ComboBox<Course> courseCombo;
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;
    @FXML private TableColumn<Student, String> studentLevelColumn;
    @FXML private TableColumn<Student, String> studentMajorColumn;
    @FXML private TableColumn<Student, String> studentDepartmentColumn;

    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, Integer> quizCourseColumn;
    @FXML private TableColumn<Quiz, Integer> quizQuestionsColumn;

    @FXML private TableView<QuizResult> resultsTable;
    @FXML private TableColumn<QuizResult, Integer> resultStudentIdColumn;
    @FXML private TableColumn<QuizResult, String> resultStudentNameColumn;
    @FXML private TableColumn<QuizResult, Integer> resultQuizIdColumn;
    @FXML private TableColumn<QuizResult, String> resultQuizTitleColumn;
    @FXML private TableColumn<QuizResult, String> resultCourseCodeColumn;
    @FXML private TableColumn<QuizResult, Integer> resultScoreColumn;
    @FXML private ComboBox<Quiz> resultQuizCombo;

    private Instructor currentInstructor;
    private CourseService courseService;
    private StudentService studentService;
    private QuizService quizService;
    private QuizResultService quizResultService;
    private EnrollmentDAO enrollmentDAO;

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        studentService = serviceLocator.getStudentService();
        quizService = serviceLocator.getQuizService();
        quizResultService = serviceLocator.getQuizResultService();
        enrollmentDAO = serviceLocator.getEnrollmentDAO();

        setupMyCoursesTable();
        setupStudentsTable();
        setupQuizzesTable();
        setupResultsTable();

        setupCourseComboBoxes();
    }

    private void setupCourseComboBoxes() {
        courseCombo.setCellFactory(param -> new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCode() + " - " + course.getCourseName());
                }
            }
        });

        courseCombo.setButtonCell(new ListCell<Course>() {
            @Override
            protected void updateItem(Course course, boolean empty) {
                super.updateItem(course, empty);
                if (empty || course == null) {
                    setText(null);
                } else {
                    setText(course.getCode() + " - " + course.getCourseName());
                }
            }
        });

        resultQuizCombo.setCellFactory(param -> new ListCell<Quiz>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText(null);
                } else {
                    setText(quiz.getId() + " - " + quiz.getTitle());
                }
            }
        });

        resultQuizCombo.setButtonCell(new ListCell<Quiz>() {
            @Override
            protected void updateItem(Quiz quiz, boolean empty) {
                super.updateItem(quiz, empty);
                if (empty || quiz == null) {
                    setText(null);
                } else {
                    setText(quiz.getId() + " - " + quiz.getTitle());
                }
            }
        });
    }

    public void setUser(Instructor instructor) {
        this.currentInstructor = instructor;
        welcomeLabel.setText("Welcome, " + instructor.getName() + "!");
        userInfoLabel.setText("Role: Instructor | ID: " + instructor.getId() +
                            " | Department: " + instructor.getDepartment());

        loadMyCourses();
        loadMyQuizzes();
    }

    private void setupMyCoursesTable() {
        courseIdColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        courseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));
        courseStudentsColumn.setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        courseQuizzesColumn.setCellValueFactory(new PropertyValueFactory<>("quizCount"));
    }

    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        studentMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));

        studentDepartmentColumn.setCellValueFactory(cellData -> {
            Student student = cellData.getValue();
            Department dept = student.getDepartmentName();
            String deptName = (dept != null) ? dept.name() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(deptName);
        });
    }

    private void setupQuizzesTable() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quizCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));

        quizQuestionsColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            int count = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
    }

    private void setupResultsTable() {
        resultStudentIdColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            int id = result.getStudent() != null ? result.getStudent().getId() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(id).asObject();
        });
        resultStudentNameColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            String name = result.getStudent() != null ? result.getStudent().getName() : "";
            return new javafx.beans.property.SimpleStringProperty(name);
        });
        resultQuizIdColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            int id = result.getQuiz() != null ? result.getQuiz().getId() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(id).asObject();
        });
        resultQuizTitleColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            String title = result.getQuiz() != null ? result.getQuiz().getTitle() : "";
            return new javafx.beans.property.SimpleStringProperty(title);
        });
        resultCourseCodeColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            String code = result.getQuiz() != null && result.getQuiz().getCourseCode() != null ?
                    result.getQuiz().getCourseCode() : "";
            return new javafx.beans.property.SimpleStringProperty(code);
        });
        resultScoreColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(result.getScore()).asObject();
        });
    }

    @FXML
    private void loadMyCourses() {
        try {
            List<Course> courses = courseService.getCoursesByInstructorId(currentInstructor.getId());
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            myCoursesTable.setItems(coursesList);

            courseCombo.setItems(coursesList);

            System.out.println("Loaded " + courses.size() + " courses for instructor");
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

            resultQuizCombo.setItems(quizzesList);

            System.out.println("Loaded " + quizzes.size() + " quizzes");
        } catch (Exception e) {
            showError("Error loading quizzes: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateQuiz() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/create_quiz.fxml"));
            Parent root = loader.load();

            CreateQuizController controller = loader.getController();
            controller.setInstructor(currentInstructor);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Create New Quiz");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(quizzesTable.getScene().getWindow());
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(true);
            dialogStage.setMinWidth(700);
            dialogStage.setMinHeight(600);

            dialogStage.showAndWait();

            if (controller.isQuizCreated()) {
                loadMyQuizzes();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Error opening quiz creation dialog: " + e.getMessage());
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
            for (QuizResult result : results) {
                System.out.println("Result - Student: " +
                    (result.getStudent() != null ? result.getStudent().getName() : "NULL") +
                    ", Quiz: " + (result.getQuiz() != null ? result.getQuiz().getTitle() : "NULL") +
                    ", Score: " + result.getScore());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading results: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewAllResults() {
        try {
            List<Quiz> myQuizzes = quizService.getQuizzesByInstructor(currentInstructor.getId());
            ObservableList<QuizResult> allResults = FXCollections.observableArrayList();

            for (Quiz quiz : myQuizzes) {
                List<QuizResult> results = quizResultService.getResultsByQuizId(quiz.getId());
                allResults.addAll(results);
            }

            resultsTable.setItems(allResults);

            System.out.println("Loaded " + allResults.size() + " total results");
            for (QuizResult result : allResults) {
                System.out.println("Result - Student: " +
                    (result.getStudent() != null ? result.getStudent().getName() : "NULL") +
                    ", Quiz: " + (result.getQuiz() != null ? result.getQuiz().getTitle() : "NULL") +
                    ", Score: " + result.getScore());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading all results: " + e.getMessage());
        }
    }

    // ==================== UTILITY METHODS ====================

    @FXML
    private void handleProfile() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/profile.fxml")
            );
            javafx.scene.Parent root = loader.load();

            ProfileController profileController = loader.getController();

            javafx.stage.Stage currentStage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();

            profileController.setUser(currentInstructor, currentStage);

            javafx.stage.Stage profileStage = new javafx.stage.Stage();
            profileStage.setTitle("My Profile - " + currentInstructor.getName());
            profileStage.setScene(new javafx.scene.Scene(root));
            profileStage.setResizable(true);
            profileStage.setMinWidth(900);
            profileStage.setMinHeight(700);

            currentStage.hide();

            profileStage.show();

        } catch (Exception e) {
            showError("Error opening profile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            System.out.println("Refreshing instructor dashboard...");

            if (currentInstructor != null) {
                welcomeLabel.setText("Welcome, " + currentInstructor.getName() + "!");
                userInfoLabel.setText("Role: Instructor | ID: " + currentInstructor.getId() +
                            " | Department: " + currentInstructor.getDepartment());
            }

            loadMyCourses();
            loadMyQuizzes();

            showInfo("Dashboard refreshed successfully!");
        } catch (Exception e) {
            showError("Error refreshing dashboard: " + e.getMessage());
        }
    }

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
