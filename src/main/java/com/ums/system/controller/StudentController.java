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

/**
 * StudentController - Handles Student panel functionality
 * Provides access to student features from CLI
 */
public class StudentController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    // Available Courses Tab
    @FXML private TableView<Course> availableCoursesTable;
    @FXML private TableColumn<Course, String> availCourseCodeColumn;
    @FXML private TableColumn<Course, String> availCourseNameColumn;
    @FXML private TableColumn<Course, String> availCourseLevelColumn;
    @FXML private TableColumn<Course, String> availCourseMajorColumn;
    @FXML private TableColumn<Course, String> availCourseInstructorColumn;
    @FXML private TableColumn<Course, String> availCourseLectureTimeColumn;

    // My Courses Tab
    @FXML private TableView<Course> myCoursesTable;
    @FXML private TableColumn<Course, String> myCourseCodeColumn;
    @FXML private TableColumn<Course, String> myCourseNameColumn;
    @FXML private TableColumn<Course, String> myCourseLevelColumn;
    @FXML private TableColumn<Course, String> myCourseMajorColumn;
    @FXML private TableColumn<Course, String> myCourseInstructorColumn;
    @FXML private TableColumn<Course, String> myCourseLectureTimeColumn;

    // Payment Tab
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

    // Quizzes Tab
    @FXML private ComboBox<Course> quizCourseCombo;
    @FXML private TableView<Quiz> quizzesTable;
    @FXML private TableColumn<Quiz, Integer> quizIdColumn;
    @FXML private TableColumn<Quiz, String> quizTitleColumn;
    @FXML private TableColumn<Quiz, String> quizCourseColumn;
    @FXML private TableColumn<Quiz, String> quizCourseNameColumn;
    @FXML private TableColumn<Quiz, Integer> quizQuestionsColumn;

    // My Grades Tab
    @FXML private TableView<QuizResult> gradesTable;
    @FXML private TableColumn<QuizResult, String> gradeQuizTitleColumn;
    @FXML private TableColumn<QuizResult, String> gradeCourseColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeScoreColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeTotalQuestionsColumn;
    @FXML private TableColumn<QuizResult, Integer> gradeAnswersColumn;
    @FXML private Label averageScoreLabel;

    // Services
    private Student currentStudent;
    private CourseService courseService;
    private StudentService studentService;
    private QuizService quizService;
    private QuizResultService quizResultService;
    private EnrollmentDAO enrollmentDAO;
    private PaymentService paymentService;
    private com.ums.system.utils.ReportGenerator reportGenerator;

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
        paymentService = new PaymentServiceImpl(serviceLocator.getConnection());

        // Set up tables
        setupAvailableCoursesTable();
        setupMyCoursesTable();
        setupPaymentTable();
        setupQuizzesTable();
        setupGradesTable();
    }

    /**
     * Set current student user
     */
    public void setUser(Student student) {
        this.currentStudent = student;
        welcomeLabel.setText("Welcome, " + student.getName() + "!");
        userInfoLabel.setText("Role: Student | ID: " + student.getId() +
                            " | Level: " + student.getLevel());

        // Load initial data
        checkPaymentStatus();
        loadPaymentHistory();
        loadAvailableCourses();
        loadMyCourses();
        loadMyGrades();
    }

    /**
     * Setup tables
     */
    private void setupAvailableCoursesTable() {
        availCourseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        availCourseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        availCourseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        availCourseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        availCourseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));

        // Custom cell value factory for instructor name instead of ID
        availCourseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            // Fetch instructor name
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

        // Custom cell value factory for instructor name instead of ID
        myCourseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            // Fetch instructor name
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

        // Format date column
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

        // Style status column based on payment status
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

            // Fetch course name from course code
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

            // Try to get course name, fallback to course code
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

            // Calculate percentage: (score * 100 / totalQuestions)
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
            // This shows the raw score (correct answers count)
            return new javafx.beans.property.SimpleIntegerProperty(result.getScore()).asObject();
        });
    }

    // ==================== PAYMENT TAB ====================

    /**
     * Check and display payment status for current level
     */
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

    /**
     * Load payment history for current student
     */
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

    /**
     * Handle pay level fee button click
     */
    @FXML
    private void handlePayLevelFee() {
        int studentLevel = currentStudent.getLevel();

        // Check if already paid
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

        // Show payment method selection dialog
        Alert paymentDialog = new Alert(Alert.AlertType.CONFIRMATION);
        paymentDialog.setTitle("Pay Level Fee");
        paymentDialog.setHeaderText("Level " + studentLevel + " Tuition Fee Payment");
        paymentDialog.setContentText(
            "Amount: " + levelFee + " EGP\n\n" +
            "Select payment method:"
        );

        // Create payment method choice box
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

    /**
     * Process payment in background thread
     */
    private void processPayment(int level, double amount, String method) {
        // Show processing dialog
        Alert processingAlert = new Alert(Alert.AlertType.INFORMATION);
        processingAlert.setTitle("Processing Payment");
        processingAlert.setHeaderText("Please wait...");
        processingAlert.setContentText("Processing your level fee payment...\nThis may take a few seconds.");
        processingAlert.show();

        // Process payment in background thread
        new Thread(() -> {
            try {
                // Create payment request
                PaymentRequest request = new PaymentRequest(
                    currentStudent.getId(),
                    level,
                    amount,
                    "EGP",
                    "Level " + level + " tuition fee",
                    method
                );

                // Process payment
                Payment payment = paymentService.processPayment(request);

                // Update UI on JavaFX thread
                javafx.application.Platform.runLater(() -> {
                    processingAlert.close();

                    if ("SUCCESS".equals(payment.getStatus())) {
                        // Success dialog
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

                        // Refresh payment status and history
                        checkPaymentStatus();
                        loadPaymentHistory();

                    } else {
                        // Failed dialog
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
                // Already paid or validation error
                javafx.application.Platform.runLater(() -> {
                    processingAlert.close();
                    Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                    warningAlert.setTitle("Payment Not Allowed");
                    warningAlert.setHeaderText("Payment Issue");
                    warningAlert.setContentText(e.getMessage());
                    warningAlert.showAndWait();
                    checkPaymentStatus(); // Refresh status
                });
            } catch (Exception e) {
                // Other errors
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

    /**
     * Check if student has paid before allowing quiz access
     * @return true if paid, false otherwise
     */
    private boolean checkPaymentBeforeQuizAccess() {
        try {
            int studentLevel = currentStudent.getLevel();
            boolean hasPaid = paymentService.hasUserPaidForLevel(currentStudent.getId(), studentLevel);

            if (!hasPaid) {
                double levelFee = paymentService.calculateLevelFee(studentLevel);

                // Show payment required dialog
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
                    // Switch to payment tab - you could add code here to switch tabs
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

    // ==================== AVAILABLE COURSES TAB ====================

    @FXML
    private void loadAvailableCourses() {
        try {
            // Get all courses
            List<Course> allCourses = courseService.getAllCourses();

            // Get enrolled courses
            List<Course> enrolledCourses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());

            // Create a set of enrolled course codes for efficient lookup
            java.util.Set<String> enrolledCourseCodes = enrolledCourses.stream()
                .map(Course::getCode)
                .collect(java.util.stream.Collectors.toSet());

            // Filter courses by student's level and major, and exclude already enrolled courses
            List<Course> filteredCourses = allCourses.stream()
                .filter(course -> {
                    // Check if course level matches student level
                    boolean levelMatches = course.getLevel().equals(String.valueOf(currentStudent.getLevel()));

                    // Check if course major matches student major
                    boolean majorMatches = course.getMajor().equalsIgnoreCase(currentStudent.getMajor());

                    // Check if student is not already enrolled in this course
                    boolean notEnrolled = !enrolledCourseCodes.contains(course.getCode());

                    return levelMatches && majorMatches && notEnrolled;
                })
                .collect(java.util.stream.Collectors.toList());

            ObservableList<Course> coursesList = FXCollections.observableArrayList(filteredCourses);
            availableCoursesTable.setItems(coursesList);
            System.out.println("Loaded " + filteredCourses.size() + " available courses for level "
                + currentStudent.getLevel() + " and major " + currentStudent.getMajor());

            // Set placeholder message if no courses are available
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

        // Check if student is already enrolled in this course
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
                // Enroll using existing method
                enrollmentDAO.enrollStudentInCourse(currentStudent.getId(), selected.getCode());
                showInfo("Successfully enrolled in: " + selected.getCourseName());
                loadMyCourses();
                loadAvailableCourses(); // Refresh available courses

            } catch (Exception e) {
                showError("Error enrolling in course: " + e.getMessage());
            }
        }
    }

    // ==================== MY COURSES TAB ====================

    @FXML
    private void loadMyCourses() {
        try {
            List<Course> courses = enrollmentDAO.getCoursesByStudentId(currentStudent.getId());
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            myCoursesTable.setItems(coursesList);

            // Update quiz course combo with custom string converter
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

    // ==================== QUIZZES TAB ====================

    @FXML
    private void handleLoadQuizzes() {
        // Check payment status first
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
        // Check payment status first
        if (!checkPaymentBeforeQuizAccess()) {
            return;
        }

        Quiz selected = quizzesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a quiz to take!");
            return;
        }

        // Check if quiz has questions
        if (selected.getQuestions() == null || selected.getQuestions().isEmpty()) {
            showError("This quiz has no questions!");
            return;
        }

        try {
            // Load the Take Quiz interface
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/take_quiz.fxml")
            );
            javafx.scene.Parent root = loader.load();

            // Get controller and set quiz data
            TakeQuizController controller = loader.getController();
            controller.setQuizData(selected, currentStudent);

            // Create new stage for quiz
            javafx.stage.Stage quizStage = new javafx.stage.Stage();
            quizStage.setTitle("Take Quiz - " + selected.getTitle());
            quizStage.setScene(new javafx.scene.Scene(root));
            quizStage.setResizable(true);
            quizStage.setMinWidth(900);
            quizStage.setMinHeight(700);

            // Make it modal
            quizStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            quizStage.initOwner(quizzesTable.getScene().getWindow());

            // Refresh grades when quiz window closes
            quizStage.setOnHidden(event -> loadMyGrades());

            quizStage.show();

        } catch (Exception e) {
            showError("Error opening quiz: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllQuizzes() {
        // Check payment status first
        if (!checkPaymentBeforeQuizAccess()) {
            return;
        }

        try {
            // Get all quizzes for all enrolled courses
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

    // ==================== MY GRADES TAB ====================

    @FXML
    private void loadMyGrades() {
        try {
            List<QuizResult> results = quizResultService.getResultsByStudentId(currentStudent.getId());
            ObservableList<QuizResult> resultsList = FXCollections.observableArrayList(results);
            gradesTable.setItems(resultsList);

            // Calculate average score using the student's overall grade from database (like Main.java)
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
            // Load the new FXML view
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/quiz_result_detail.fxml")
            );
            javafx.scene.Parent root = loader.load();

            // Get the controller and set the data
            QuizResultDetailController controller = loader.getController();
            controller.setQuizResult(selected, currentStudent);

            // Create a new stage (window) for the details
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

    /**
     * Generate Academic Report (PDF)
     */
    @FXML
    private void handleGenerateReport() {
        // Show confirmation dialog
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

        // Show progress dialog
        Alert progressAlert = new Alert(Alert.AlertType.INFORMATION);
        progressAlert.setTitle("Generating Report");
        progressAlert.setHeaderText("Please wait...");
        progressAlert.setContentText("Generating your academic report...");
        progressAlert.show();

        // Generate report in background thread to avoid UI freeze
        new Thread(() -> {
            try {
                // Get fresh student data
                Student updatedStudent = studentService.getStudentById(currentStudent.getId());
                if (updatedStudent == null) {
                    updatedStudent = currentStudent;
                }

                // Make it final for use in lambda
                final Student finalStudent = updatedStudent;

                // Get ReportGenerator from ServiceLocator
                if (reportGenerator == null) {
                    reportGenerator = ServiceLocator.getInstance().getReportGenerator();
                }

                // Generate the report
                String filename = reportGenerator.generateStudentReport(finalStudent);

                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    progressAlert.close();

                    // Show success dialog with file location
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

                    // Add button to open the file location
                    ButtonType openFolderButton = new ButtonType("Open Folder");
                    ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                    success.getButtonTypes().setAll(openFolderButton, okButton);

                    Optional<ButtonType> choice = success.showAndWait();
                    if (choice.isPresent() && choice.get() == openFolderButton) {
                        try {
                            // Open the reports folder
                            java.io.File file = new java.io.File(filename);
                            if (file.exists()) {
                                // Open the folder containing the file
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
                // Update UI on JavaFX Application Thread
                javafx.application.Platform.runLater(() -> {
                    progressAlert.close();
                    showError("Error generating report: " + e.getMessage());
                });
            }
        }).start();
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

