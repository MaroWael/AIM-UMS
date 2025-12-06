package com.ums.system.controller;

import com.ums.system.App;
import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.utils.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * LoginController - Handles login screen logic
 * Authenticates users and routes them to appropriate panels based on role
 */
public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label errorLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    // Services from ServiceLocator
    private AdminService adminService;
    private InstructorService instructorService;
    private StudentService studentService;

    /**
     * Initialize method - called after FXML is loaded
     */
    @FXML
    public void initialize() {
        // Get services from ServiceLocator
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        adminService = serviceLocator.getAdminService();
        instructorService = serviceLocator.getInstructorService();
        studentService = serviceLocator.getStudentService();

        // Hide progress indicator initially
        progressIndicator.setVisible(false);

        // Hide and clear error label
        errorLabel.setText("");
        errorLabel.setVisible(false);

        // Set up Enter key handler
        passwordField.setOnAction(event -> handleLogin());
    }

    /**
     * Handle login button click
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Clear previous error
        clearError();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        // Show loading
        progressIndicator.setVisible(true);
        loginButton.setDisable(true);

        // Perform login in background thread
        new Thread(() -> {
            User loggedInUser = authenticateUser(email, password);

            // Update UI on JavaFX thread
            javafx.application.Platform.runLater(() -> {
                progressIndicator.setVisible(false);
                loginButton.setDisable(false);

                if (loggedInUser != null) {
                    onLoginSuccess(loggedInUser);
                } else {
                    showError("Invalid email or password!");
                }
            });
        }).start();
    }

    /**
     * Authenticate user - same logic as CLI Main.java
     */
    private User authenticateUser(String email, String password) {
        try {
            // Try Admin login
            Admin admin = adminService.getAdminByEmail(email);
            if (admin != null && PasswordUtil.verifyPassword(password, admin.getPassword())) {
                System.out.println("Admin login successful: " + admin.getName());
                return admin;
            }

            // Try Instructor login
            Instructor instructor = instructorService.getInstructorByEmail(email);
            if (instructor != null && PasswordUtil.verifyPassword(password, instructor.getPassword())) {
                System.out.println("Instructor login successful: " + instructor.getName());
                return instructor;
            }

            // Try Student login
            Student student = studentService.getStudentByEmail(email);
            if (student != null && PasswordUtil.verifyPassword(password, student.getPassword())) {
                System.out.println("Student login successful: " + student.getName());
                return student;
            }

        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Handle successful login - route to appropriate panel
     */
    private void onLoginSuccess(User user) {
        try {
            Role role = user.getRole();
            FXMLLoader loader = null;
            String title = "";

            // Load appropriate FXML based on role
            switch (role) {
                case ADMIN:
                    loader = new FXMLLoader(getClass().getResource("/view/admin.fxml"));
                    title = "UMS - Admin Panel";
                    break;
                case INSTRUCTOR:
                    loader = new FXMLLoader(getClass().getResource("/view/instructor.fxml"));
                    title = "UMS - Instructor Panel";
                    break;
                case STUDENT:
                    loader = new FXMLLoader(getClass().getResource("/view/student.fxml"));
                    title = "UMS - Student Panel";
                    break;
                default:
                    showError("Unknown user role!");
                    return;
            }

            // Load the view
            Parent root = loader.load();

            // Get controller and pass user object
            Object controller = loader.getController();
            if (controller instanceof AdminController) {
                ((AdminController) controller).setUser((Admin) user);
            } else if (controller instanceof InstructorController) {
                ((InstructorController) controller).setUser((Instructor) user);
            } else if (controller instanceof StudentController) {
                ((StudentController) controller).setUser((Student) user);
            }

            // Switch to new scene
            Stage stage = App.getPrimaryStage();
            Scene scene = new Scene(root, 1000, 700);
            stage.setScene(scene);
            stage.setTitle(title);

            System.out.println("Loaded panel for: " + user.getName() + " (" + role + ")");

        } catch (Exception e) {
            System.err.println("Error loading user panel: " + e.getMessage());
            e.printStackTrace();
            showError("Error loading user panel: " + e.getMessage());
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        errorLabel.setVisible(true);
    }

    /**
     * Clear error message
     */
    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}

