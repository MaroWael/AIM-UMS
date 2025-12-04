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

/**
 * AdminController - Handles Admin panel functionality
 * Provides access to all admin features from CLI
 */
public class AdminController {

    @FXML private Label welcomeLabel;
    @FXML private Label userInfoLabel;

    // Courses Tab
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

    // Users Tab
    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, Integer> userIdColumn;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> userEmailColumn;
    @FXML private TableColumn<User, String> userRoleColumn;
    @FXML private ComboBox<String> userTypeCombo;
    @FXML private TextField userNameField;
    @FXML private TextField userEmailField;
    @FXML private PasswordField userPasswordField;

    // Dynamic User Creation Fields
    @FXML private GridPane studentFieldsPane;
    @FXML private TextField studentLevelField;
    @FXML private ComboBox<String> studentMajorCombo;
    @FXML private ComboBox<Department> studentDepartmentCombo;
    @FXML private HBox instructorFieldsPane;
    @FXML private ComboBox<Department> instructorDepartmentCombo;

    // Students Tab
    @FXML private TableView<Student> studentsTable;
    @FXML private TableColumn<Student, Integer> studentIdColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentEmailColumn;
    @FXML private TableColumn<Student, String> studentLevelColumn;

    // Instructors Tab
    @FXML private TableView<Instructor> instructorsTable;
    @FXML private TableColumn<Instructor, Integer> instructorIdColumn;
    @FXML private TableColumn<Instructor, String> instructorNameColumn;
    @FXML private TableColumn<Instructor, String> instructorEmailColumn;
    @FXML private TableColumn<Instructor, String> instructorDeptColumn;

    // Services
    private Admin currentAdmin;
    private CourseService courseService;
    private AdminService adminService;
    private InstructorService instructorService;
    private StudentService studentService;

