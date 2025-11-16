package com.ums.system.service;

import com.ums.system.dao.AdminDAOImpl;
import com.ums.system.dao.StudentDAOImpl;
import com.ums.system.model.Admin;
import com.ums.system.model.Student;
import com.ums.system.utils.ValidationUtil;
import com.ums.system.utils.PasswordUtil;

import java.sql.Connection;
import java.util.List;

public class AdminServiceImpl implements AdminService {
    private final AdminDAOImpl adminDAO;
    private final StudentDAOImpl studentDAO;

    public AdminServiceImpl(Connection connection) {
        this.adminDAO = new AdminDAOImpl(connection);
        this.studentDAO = new StudentDAOImpl(connection);
    }

    @Override
    public boolean addAdmin(Admin admin) {
        String emailError = ValidationUtil.validateEmail(admin.getEmail());
        if (emailError != null) {
            System.out.println(emailError);
            return false;
        }

        String passwordError = ValidationUtil.validatePassword(admin.getPassword());
        if (passwordError != null) {
            System.out.println("Password validation failed:");
            System.out.println("  " + passwordError);
            System.out.println("\n" + ValidationUtil.getPasswordRequirements());
            return false;
        }

        Admin existing = adminDAO.getByEmail(admin.getEmail());
        if (existing != null) {
            System.out.println("Admin with email " + admin.getEmail() + " already exists.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(admin.getPassword());
        admin.setPassword(hashedPassword);

        adminDAO.insert(admin);
        Admin created = adminDAO.getByEmail(admin.getEmail());
        return created != null;
    }

    @Override
    public void updateAdmin(Admin admin) {
        adminDAO.update(admin);
    }

    @Override
    public void deleteAdmin(int id) {
        adminDAO.delete(id);
    }

    @Override
    public Admin getAdminById(int id) {
        return adminDAO.getById(id);
    }

    @Override
    public Admin getAdminByEmail(String email) {
        return adminDAO.getByEmail(email);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminDAO.getAll();
    }

    @Override
    public boolean updateStudentLevel(int studentId, int newLevel) {
        Student student = studentDAO.getById(studentId);
        if (student == null) {
            System.out.println("Student with ID " + studentId + " not found.");
            return false;
        }

        if (newLevel < 1 || newLevel > 4) {
            System.out.println("Invalid level! Level must be between 1 and 4.");
            return false;
        }

        student.setLevel(newLevel);
        studentDAO.update(student);

        System.out.println("Student level updated successfully!");
        System.out.println("   Student: " + student.getName());
        System.out.println("   New Level: " + newLevel);
        return true;
    }
}
