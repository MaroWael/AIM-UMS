package com.ums.system.controller;

import com.ums.system.App;
import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.utils.PasswordUtil;
import com.ums.system.utils.ValidationUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;


public class AdminController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    @FXML private TableView<Course> coursesTable;
    @FXML private TableColumn<Course, String> courseCodeColumn;
    @FXML private TableColumn<Course, String> courseNameColumn;
    @FXML private TableColumn<Course, String> courseLevelColumn;
    @FXML private TableColumn<Course, String> courseMajorColumn;
    @FXML private TableColumn<Course, String> courseLectureTimeColumn;
    @FXML private TableColumn<Course, String> courseInstructorColumn;
    @FXML private TextField courseNameField;
    @FXML private TextField courseCodeField;
    @FXML private ComboBox<String> courseMajorCombo;
    @FXML private TextField courseLevelField;
    @FXML private TextField courseLectureTimeField;
    @FXML private ComboBox<Instructor> courseInstructorCombo;

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;
    @FXML private PasswordField userPasswordField;

    @FXML private GridPane studentFieldsPane;
    @FXML private TextField studentLevelField;
    @FXML private ComboBox<String> studentMajorCombo;
    @FXML private ComboBox<Department> studentDepartmentCombo;
    @FXML private HBox instructorFieldsPane;
    @FXML private ComboBox<Department> instructorDepartmentCombo;

    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;
    @FXML private TableColumn<Student, String> studentLevelColumn;

    @FXML private TableView<Instructor> instructorsTable;
    @FXML private TableColumn<Instructor, Integer> instructorIdColumn;
    @FXML private TableColumn<Instructor, String> instructorNameColumn;
    @FXML private TableColumn<Instructor, String> instructorEmailColumn;
    @FXML private TableColumn<Instructor, String> instructorDeptColumn;

    @FXML private Label totalRevenueLabel;
    @FXML private Label totalPaymentsLabel;
    @FXML private Label level1RevenueLabel;
    @FXML private Label level2RevenueLabel;
    @FXML private Label level3RevenueLabel;
    @FXML private Label level4RevenueLabel;
    @FXML private Label successfulPaymentsLabel;
    @FXML private Label failedPaymentsLabel;
    @FXML private Label pendingPaymentsLabel;
    @FXML private TableView<Payment> paymentsTable;
    @FXML private TableColumn<Payment, Integer> paymentIdColumn;
    @FXML private TableColumn<Payment, Integer> paymentUserIdColumn;
    @FXML private TableColumn<Payment, String> paymentUserNameColumn;
    @FXML private TableColumn<Payment, Integer> paymentLevelColumn;
    @FXML private TableColumn<Payment, Double> paymentAmountColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentStatusColumn;
    @FXML private TableColumn<Payment, String> paymentTransactionColumn;
    @FXML private TableColumn<Payment, String> paymentDateColumn;
    @FXML private ComboBox<String> paymentStatusFilterCombo;
    @FXML private ComboBox<String> paymentLevelFilterCombo;

    private Admin currentAdmin;
    private CourseService courseService;
    private AdminService adminService;
    private InstructorService instructorService;
    private StudentService studentService;
    private PaymentService paymentService;


    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        adminService = serviceLocator.getAdminService();
        instructorService = serviceLocator.getInstructorService();
        studentService = serviceLocator.getStudentService();
        paymentService = new PaymentServiceImpl(serviceLocator.getConnection());

        setupCoursesTable();
        setupUsersTable();
        setupStudentsTable();
        setupInstructorsTable();
        setupPaymentsTable();

        setupPaymentFilters();

        ObservableList<String> majors = FXCollections.observableArrayList(
            "Computer Science", "Information Systems", "Information Technology", "Artificial Intelligence"
        );
        courseMajorCombo.setItems(majors);
        studentMajorCombo.setItems(majors);

        ObservableList<Department> departments = FXCollections.observableArrayList(Department.values());
        studentDepartmentCombo.setItems(departments);
        instructorDepartmentCombo.setItems(departments);

        userTypeCombo.setItems(FXCollections.observableArrayList("Admin", "Instructor", "Student"));
        userTypeCombo.setValue("Admin");

        userTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            handleUserTypeChange(newVal);
        });

        studentFieldsPane.setVisible(false);
        studentFieldsPane.setManaged(false);
        instructorFieldsPane.setVisible(false);
        instructorFieldsPane.setManaged(false);
    }


    private void handleUserTypeChange(String userType) {
        studentFieldsPane.setVisible(false);
        studentFieldsPane.setManaged(false);
        instructorFieldsPane.setVisible(false);
        instructorFieldsPane.setManaged(false);

        switch (userType) {
            case "Student":
                studentFieldsPane.setVisible(true);
                studentFieldsPane.setManaged(true);
                break;
            case "Instructor":
                instructorFieldsPane.setVisible(true);
                instructorFieldsPane.setManaged(true);
                break;
            case "Admin":
                break;
        }
    }

    public void setUser(Admin admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText("Welcome, " + admin.getName() + "!");
        userInfoLabel.setText("Role: Admin | ID: " + admin.getId() + " | Email: " + admin.getEmail());

        loadInstructorsIntoCombo();

        loadAllCourses();
        loadAllUsers();
        loadAllStudents();
        loadAllInstructors();
        loadAllPayments();
        updateRevenueStatistics();
    }


    private void loadInstructorsIntoCombo() {
        try {
            List<Instructor> instructors = instructorService.getAllInstructors();

            if (instructors.isEmpty()) {
                System.out.println("No instructors available. Please create an instructor first.");
            }

            courseInstructorCombo.setItems(FXCollections.observableArrayList(instructors));
            courseInstructorCombo.setConverter(new javafx.util.StringConverter<Instructor>() {
                @Override
                public String toString(Instructor instructor) {
                    if (instructor == null) {
                        return null;
                    }
                    return "ID: " + instructor.getId() + " | " + instructor.getName() + " | " + instructor.getDepartment();
                }

                @Override
                public Instructor fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showError("Error loading instructors: " + e.getMessage());
        }
    }


    private void setupCoursesTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        courseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));

        courseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            try {
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


    private void setupUsersTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }


    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
    }


    private void setupInstructorsTable() {
        instructorIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        instructorNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        instructorEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        instructorDeptColumn.setCellValueFactory(new PropertyValueFactory<>("department"));
    }

    // ==================== COURSES TAB ACTIONS ====================

    @FXML
    private void loadAllCourses() {
        try {
            List<Course> courses = courseService.getAllCourses();
            ObservableList<Course> coursesList = FXCollections.observableArrayList(courses);
            coursesTable.setItems(coursesList);
            System.out.println("Loaded " + courses.size() + " courses");
        } catch (Exception e) {
            showError("Error loading courses: " + e.getMessage());
        }
    }


    @FXML
    private void handleCreateCourse() {
        String code = courseCodeField.getText().trim();
        String name = courseNameField.getText().trim();
        String level = courseLevelField.getText().trim();
        String major = courseMajorCombo.getValue();
        String lectureTime = courseLectureTimeField.getText().trim();
        Instructor instructor = courseInstructorCombo.getValue();

        clearFieldErrors(courseCodeField, courseNameField, courseLevelField, courseLectureTimeField);
        courseMajorCombo.setStyle("");
        courseInstructorCombo.setStyle("");

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        if (code.isEmpty()) {
            setFieldError(courseCodeField, "Course Code is required");
            errorMessage.append("- Course Code is required\n");
            hasError = true;
        } else if (code.length() < 3) {
            setFieldError(courseCodeField, "Course Code must be at least 3 characters");
            errorMessage.append("- Course Code must be at least 3 characters\n");
            hasError = true;
        }

        if (name.isEmpty()) {
            setFieldError(courseNameField, "Course Name is required");
            errorMessage.append("- Course Name is required\n");
            hasError = true;
        } else if (name.length() < 3) {
            setFieldError(courseNameField, "Course Name must be at least 3 characters");
            errorMessage.append("- Course Name must be at least 3 characters\n");
            hasError = true;
        }

        if (level.isEmpty()) {
            setFieldError(courseLevelField, "Level is required");
            errorMessage.append("- Level is required\n");
            hasError = true;
        } else {
            try {
                int levelNum = Integer.parseInt(level);
                if (levelNum < 1 || levelNum > 4) {
                    setFieldError(courseLevelField, "Level must be between 1 and 4");
                    errorMessage.append("- Level must be between 1 and 4\n");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                setFieldError(courseLevelField, "Level must be a valid number (1-4)");
                errorMessage.append("- Level must be a valid number (1-4)\n");
                hasError = true;
            }
        }

        if (major == null || major.isEmpty()) {
            setComboBoxError(courseMajorCombo);
            errorMessage.append("- Major is required\n");
            hasError = true;
        }

        if (lectureTime.isEmpty()) {
            setFieldError(courseLectureTimeField, "Lecture Time is required");
            errorMessage.append("- Lecture Time is required\n");
            hasError = true;
        }

        if (instructor == null) {
            setComboBoxError(courseInstructorCombo);
            errorMessage.append("- Instructor is required\n");
            hasError = true;
        }

        if (hasError) {
            showError(errorMessage.toString());
            return;
        }

        try {
            Course course = new Course(code, name, level, major, lectureTime, null, null, instructor.getId());

            boolean success = courseService.addCourse(course);
            if (success) {
                System.out.println("Course created successfully!");
                showInfo("Course created successfully!");

                // Clear all fields and reload
                courseCodeField.clear();
                courseNameField.clear();
                courseLevelField.clear();
                courseMajorCombo.getSelectionModel().clearSelection();
                courseLectureTimeField.clear();
                courseInstructorCombo.getSelectionModel().clearSelection();

                loadAllCourses();
                loadInstructorsIntoCombo();
            }

        } catch (Exception e) {
            System.out.println("Error creating course: " + e.getMessage());
            showError("Error creating course: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse() {
        Course selected = coursesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a course to delete!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Course");
        confirm.setContentText("Are you sure you want to delete: " + selected.getCourseName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                courseService.deleteCourse(selected.getCode());
                showInfo("Course deleted successfully!");
                loadAllCourses();
            } catch (Exception e) {
                showError("Error deleting course: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadAllUsers() {
        try {
            ObservableList<User> allUsers = FXCollections.observableArrayList();

            List<Admin> admins = adminService.getAllAdmins();
            List<Instructor> instructors = instructorService.getAllInstructors();
            List<Student> students = studentService.getAllStudents();

            allUsers.addAll(admins);
            allUsers.addAll(instructors);
            allUsers.addAll(students);

            usersTable.setItems(allUsers);
            System.out.println("Loaded " + allUsers.size() + " users");
        } catch (Exception e) {
            showError("Error loading users: " + e.getMessage());
        }
    }


    @FXML
    private void handleCreateUser() {
        String name = userNameField.getText().trim();
        String email = userEmailField.getText().trim();
        String password = userPasswordField.getText().trim();
        String userType = userTypeCombo.getValue();

        userNameField.setStyle("");
        userEmailField.setStyle("");
        userPasswordField.setStyle("");
        studentLevelField.setStyle("");
        studentMajorCombo.setStyle("");
        studentDepartmentCombo.setStyle("");
        instructorDepartmentCombo.setStyle("");

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        if (name.isEmpty()) {
            setFieldError(userNameField, "Name is required");
            errorMessage.append("- Name is required\n");
            hasError = true;
        } else if (name.length() < 2) {
            setFieldError(userNameField, "Name must be at least 2 characters");
            errorMessage.append("- Name must be at least 2 characters\n");
            hasError = true;
        }

        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            setFieldError(userEmailField, emailError);
            errorMessage.append("- ").append(emailError).append("\n");
            hasError = true;
        }

        String passwordError = ValidationUtil.validatePassword(password);
        if (passwordError != null) {
            setFieldError(userPasswordField, passwordError);
            errorMessage.append("- ").append(passwordError).append("\n");
            hasError = true;
        }

        switch (userType) {
            case "Student":
                String levelStr = studentLevelField.getText().trim();
                String studentMajor = studentMajorCombo.getValue();
                Department studentDept = studentDepartmentCombo.getValue();

                if (levelStr.isEmpty()) {
                    setFieldError(studentLevelField, "Level is required");
                    errorMessage.append("- Student Level is required\n");
                    hasError = true;
                } else {
                    try {
                        int level = Integer.parseInt(levelStr);
                        if (level < 1 || level > 4) {
                            setFieldError(studentLevelField, "Level must be between 1 and 4");
                            errorMessage.append("- Student Level must be between 1 and 4\n");
                            hasError = true;
                        }
                    } catch (NumberFormatException e) {
                        setFieldError(studentLevelField, "Level must be a valid number");
                        errorMessage.append("- Student Level must be a valid number (1-4)\n");
                        hasError = true;
                    }
                }

                if (studentMajor == null || studentMajor.isEmpty()) {
                    setComboBoxError(studentMajorCombo);
                    errorMessage.append("- Student Major is required\n");
                    hasError = true;
                }

                if (studentDept == null) {
                    setComboBoxError(studentDepartmentCombo);
                    errorMessage.append("- Student Department is required\n");
                    hasError = true;
                }
                break;

            case "Instructor":
                Department instructorDept = instructorDepartmentCombo.getValue();
                if (instructorDept == null) {
                    setComboBoxError(instructorDepartmentCombo);
                    errorMessage.append("- Instructor Department is required\n");
                    hasError = true;
                }
                break;
        }

        if (hasError) {
            if (passwordError != null) {
                errorMessage.append("\n").append(ValidationUtil.getPasswordRequirements());
            }
            showError(errorMessage.toString());
            return;
        }

        try {
            String hashedPassword = PasswordUtil.hashPassword(password);

            switch (userType) {
                case "Admin":
                    Admin admin = new Admin(0, name, email, hashedPassword, Role.ADMIN);
                    adminService.addAdmin(admin);
                    showInfo("Admin created successfully!");
                    break;

                case "Instructor":
                    Department instructorDept = instructorDepartmentCombo.getValue();
                    Instructor instructor = new Instructor(0, name, email, hashedPassword, Role.INSTRUCTOR, instructorDept);
                    instructorService.addInstructor(instructor);
                    showInfo("Instructor created successfully!");

                    loadInstructorsIntoCombo();
                    break;

                case "Student":
                    String levelStr = studentLevelField.getText().trim();
                    String studentMajor = studentMajorCombo.getValue();
                    Department studentDept = studentDepartmentCombo.getValue();

                    int level = Integer.parseInt(levelStr);

                    Student student = new Student(0, name, email, hashedPassword, Role.STUDENT,
                                                 level, studentMajor, null, 0, studentDept, 0.0);
                    studentService.addStudent(student);
                    showInfo("Student created successfully!");
                    break;
            }

            userNameField.clear();
            userEmailField.clear();
            userPasswordField.clear();
            studentLevelField.clear();
            studentMajorCombo.getSelectionModel().clearSelection();
            studentDepartmentCombo.getSelectionModel().clearSelection();
            instructorDepartmentCombo.getSelectionModel().clearSelection();

            loadAllUsers();

        } catch (Exception e) {
            showError("Error creating user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a user to delete!");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete User");
        confirm.setContentText("Are you sure you want to delete: " + selected.getName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Role role = selected.getRole();
                switch (role) {
                    case ADMIN:
                        adminService.deleteAdmin(selected.getId());
                        break;
                    case INSTRUCTOR:
                        instructorService.deleteInstructor(selected.getId());
                        break;
                    case STUDENT:
                        studentService.deleteStudent(selected.getId());
                        break;
                }
                showInfo("User deleted successfully!");
                loadAllUsers();
            } catch (Exception e) {
                showError("Error deleting user: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadAllStudents() {
        try {
            List<Student> students = studentService.getAllStudents();
            ObservableList<Student> studentsList = FXCollections.observableArrayList(students);
            studentsTable.setItems(studentsList);
            System.out.println("Loaded " + students.size() + " students");
        } catch (Exception e) {
            showError("Error loading students: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewStudentDetails() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student to view details!");
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/view/student_details.fxml")
            );
            javafx.scene.Parent root = loader.load();

            StudentDetailsController controller = loader.getController();

            javafx.stage.Stage currentStage = (javafx.stage.Stage) welcomeLabel.getScene().getWindow();

            controller.setStudent(selected, currentStage);

            javafx.stage.Stage detailsStage = new javafx.stage.Stage();
            detailsStage.setTitle("Student Details - " + selected.getName());
            detailsStage.setScene(new javafx.scene.Scene(root));
            detailsStage.setResizable(true);
            detailsStage.setMinWidth(1000);
            detailsStage.setMinHeight(750);

            currentStage.hide();

            detailsStage.show();

        } catch (Exception e) {
            showError("Error opening student details: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStudentLevel() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student!");
            return;
        }

        try {
            int currentLevel = selected.getLevel();
            boolean hasPaid = paymentService.hasUserPaidForLevel(selected.getId(), currentLevel);

            if (!hasPaid) {
                double levelFee = paymentService.calculateLevelFee(currentLevel);

                Alert warningAlert = new Alert(Alert.AlertType.WARNING);
                warningAlert.setTitle("Payment Required");
                warningAlert.setHeaderText("⚠️ Student Must Pay Current Level Fees First");
                warningAlert.setContentText(
                    "Student: " + selected.getName() + " (ID: " + selected.getId() + ")\n" +
                    "Current Level: " + currentLevel + "\n" +
                    "Required Fee: " + levelFee + " EGP\n\n" +
                    "The student must pay their current level fees before you can update their level.\n" +
                    "Please ensure the student completes payment first."
                );
                warningAlert.showAndWait();
                return;
            }
        } catch (Exception e) {
            showError("Error checking payment status: " + e.getMessage());
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>("2", "1", "2", "3", "4");
        dialog.setTitle("Update Student Level");
        dialog.setHeaderText("Update level for: " + selected.getName());
        dialog.setContentText("Choose level (1=Freshman, 2=Sophomore, 3=Junior, 4=Senior):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int newLevel = Integer.parseInt(result.get());
                adminService.updateStudentLevel(selected.getId(), newLevel);
                showInfo("Student level updated successfully!");
                loadAllStudents();
            } catch (Exception e) {
                showError("Error updating student: " + e.getMessage());
            }
        }
    }

    @FXML
    private void loadAllInstructors() {
        try {
            List<Instructor> instructors = instructorService.getAllInstructors();
            ObservableList<Instructor> instructorsList = FXCollections.observableArrayList(instructors);
            instructorsTable.setItems(instructorsList);
            System.out.println("Loaded " + instructors.size() + " instructors");
        } catch (Exception e) {
            showError("Error loading instructors: " + e.getMessage());
        }
    }

    // ==================== REVENUE TAB ====================

    private void setupPaymentsTable() {
        paymentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        paymentUserIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        paymentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        paymentMethodColumn.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        paymentTransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactionId"));

        paymentUserNameColumn.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            try {
                Student student = studentService.getStudentById(payment.getUserId());
                if (student != null) {
                    return new javafx.beans.property.SimpleStringProperty(student.getName());
                }
            } catch (Exception e) {
                // Ignore
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });

        paymentDateColumn.setCellValueFactory(cellData -> {
            Payment payment = cellData.getValue();
            if (payment.getCreatedAt() != null) {
                java.time.format.DateTimeFormatter formatter =
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
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

    private void setupPaymentFilters() {
        paymentStatusFilterCombo.getItems().addAll("All", "SUCCESS", "FAILED", "PENDING");
        paymentStatusFilterCombo.setValue("All");

        paymentLevelFilterCombo.getItems().addAll("All", "1", "2", "3", "4");
        paymentLevelFilterCombo.setValue("All");
    }

    @FXML
    private void loadAllPayments() {
        try {
            List<Payment> payments = paymentService.getAllPayments();
            ObservableList<Payment> paymentsList = FXCollections.observableArrayList(payments);
            paymentsTable.setItems(paymentsList);
            System.out.println("Loaded " + payments.size() + " payments");

            updateRevenueStatistics();
        } catch (Exception e) {
            showError("Error loading payments: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilterPayments() {
        try {
            String statusFilter = paymentStatusFilterCombo.getValue();
            String levelFilter = paymentLevelFilterCombo.getValue();

            List<Payment> payments = paymentService.getAllPayments();

            if (!"All".equals(statusFilter)) {
                payments = payments.stream()
                    .filter(p -> statusFilter.equals(p.getStatus()))
                    .collect(java.util.stream.Collectors.toList());
            }

            if (!"All".equals(levelFilter)) {
                int level = Integer.parseInt(levelFilter);
                payments = payments.stream()
                    .filter(p -> p.getLevel() == level)
                    .collect(java.util.stream.Collectors.toList());
            }

            ObservableList<Payment> paymentsList = FXCollections.observableArrayList(payments);
            paymentsTable.setItems(paymentsList);
            System.out.println("Filtered to " + payments.size() + " payments");

        } catch (Exception e) {
            showError("Error filtering payments: " + e.getMessage());
        }
    }

    private void updateRevenueStatistics() {
        try {
            double totalRevenue = paymentService.getTotalRevenue();
            totalRevenueLabel.setText(String.format("%.2f EGP", totalRevenue));

            List<Payment> allPayments = paymentService.getAllPayments();
            totalPaymentsLabel.setText(allPayments.size() + " payments");

            level1RevenueLabel.setText(String.format("%.2f EGP",
                paymentService.getTotalRevenueByLevel(1)));
            level2RevenueLabel.setText(String.format("%.2f EGP",
                paymentService.getTotalRevenueByLevel(2)));
            level3RevenueLabel.setText(String.format("%.2f EGP",
                paymentService.getTotalRevenueByLevel(3)));
            level4RevenueLabel.setText(String.format("%.2f EGP",
                paymentService.getTotalRevenueByLevel(4)));

            long successCount = allPayments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .count();
            long failedCount = allPayments.stream()
                .filter(p -> "FAILED".equals(p.getStatus()))
                .count();
            long pendingCount = allPayments.stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .count();

            successfulPaymentsLabel.setText(String.valueOf(successCount));
            failedPaymentsLabel.setText(String.valueOf(failedCount));
            pendingPaymentsLabel.setText(String.valueOf(pendingCount));

            System.out.println("Revenue statistics updated");

        } catch (Exception e) {
            System.err.println("Error updating revenue statistics: " + e.getMessage());
        }
    }

    private void setFieldError(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    private void setFieldError(TextField field, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(field, tooltip);
    }

    private void setComboBoxError(ComboBox<?> comboBox) {
        comboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip("This field is required");
        Tooltip.install(comboBox, tooltip);
    }

    private void clearFieldErrors(TextField... fields) {
        for (TextField field : fields) {
            field.setStyle("");
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

            profileController.setUser(currentAdmin, currentStage);

            javafx.stage.Stage profileStage = new javafx.stage.Stage();
            profileStage.setTitle("My Profile - " + currentAdmin.getName());
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
            System.out.println("Refreshing admin dashboard...");

            if (currentAdmin != null) {
                welcomeLabel.setText("Welcome, " + currentAdmin.getName() + "!");
                userInfoLabel.setText("Role: Admin | ID: " + currentAdmin.getId() + " | Email: " + currentAdmin.getEmail());
            }

            loadInstructorsIntoCombo();

            loadAllCourses();
            loadAllUsers();
            loadAllStudents();
            loadAllInstructors();
            loadAllPayments();
            updateRevenueStatistics();

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
            System.out.println("Logging out...");
            App.showLoginScreen();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validation Error");
        alert.setHeaderText("Invalid Input");
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

