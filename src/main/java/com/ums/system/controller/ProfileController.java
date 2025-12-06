package com.ums.system.controller;

import com.ums.system.model.*;
import com.ums.system.service.*;
import com.ums.system.util.ServiceLocator;
import com.ums.system.utils.PasswordUtil;
import com.ums.system.utils.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Optional;

/**
 * ProfileController - Handles user profile management
 * Works for all user types: Student, Instructor, Admin
 */
public class ProfileController {

    @FXML private Label roleLabel;
    @FXML private TextField userIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField roleField;

    @FXML private VBox studentFieldsBox;
    @FXML private TextField levelField;
    @FXML private TextField majorField;
    @FXML private TextField departmentField;
    @FXML private TextField gradeField;

    @FXML private VBox instructorFieldsBox;
    @FXML private TextField instructorDepartmentField;

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private StudentService studentService;
    private InstructorService instructorService;
    private AdminService adminService;

    private User currentUser;
    private Stage previousStage;

    @FXML
    public void initialize() {
        ServiceLocator serviceLocator = ServiceLocator.getInstance();
        studentService = serviceLocator.getStudentService();
        instructorService = serviceLocator.getInstructorService();
        adminService = serviceLocator.getAdminService();
    }

    public void setUser(User user, Stage previousStage) {
        this.currentUser = user;
        this.previousStage = previousStage;
        populateFields();
    }

    private void populateFields() {
        if (currentUser == null) {
            return;
        }

        userIdField.setText(String.valueOf(currentUser.getId()));
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        roleField.setText(currentUser.getRole().toString());
        roleLabel.setText("Role: " + currentUser.getRole().toString());

        if (currentUser instanceof Student) {
            Student student = (Student) currentUser;
            studentFieldsBox.setVisible(true);
            studentFieldsBox.setManaged(true);

            levelField.setText(String.valueOf(student.getLevel()));
            majorField.setText(student.getMajor());
            departmentField.setText(student.getDepartmentName() != null ? student.getDepartmentName().toString() : "N/A");
            gradeField.setText(String.format("%.2f%%", student.getGrade()));

        } else if (currentUser instanceof Instructor) {
            Instructor instructor = (Instructor) currentUser;
            instructorFieldsBox.setVisible(true);
            instructorFieldsBox.setManaged(true);

            instructorDepartmentField.setText(instructor.getDepartment() != null ?
                instructor.getDepartment().toString() : "N/A");
        }
    }

    @FXML
    private void handleSaveProfile() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();

        if (newName.isEmpty()) {
            showError("Name cannot be empty!");
            return;
        }

        if (newName.length() < 2) {
            showError("Name must be at least 2 characters!");
            return;
        }

        String emailError = ValidationUtil.validateEmail(newEmail);
        if (emailError != null) {
            showError(emailError);
            return;
        }

        try {
            currentUser.setName(newName);
            currentUser.setEmail(newEmail);

            if (currentUser instanceof Student) {
                studentService.updateStudent((Student) currentUser);
            } else if (currentUser instanceof Instructor) {
                instructorService.updateInstructor((Instructor) currentUser);
            } else if (currentUser instanceof Admin) {
                adminService.updateAdmin((Admin) currentUser);
            }

            showInfo("✅ Profile updated successfully!");

        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty()) {
            showError("Please enter your current password!");
            return;
        }

        try {
            if (!PasswordUtil.verifyPassword(currentPassword, currentUser.getPassword())) {
                showError("Current password is incorrect!");
                return;
            }
        } catch (Exception e) {
            showError("Error verifying password: " + e.getMessage());
            return;
        }

        String passwordError = ValidationUtil.validatePassword(newPassword);
        if (passwordError != null) {
            showError(passwordError + "\n\n" + ValidationUtil.getPasswordRequirements());
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("New passwords do not match!");
            return;
        }

        if (currentPassword.equals(newPassword)) {
            showError("New password must be different from current password!");
            return;
        }

        try {
            String hashedPassword = PasswordUtil.hashPassword(newPassword);

            currentUser.setPassword(hashedPassword);

            if (currentUser instanceof Student) {
                studentService.updateStudent((Student) currentUser);
            } else if (currentUser instanceof Instructor) {
                instructorService.updateInstructor((Instructor) currentUser);
            } else if (currentUser instanceof Admin) {
                adminService.updateAdmin((Admin) currentUser);
            }

            showInfo("✅ Password changed successfully!");

            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();

        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
        }
    }

    @FXML
    private void handleReset() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset Changes");
        confirm.setHeaderText("Discard Changes?");
        confirm.setContentText("Are you sure you want to discard all changes?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (currentUser instanceof Student) {
                    Student student = studentService.getStudentById(currentUser.getId());
                    if (student != null) {
                        this.currentUser = student;
                    }
                } else if (currentUser instanceof Instructor) {
                    Instructor instructor = instructorService.getInstructorById(currentUser.getId());
                    if (instructor != null) {
                        this.currentUser = instructor;
                    }
                } else if (currentUser instanceof Admin) {
                    Admin admin = adminService.getAdminById(currentUser.getId());
                    if (admin != null) {
                        this.currentUser = admin;
                    }
                }

                populateFields();

                currentPasswordField.clear();
                newPasswordField.clear();
                confirmPasswordField.clear();

                showInfo("Changes discarded. Original data restored.");

            } catch (Exception e) {
                showError("Error resetting data: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage currentStage = (Stage) nameField.getScene().getWindow();

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
        alert.setHeaderText("Profile Error");
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

