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

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StudentController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    @FXML private Label totalCoursesLabel;
    @FXML private Label completedQuizzesLabel;
    @FXML private Label averageGradeLabel;
    @FXML private Label paymentStatusShortLabel;

    @FXML private TableView<Course> availableCoursesTable;
    @FXML private TableColumn<Course, String> availCourseCodeColumn;
    @FXML private TableColumn<Course, String> availCourseNameColumn;
    @FXML private TableColumn<Course, String> availCourseLevelColumn;
    @FXML private TableColumn<Course, String> availCourseMajorColumn;
    @FXML private TableColumn<Course, String> availCourseInstructorColumn;
    @FXML private TableColumn<Course, String> availCourseLectureTimeColumn;

    @FXML private TableView<Course> myCoursesTable;
    @FXML private TableColumn<Course, String> myCourseCodeColumn;
    @FXML private TableColumn<Course, String> myCourseNameColumn;
    @FXML private TableColumn<Course, String> myCourseLevelColumn;
    @FXML private TableColumn<Course, String> myCourseMajorColumn;
    @FXML private TableColumn<Course, String> myCourseInstructorColumn;
    @FXML private TableColumn<Course, String> myCourseLectureTimeColumn;

    @FXML private Label paymentStatusLabel;
    @FXML private Label paymentAmountLabel;
    @FXML private Button payLevelFeeButton;
    @FXML private TableView<Payment> paymentHistoryTable;
    @FXML private TableColumn<Payment, Integer> paymentIdColumn;
    @FXML private TableColumn<Payment, Integer> paymentLevelColumn;
    @FXML private TableColumn<Payment, Double> paymentAmountColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;
    @FXML private TableColumn<Payment, String> paymentTransactionColumn;
    @FXML private TableColumn<Payment, String> paymentDateColumn;

    @FXML private ComboBox<Course> quizCourseCombo;
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizCourseColumn;
    @FXML private TableColumn<Quiz, String> quizCourseNameColumn;
    @FXML private TableColumn<Quiz, Integer> quizQuestionsColumn;

    @FXML private TableView<QuizResult> gradesTable;
    @FXML private TableColumn<QuizResult, String> gradeQuizTitleColumn;
    @FXML private TableColumn<QuizResult, String> gradeCourseColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeScoreColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeTotalQuestionsColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeAnswersColumn;
    @FXML private Label averageScoreLabel;

    private Student currentStudent;
    private CourseService courseService;
    private StudentService studentService;
    private QuizService quizService;
    private QuizResultService quizResultService;
    private EnrollmentDAO enrollmentDAO;
    private PaymentService paymentService;
    private com.ums.system.utils.ReportGenerator reportGenerator;

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        studentService = serviceLocator.getStudentService();
        quizService = serviceLocator.getQuizService();
        quizResultService = serviceLocator.getQuizResultService();
        enrollmentDAO = serviceLocator.getEnrollmentDAO();
        paymentService = new PaymentServiceImpl(serviceLocator.getConnection());

        setupAvailableCoursesTable();
        setupMyCoursesTable();
        setupPaymentTable();
        setupQuizzesTable();
        setupGradesTable();
    }

    public void setUser(Student student) {
        this.currentStudent = student;
        welcomeLabel.setText("Welcome, " + student.getName() + "!");
        userInfoLabel.setText("Role: Student | ID: " + student.getId() +
                            " | Level: " + student.getLevel());

        updateDashboardStatistics();
        checkPaymentStatus();
        loadPaymentHistory();
        loadAvailableCourses();
        loadMyCourses();
        loadMyGrades();
    }

    private void setupAvailableCoursesTable() {
        availCourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        availCourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        availCourseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        availCourseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        availCourseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));

        availCourseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            try {
                InstructorService instructorService = ServiceLocator.getInstance().getInstructorService();
                Instructor instructor = instructorService.getInstructorById(instructorId);
                if (instructor != null) {
                    return new javafx.beans.property.SimpleStringProperty(instructor.getName());
                } else {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
    }

    private void setupMyCoursesTable() {
        myCourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        myCourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        myCourseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        myCourseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        myCourseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));

        myCourseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            try {
                InstructorService instructorService = ServiceLocator.getInstance().getInstructorService();
                Instructor instructor = instructorService.getInstructorById(instructorId);
                if (instructor != null) {
                    return new javafx.beans.property.SimpleStringProperty(instructor.getName());
                } else {
                    return new javafx.beans.property.SimpleStringProperty("N/A");
                }
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("Error");
            }
        });
    }

    private void setupPaymentTable() {
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        paymentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentTransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        paymentDateColumn.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            if (payment.getCreatedAt() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return new javafx.beans.property.SimpleStringProperty(
                    payment.getCreatedAt().format(formatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        paymentStatusColumn.setCellFactory(column -> new TableCell<Payment, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("SUCCESS".equals(status)) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else if ("FAILED".equals(status)) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else if ("PENDING".equals(status)) {
                        setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupQuizzesTable() {
        quizIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        quizTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        quizCourseColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        quizCourseNameColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            String courseCode = quiz.getCourseCode();

            try {
                Course course = courseService.getCourseByCode(courseCode);
                return new javafx.beans.property.SimpleStringProperty(course != null ? course.getCourseName() : "N/A");
            } catch (Exception e) {
                return new javafx.beans.property.SimpleStringProperty("N/A");
            }
        });
        quizQuestionsColumn.setCellValueFactory(cellData -> {
            Quiz quiz = cellData.getValue();
            int count = quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(count).asObject();
        });
    }

    private void setupGradesTable() {
        gradeQuizTitleColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            return new javafx.beans.property.SimpleStringProperty(quiz != null ? quiz.getTitle() : "N/A");
        });
        gradeCourseColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            String courseCode = quiz != null ? quiz.getCourseCode() : null;

            try {
                if (courseCode != null) {
                    Course course = courseService.getCourseByCode(courseCode);
                    return new javafx.beans.property.SimpleStringProperty(course != null ? course.getCourseName() : courseCode);
                }
            } catch (Exception e) {
                // Fallback to course code if lookup fails
            }
            return new javafx.beans.property.SimpleStringProperty(courseCode != null ? courseCode : "N/A");
        });
        gradeScoreColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();

            int rawScore = result.getScore();
            int totalQuestions = quiz != null && quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;

            int percentage = (totalQuestions > 0) ? (rawScore * 100 / totalQuestions) : 0;
            return new javafx.beans.property.SimpleIntegerProperty(percentage).asObject();
        });
        gradeTotalQuestionsColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            Quiz quiz = result.getQuiz();
            int totalQuestions = quiz != null && quiz.getQuestions() != null ? quiz.getQuestions().size() : 0;
            return new javafx.beans.property.SimpleIntegerProperty(totalQuestions).asObject();
        });
        gradeAnswersColumn.setCellValueFactory(cellData -> {
            QuizResult result = cellData.getValue();
            return new javafx.beans.property.SimpleIntegerProperty(result.getScore()).asObject();
        });
    }

    private void checkPaymentStatus() {
        try {
            int studentLevel = currentStudent.getLevel();
            boolean hasPaid = paymentService.hasUserPaidForLevel(currentStudent.getId(), studentLevel);
            double levelFee = paymentService.calculateLevelFee(studentLevel);

            if (hasPaid) {
                paymentStatusLabel.setText("✅ Payment Status: PAID for Level " + studentLevel);
                paymentStatusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 16px; -fx-font-weight: bold;");
                paymentAmountLabel.setText("Level " + studentLevel + " Fee: " + levelFee + " EGP (Paid)");
                payLevelFeeButton.setDisable(true);
                payLevelFeeButton.setText("✅ Already Paid");
            } else {
                paymentStatusLabel.setText("⚠️ Payment Status: NOT PAID for Level " + studentLevel);
                paymentStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-font-weight: bold;");
                paymentAmountLabel.setText("Level " + studentLevel + " Fee: " + levelFee + " EGP (Due)");
                payLevelFeeButton.setDisable(false);
                payLevelFeeButton.setText("💳 Pay Level Fee");
            }
        } catch (Exception e) {
            paymentStatusLabel.setText("❌ Error checking payment status");
            paymentStatusLabel.setStyle("-fx-text-fill: orange;");
            showError("Error checking payment status: " + e.getMessage());
        }
    }

    @FXML
    private void loadPaymentHistory() {
        try {
            List<Payment> payments = paymentService.getPaymentsByUserId(currentStudent.getId());
            ObservableList<Payment> paymentsList = FXCollections.observableArrayList(payments);
            paymentHistoryTable.setItems(paymentsList);
            System.out.println("Loaded " + payments.size() + " payment records");
        } catch (Exception e) {
            showError("Error loading payment history: " + e.getMessage());
        }
    }

    @FXML
    private void handlePayLevelFee() {
        int studentLevel = currentStudent.getLevel();

        try {
            if (paymentService.hasUserPaidForLevel(currentStudent.getId(), studentLevel)) {
                showInfo("You have already paid for Level " + studentLevel);
                return;
            }
        } catch (Exception e) {
            showError("Error checking payment status: " + e.getMessage());
            return;
        }

        double levelFee = paymentService.calculateLevelFee(studentLevel);

        Alert paymentDialog = new Alert(Alert.AlertType.CONFIRMATION);
        paymentDialog.setTitle("Pay Level Fee");
        paymentDialog.setHeaderText("Level " + studentLevel + " Tuition Fee Payment");
        paymentDialog.setContentText(
            "Amount: " + levelFee + " EGP\n\n" +
            "Select payment method:"
        );

        ChoiceBox<String> paymentMethodBox = new ChoiceBox<>();
        paymentMethodBox.getItems().addAll("CARD", "BANK_TRANSFER", "CASH");
        paymentMethodBox.setValue("CARD");
        paymentMethodBox.setStyle("-fx-font-size: 14px;");

        paymentDialog.getDialogPane().setContent(paymentMethodBox);

        Optional<ButtonType> result = paymentDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            processPayment(studentLevel, levelFee, paymentMethodBox.getValue());
        }
    }

    private void processPayment(int level, double amount, String method) {
        Alert processingAlert = new Alert(Alert.AlertType.INFORMATION);
        processingAlert.setTitle("Processing Payment");
        processingAlert.setHeaderText("Please wait...");
        processingAlert.setContentText("Processing your level fee payment...\nThis may take a few seconds.");
        processingAlert.show();

        new Thread(() -> {
            try {
                PaymentRequest request = new PaymentRequest(
                    currentStudent.getId(),
                    level,
                    amount,
                    "EGP",
                    "Level " + level + " tuition fee",
                    method
                );

                Payment payment = paymentService.processPayment(request);

                javafx.application.Platform.runLater(() -> {
                    processingAlert.close();

                    if ("SUCCESS".equals(payment.getStatus())) {
                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Payment Successful");
                        successAlert.setHeaderText("✅ Level Fee Paid Successfully!");
                        successAlert.setContentText(
                            "Transaction ID: " + payment.getTransactionId() + "\n" +
                            "Amount: " + payment.getAmount() + " EGP\n" +
                            "Level: " + payment.getLevel() + "\n" +
                            "Method: " + payment.getPaymentMethod() + "\n\n" +
                            "You can now access quizzes and all features!"
                        );
                        successAlert.showAndWait();

                        checkPaymentStatus();
                        loadPaymentHistory();

                    } else {
                        Alert failAlert = new Alert(Alert.AlertType.ERROR);
                        failAlert.setTitle("Payment Failed");
                        failAlert.setHeaderText("❌ Payment Processing Failed");
                        failAlert.setContentText(
                            "Status: " + payment.getStatus() + "\n\n" +
                            "Please try again or contact administration."
                        );
                        failAlert.showAndWait();
                    }
                });

            } catch (IllegalArgumentException e) {
                javafx.application.Platform.runLater(() -> {
                    processingAlert.close();
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setTitle("Payment Not Allowed");
                    warningAlert.setHeaderText("Payment Issue");
                    warningAlert.setContentText(e.getMessage());
                    warningAlert.showAndWait();
                    checkPaymentStatus();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    processingAlert.close();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText("An error occurred");
                    errorAlert.setContentText(e.getMessage());
                    errorAlert.showAndWait();
                });
            }
        }).start();
    }

    private boolean checkPaymentBeforeQuizAccess() {
        try {
            int studentLevel = currentStudent.getLevel();
            boolean hasPaid = paymentService.hasUserPaidForLevel(currentStudent.getId(), studentLevel);

            if (!hasPaid) {
                double levelFee = paymentService.calculateLevelFee(studentLevel);

                Alert paymentRequired = new Alert(Alert.AlertType.WARNING);
                paymentRequired.setTitle("Payment Required");
                paymentRequired.setHeaderText("⚠️ Level Fee Payment Required");
                paymentRequired.setContentText(
                    "You must pay your Level " + studentLevel + " fee to access quizzes.\n\n" +
                    "Fee Amount: " + levelFee + " EGP\n\n" +
                    "Please go to the Payment tab to complete your payment."
                );

                ButtonType goToPaymentButton = new ButtonType("Go to Payment Tab");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                paymentRequired.getButtonTypes().setAll(goToPaymentButton, cancelButton);

                Optional<ButtonType> result = paymentRequired.showAndWait();
                if (result.isPresent() && result.get() == goToPaymentButton) {
                    showInfo("Please complete your payment in the Payment tab.");
                }

                return false;
            }

            return true;

        } catch (Exception e) {
            showError("Error checking payment status: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void loadAvailableCourses() {
        try {
            List<Course> allCourses = courseService.getAllCourses();

            List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());

            java.util.Set<String> enrolledCourseCodes = enrolledCourses.stream()
                .map(Course::getCode)
                .collect(java.util.stream.Collectors.toSet());

            List<Course> filteredCourses = allCourses.stream()
                .filter(course -> {
                    boolean levelMatches = course.getLevel().equals(String.valueOf(currentStudent.getLevel()));

                    boolean majorMatches = course.getMajor().equalsIgnoreCase(currentStudent.getMajor());

                    boolean notEnrolled = !enrolledCourseCodes.contains(course.getCode());

                    return levelMatches && majorMatches && notEnrolled;
                })
                .collect(java.util.stream.Collectors.toList());

            ObservableList<Course> coursesList = FXCollections.observableArrayList(filteredCourses);
            availableCoursesTable.setItems(coursesList);
            System.out.println("Loaded " + filteredCourses.size() + " available courses for level "
                + currentStudent.getLevel() + " and major " + currentStudent.getMajor());

            if (filteredCourses.isEmpty()) {
                Label placeholder = new Label("No available courses to enroll.\n\n" +
                        "All courses for your level (" + currentStudent.getLevel() + ") and major (" +
                        currentStudent.getMajor() + ") are already enrolled or there are no courses matching your criteria.");
                placeholder.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-text-alignment: center;");
                placeholder.setWrapText(true);
                placeholder.setMaxWidth(600);
                availableCoursesTable.setPlaceholder(placeholder);
            } else {
                availableCoursesTable.setPlaceholder(new Label("No courses available"));
            }
        } catch (Exception e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleEnrollCourse() {
        Course selected = availableCoursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a course to enroll!");
            return;
        }

        try {
            List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            boolean alreadyEnrolled = enrolledCourses.stream()
                .anyMatch(course -> course.getCode().equals(selected.getCode()));

            if (alreadyEnrolled) {
                showError("You are already enrolled in: " + selected.getCourseName());
                return;
            }
        } catch (Exception e) {
            showError("Error checking enrollment status: " + e.getMessage());
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Enrollment");
        confirm.setHeaderText("Enroll in Course");
        confirm.setContentText("Do you want to enroll in: " + selected.getCourseName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                enrollmentDAO.enrollStudentInCourse(currentStudent.getId(), selected.getCode());
                showInfo("Successfully enrolled in: " + selected.getCourseName());
                loadMyCourses();
                loadAvailableCourses();

            } catch (Exception e) {
                showError("Error enrolling in course: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadMyCourses() {
        try {
            List<Course> courses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            myCoursesTable.setItems(coursesList);

            quizCourseCombo.setItems(coursesList);
            quizCourseCombo.setConverter(new javafx.util.StringConverter<Course>() {
                @Override
                public String toString(Course course) {
                    if (course == null) {
                        return null;
                    }
                    return course.getCode() + " - " + course.getCourseName();
                }

                @Override
                public Course fromString(String string) {
                    return null;
                }
            });

            System.out.println("Loaded " + courses.size() + " enrolled courses");
        } catch (Exception e) {
            showError("Error loading enrolled courses: " + e.getMessage());
        }
    }

    @FXML
    private void handleDropCourse() {
        Course selected = myCoursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a course to drop!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Drop");
        confirm.setHeaderText("Drop Course");
        confirm.setContentText("Are you sure you want to drop: " + selected.getCourseName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                enrollmentDAO.removeStudentFromCourse(currentStudent.getId(), selected.getCode());
                showInfo("Successfully dropped: " + selected.getCourseName());
                loadMyCourses();

            } catch (Exception e) {
                showError("Error dropping course: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleLoadQuizzes() {
        if (!checkPaymentBeforeQuizAccess()) {
            return;
        }

        Course selectedCourse = quizCourseCombo.getValue();
        if (selectedCourse == null) {
            showError("Please select a course first!");
            return;
        }

        try {
            List<Quiz> quizzes = quizService.getQuizzesByCourseCode(selectedCourse.getCode());
            ObservableList<Quiz> quizzesList = FXCollections.observableArrayList(quizzes);
            quizzesTable.setItems(quizzesList);
            System.out.println("Loaded " + quizzes.size() + " quizzes for course: " + selectedCourse.getCourseName());
        } catch (Exception e) {
            showError("Error loading quizzes: " + e.getMessage());
        }
    }

    @FXML
    private void handleTakeQuiz() {
        if (!checkPaymentBeforeQuizAccess()) {
            return;
        }

        Quiz selected = quizzesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a quiz to take!");
            return;
        }

        if (selected.getQuestions() == null || selected.getQuestions().isEmpty()) {
            showError("This quiz has no questions!");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/take_quiz.fxml")
            );
            javafx.scene.Parent root = loader.load();

            TakeQuizController controller = loader.getController();
            controller.setQuizData(selected, currentStudent);

            javafx.stage.Stage quizStage = new javafx.stage.Stage();
            quizStage.setTitle("Take Quiz - " + selected.getTitle());
            quizStage.setScene(new javafx.scene.Scene(root));
            quizStage.setResizable(true);
            quizStage.setMinWidth(900);
            quizStage.setMinHeight(700);

            quizStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            quizStage.initOwner(quizzesTable.getScene().getWindow());

            quizStage.setOnHidden(event -> loadMyGrades());

            quizStage.show();

        } catch (Exception e) {
            showError("Error opening quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllQuizzes() {
        if (!checkPaymentBeforeQuizAccess()) {
            return;
        }

        try {
            List<Course> myCourses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            ObservableList<Quiz> allQuizzes = FXCollections.observableArrayList();

            for (Course course : myCourses) {
                List<Quiz> quizzes = quizService.getQuizzesByCourseCode(course.getCode());
                allQuizzes.addAll(quizzes);
            }

            quizzesTable.setItems(allQuizzes);
            System.out.println("Loaded " + allQuizzes.size() + " total quizzes");
        } catch (Exception e) {
            showError("Error loading all quizzes: " + e.getMessage());
        }
    }

    @FXML
    private void loadMyGrades() {
        try {
            List<QuizResult> results = quizResultService.getResultsByStudentId(currentStudent.getId());
            ObservableList<QuizResult> resultsList = FXCollections.observableArrayList(results);
            gradesTable.setItems(resultsList);

            if (!results.isEmpty()) {
                Student updatedStudent = studentService.getStudentById(currentStudent.getId());
                if (updatedStudent != null) {
                    averageScoreLabel.setText(String.format("Average Score: %.2f%%", updatedStudent.getGrade()));
                } else {
                    averageScoreLabel.setText("Average Score: N/A");
                }
            } else {
                averageScoreLabel.setText("Average Score: 0.00%");
            }

            System.out.println("Loaded " + results.size() + " quiz results");
        } catch (Exception e) {
            showError("Error loading grades: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewGradeDetails() {
        QuizResult selected = gradesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a grade to view details!");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/quiz_result_detail.fxml")
            );
            javafx.scene.Parent root = loader.load();

            QuizResultDetailController controller = loader.getController();
            controller.setQuizResult(selected, currentStudent);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Quiz Result Details - " + (selected.getQuiz() != null ? selected.getQuiz().getTitle() : "Quiz"));
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setResizable(true);
            stage.show();

        } catch (Exception e) {
            showError("Error loading grade details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGenerateReport() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Generate Report");
        confirm.setHeaderText("Generate Academic Report (PDF)");
        confirm.setContentText("This will generate a comprehensive PDF report containing:\n\n" +
                              "• Your personal information\n" +
                              "• Overall grade and performance\n" +
                              "• List of registered courses\n" +
                              "• Grade for each course\n" +
                              "• Quiz results summary\n\n" +
                              "Do you want to continue?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Generating Report");
        progressAlert.setHeaderText("Please wait...");
        progressAlert.setContentText("Generating your academic report...");
        progressAlert.show();

        new Thread(() -> {
            try {
                Student updatedStudent = studentService.getStudentById(currentStudent.getId());
                if (updatedStudent == null) {
                    updatedStudent = currentStudent;
                }

                final Student finalStudent = updatedStudent;

                if (reportGenerator == null) {
                    reportGenerator = ServiceLocator.getInstance().getReportGenerator();
                }

                String filename = reportGenerator.generateStudentReport(finalStudent);

                javafx.application.Platform.runLater(() -> {
                    progressAlert.close();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Report Generated");
                    success.setHeaderText("Academic Report Generated Successfully!");
                    success.setContentText("Your academic report has been saved to:\n\n" +
                                          filename + "\n\n" +
                                          "The report contains:\n" +
                                          "  • Your personal information\n" +
                                          "  • Overall grade: " + String.format("%.2f%%", finalStudent.getGrade()) + "\n" +
                                          "  • List of registered courses\n" +
                                          "  • Grade for each course\n" +
                                          "  • Quiz results summary");

                    ButtonType openFolderButton = new ButtonType("Open Folder");
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    success.getButtonTypes().setAll(openFolderButton, okButton);

                    Optional<ButtonType> choice = success.showAndWait();
                    if (choice.isPresent() && choice.get() == openFolderButton) {
                        try {
                            java.io.File file = new java.io.File(filename);
                            if (file.exists()) {
                                if (java.awt.Desktop.isDesktopSupported()) {
                                    java.awt.Desktop.getDesktop().open(file.getParentFile());
                                }
                            }
                        } catch (Exception e) {
                            showError("Could not open folder: " + e.getMessage());
                        }
                    }
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    progressAlert.close();
                    showError("Error generating report: " + e.getMessage());
                });
            }
        }).start();
    }

    private void updateDashboardStatistics() {
        try {
            List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            int totalCourses = enrolledCourses.size();
            totalCoursesLabel.setText(String.valueOf(totalCourses));

            List<QuizResult> myResults = quizResultService.getResultsByStudentId(currentStudent.getId());
            int completedQuizzes = myResults.size();

            int totalAvailableQuizzes = 0;
            for (Course course : enrolledCourses) {
                List<Quiz> courseQuizzes = quizService.getQuizzesByCourseCode(course.getCode());
                totalAvailableQuizzes += courseQuizzes.size();
            }
            completedQuizzesLabel.setText(completedQuizzes + "/" + totalAvailableQuizzes);

            double averageGrade = currentStudent.getGrade();
            averageGradeLabel.setText(String.format("%.2f%%", averageGrade));

            boolean hasPaid = paymentService.hasUserPaidForLevel(
                currentStudent.getId(),
                currentStudent.getLevel()
            );
            if (hasPaid) {
                paymentStatusShortLabel.setText("✅ PAID");
                paymentStatusShortLabel.setStyle("-fx-text-fill: white;");
            } else {
                paymentStatusShortLabel.setText("❌ NOT PAID");
                paymentStatusShortLabel.setStyle("-fx-text-fill: white;");
            }

            System.out.println("Dashboard statistics updated successfully");

        } catch (Exception e) {
            System.err.println("Error updating dashboard statistics: " + e.getMessage());
            totalCoursesLabel.setText("0");
            completedQuizzesLabel.setText("0/0");
            averageGradeLabel.setText("0.00%");
            paymentStatusShortLabel.setText("N/A");
        }
    }

    @FXML
    private void handleProfile() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/profile.fxml")
            );
            javafx.scene.Parent root = loader.load();

            ProfileController profileController = loader.getController();

            javafx.stage.Stage currentStage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();

            profileController.setUser(currentStudent, currentStage);

            javafx.stage.Stage profileStage = new javafx.stage.Stage();
            profileStage.setTitle("My Profile - " + currentStudent.getName());
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
            System.out.println("Refreshing student dashboard...");

            if (currentStudent != null) {
                welcomeLabel.setText("Welcome, " + currentStudent.getName() + "!");
                userInfoLabel.setText("Role: Student | ID: " + currentStudent.getId() +
                            " | Level: " + currentStudent.getLevel());
            }

            updateDashboardStatistics();
            checkPaymentStatus();
            loadPaymentHistory();
            loadAvailableCourses();
            loadMyCourses();
            loadMyGrades();

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

