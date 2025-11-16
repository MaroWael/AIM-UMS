package com.ums.system.service;

import com.ums.system.dao.StudentDAOImpl;
import com.ums.system.model.Student;
import com.ums.system.utils.ValidationUtil;
import com.ums.system.utils.PasswordUtil;

import java.sql.Connection;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAOImpl studentDAO;

    public StudentServiceImpl(Connection connection) {
        this.studentDAO = new StudentDAOImpl(connection);
    }

    @Override
    public boolean addStudent(Student student) {
        String emailError = ValidationUtil.validateEmail(student.getEmail());
        if (emailError != null) {
            System.out.println(emailError);
            return false;
        }

        String passwordError = ValidationUtil.validatePassword(student.getPassword());
        if (passwordError != null) {
            System.out.println("Password validation failed:");
            System.out.println("  " + passwordError);
            System.out.println("\n" + ValidationUtil.getPasswordRequirements());
            return false;
        }

        if (student.getLevel() < 1 || student.getLevel() > 4) {
            System.out.println("Invalid level! Level must be between 1 and 4.");
            return false;
        }

        Student existing = studentDAO.getByEmail(student.getEmail());
        if (existing != null) {
            System.out.println("Student with email " + student.getEmail() + " already exists.");
            return false;
        }

        String hashedPassword = PasswordUtil.hashPassword(student.getPassword());
        student.setPassword(hashedPassword);

        studentDAO.insert(student);
        Student created = studentDAO.getByEmail(student.getEmail());
        return created != null;
    }

    @Override
    public void updateStudent(Student student) {
        studentDAO.update(student);
    }

    @Override
    public void deleteStudent(int id) {
        studentDAO.delete(id);
    }

    @Override
    public Student getStudentById(int id) {
        return studentDAO.getById(id);
    }

    @Override
    public Student getStudentByEmail(String email) {
        return studentDAO.getByEmail(email);
    }

    @Override
    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }

    @Override
    public void updateStudentGrade(int studentId, double grade) {
        studentDAO.updateGrade(studentId, grade);
    }
}
