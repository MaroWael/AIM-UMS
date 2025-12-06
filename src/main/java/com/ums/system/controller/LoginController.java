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

    private AdminService adminService;
    private InstructorService instructorService;
    private StudentService studentService;

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        adminService = serviceLocator.getAdminService();
        instructorService = serviceLocator.getInstructorService();
        studentService = serviceLocator.getStudentService();

        progressIndicator.setVisible(false);

        errorLabel.setText("");
        errorLabel.setVisible(false);

        passwordField.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        clearError();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        progressIndicator.setVisible(true);
        loginButton.setDisable(true);

        new Thread(() -> {
            User loggedInUser = authenticateUser(email, password);

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

    private User authenticateUser(String email, String password) {
        try {
            Admin admin = adminService.getAdminByEmail(email);
            if (admin != null && PasswordUtil.verifyPassword(password, admin.getPassword())) {
                System.out.println("Admin login successful: " + admin.getName());
                return admin;
            }
            Instructor instructor = instructorService.getInstructorByEmail(email);
            if (instructor != null && PasswordUtil.verifyPassword(password, instructor.getPassword())) {
                System.out.println("Instructor login successful: " + instructor.getName());
                return instructor;
            }
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

    private void onLoginSuccess(User user) {
        try {
            Role role = user.getRole();
            FXMLLoader loader = null;
            String title = "";

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

            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof AdminController) {
                ((AdminController) controller).setUser((Admin) user);
            } else if (controller instanceof InstructorController) {
                ((InstructorController) controller).setUser((Instructor) user);
            } else if (controller instanceof StudentController) {
                ((StudentController) controller).setUser((Student) user);
            }

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

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        errorLabel.setVisible(true);
    }

    private void clearError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }
}