    /**
     * Initialize method
     */
    @FXML
    public void initialize() {
        // Get services from ServiceLocator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        courseService = serviceLocator.getCourseService();
        adminService = serviceLocator.getAdminService();
        instructorService = serviceLocator.getInstructorService();
        studentService = serviceLocator.getStudentService();

        // Set up tables
        setupCoursesTable();
        setupUsersTable();
        setupStudentsTable();
        setupInstructorsTable();

        // Set up major dropdowns with available majors
        ObservableList<String> majors = FXCollections.observableArrayList(
            "Computer Science", "Information Systems", "Information Technology", "Artificial Intelligence"
        );
        courseMajorCombo.setItems(majors);
        studentMajorCombo.setItems(majors);

        // Set up department dropdowns
        ObservableList<Department> departments = FXCollections.observableArrayList(Department.values());
        studentDepartmentCombo.setItems(departments);
        instructorDepartmentCombo.setItems(departments);

        // Set up user type combo
        userTypeCombo.setItems(FXCollections.observableArrayList("Admin", "Instructor", "Student"));
        userTypeCombo.setValue("Admin");

        // Add listener to user type combo to show/hide dynamic fields
        userTypeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            handleUserTypeChange(newVal);
        });

        // Initially hide all dynamic fields
        studentFieldsPane.setVisible(false);
        studentFieldsPane.setManaged(false);
        instructorFieldsPane.setVisible(false);
        instructorFieldsPane.setManaged(false);
    }

    /**
     * Handle user type change - show/hide appropriate fields
     */
    private void handleUserTypeChange(String userType) {
        // Hide all dynamic fields first
        studentFieldsPane.setVisible(false);
        studentFieldsPane.setManaged(false);
        instructorFieldsPane.setVisible(false);
        instructorFieldsPane.setManaged(false);

        // Show appropriate fields based on user type
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
                // No additional fields needed
                break;
        }
    }

    /**
     * Set current admin user
     */
    public void setUser(Admin admin) {
        this.currentAdmin = admin;
        welcomeLabel.setText("Welcome, " + admin.getName() + "!");
        userInfoLabel.setText("Role: Admin | ID: " + admin.getId() + " | Email: " + admin.getEmail());

        // Load available instructors into combo box - matching CLI pattern
        loadInstructorsIntoCombo();

        // Load initial data
        loadAllCourses();
        loadAllUsers();
        loadAllStudents();
        loadAllInstructors();
    }

    /**
     * Load instructors into combo box for course creation - matches CLI createCourse()
     */
    private void loadInstructorsIntoCombo() {
        try {
            List<Instructor> instructors = instructorService.getAllInstructors();

            if (instructors.isEmpty()) {
                System.out.println("No instructors available. Please create an instructor first.");
            }

            // Create a custom string converter to display instructor info
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

    /**
     * Setup Courses Table - with instructor name lookup
     */
    private void setupCoursesTable() {
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        courseNameColumn.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        courseLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
        courseMajorColumn.setCellValueFactory(new PropertyValueFactory<>("major"));
        courseLectureTimeColumn.setCellValueFactory(new PropertyValueFactory<>("lectureTime"));

        // Custom cell value factory for instructor name instead of ID
        courseInstructorColumn.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            int instructorId = course.getInstructorId();

            // Fetch instructor name
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

    /**
     * Setup Users Table
     */
    private void setupUsersTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        userEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        userRoleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
    }

    /**
     * Setup Students Table
     */
    private void setupStudentsTable() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        studentEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        studentLevelColumn.setCellValueFactory(new PropertyValueFactory<>("level"));
    }

    /**
     * Setup Instructors Table
     */
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

    /**
     * Handle Create Course - matches CLI createCourse() method with ComboBox for major
     */
    @FXML
    private void handleCreateCourse() {
        String code = courseCodeField.getText().trim();
        String name = courseNameField.getText().trim();
        String level = courseLevelField.getText().trim();
        String major = courseMajorCombo.getValue();
        String lectureTime = courseLectureTimeField.getText().trim();
        Instructor instructor = courseInstructorCombo.getValue();

        // Clear previous error styles
        clearFieldErrors(courseCodeField, courseNameField, courseLevelField, courseLectureTimeField);
        courseMajorCombo.setStyle("");
        courseInstructorCombo.setStyle("");

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        // Validate course code
        if (code.isEmpty()) {
            setFieldError(courseCodeField, "Course Code is required");
            errorMessage.append("- Course Code is required\n");
            hasError = true;
        } else if (code.length() < 3) {
            setFieldError(courseCodeField, "Course Code must be at least 3 characters");
            errorMessage.append("- Course Code must be at least 3 characters\n");
            hasError = true;
        }

        // Validate course name
        if (name.isEmpty()) {
            setFieldError(courseNameField, "Course Name is required");
            errorMessage.append("- Course Name is required\n");
            hasError = true;
        } else if (name.length() < 3) {
            setFieldError(courseNameField, "Course Name must be at least 3 characters");
            errorMessage.append("- Course Name must be at least 3 characters\n");
            hasError = true;
        }

        // Validate level
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

        // Validate major
        if (major == null || major.isEmpty()) {
            setComboBoxError(courseMajorCombo);
            errorMessage.append("- Major is required\n");
            hasError = true;
        }

        // Validate lecture time
        if (lectureTime.isEmpty()) {
            setFieldError(courseLectureTimeField, "Lecture Time is required");
            errorMessage.append("- Lecture Time is required\n");
            hasError = true;
        }

        // Validate instructor
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
            // Create course with constructor matching CLI pattern
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

    // ==================== USERS TAB ACTIONS ====================

    @FXML
    private void loadAllUsers() {
        try {
            ObservableList<User> allUsers = FXCollections.observableArrayList();

            // Load all types of users
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

    /**
     * Handle Create User - with dynamic fields based on user type
     */
    @FXML
    private void handleCreateUser() {
        String name = userNameField.getText().trim();
        String email = userEmailField.getText().trim();
        String password = userPasswordField.getText().trim();
        String userType = userTypeCombo.getValue();

        // Clear previous error styles
        userNameField.setStyle("");
        userEmailField.setStyle("");
        userPasswordField.setStyle("");
        studentLevelField.setStyle("");
        studentMajorCombo.setStyle("");
        studentDepartmentCombo.setStyle("");
        instructorDepartmentCombo.setStyle("");

        boolean hasError = false;
        StringBuilder errorMessage = new StringBuilder("Please fix the following errors:\n");

        // Validate name
        if (name.isEmpty()) {
            setFieldError(userNameField, "Name is required");
            errorMessage.append("- Name is required\n");
            hasError = true;
        } else if (name.length() < 2) {
            setFieldError(userNameField, "Name must be at least 2 characters");
            errorMessage.append("- Name must be at least 2 characters\n");
            hasError = true;
        }

        // Validate email using ValidationUtil
        String emailError = ValidationUtil.validateEmail(email);
        if (emailError != null) {
            setFieldError(userEmailField, emailError);
            errorMessage.append("- ").append(emailError).append("\n");
            hasError = true;
        }

        // Validate password using ValidationUtil
        String passwordError = ValidationUtil.validatePassword(password);
        if (passwordError != null) {
            setFieldError(userPasswordField, passwordError);
            errorMessage.append("- ").append(passwordError).append("\n");
            hasError = true;
        }

        // Validate user type specific fields
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
            // Add password requirements to error message if password validation failed
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
                    // Admin: Only common fields needed
                    Admin admin = new Admin(0, name, email, hashedPassword, Role.ADMIN);
                    adminService.addAdmin(admin);
                    showInfo("Admin created successfully!");
                    break;

                case "Instructor":
                    // Instructor: Need department
                    Department instructorDept = instructorDepartmentCombo.getValue();
                    Instructor instructor = new Instructor(0, name, email, hashedPassword, Role.INSTRUCTOR, instructorDept);
                    instructorService.addInstructor(instructor);
                    showInfo("Instructor created successfully!");

                    // Refresh instructor combo in courses tab
                    loadInstructorsIntoCombo();
                    break;

                case "Student":
                    // Student: Need level, major, and department
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

            // Clear all fields and reload
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

    // ==================== STUDENTS TAB ACTIONS ====================

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
    private void handleUpdateStudentLevel() {
        Student selected = studentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a student!");
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

    // ==================== INSTRUCTORS TAB ACTIONS ====================

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

    // ==================== UTILITY METHODS ====================

    /**
     * Set visual error indicator on a text field
     */
    private void setFieldError(TextField field) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
    }

    /**
     * Set visual error indicator on a text field with custom message
     */
    private void setFieldError(TextField field, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip(message);
        Tooltip.install(field, tooltip);
    }

    /**
     * Set visual error indicator on a ComboBox
     */
    private void setComboBoxError(ComboBox<?> comboBox) {
        comboBox.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip("This field is required");
        Tooltip.install(comboBox, tooltip);
    }

    /**
     * Clear error styles from multiple fields
     */
    private void clearFieldErrors(TextField... fields) {
        for (TextField field : fields) {
            field.setStyle("");
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

