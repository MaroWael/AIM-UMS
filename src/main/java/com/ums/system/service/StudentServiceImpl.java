package com.ums.system.service;

import com.ums.system.dao.StudentDAOImpl;
import com.ums.system.model.Student;
import com.ums.system.utils.ValidationUtil;

import java.sql.Connection;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAOImpl studentDAO;

    public StudentServiceImpl(Connection connection) {
        this.studentDAO = new StudentDAOImpl(connection);
    }

    @Override
    public boolean addStudent(Student student) {
        if (!ValidationUtil.isValidEmail(student.getEmail())) {
            System.out.println("Invalid email format! Please provide a valid email address (e.g., user@example.com)");
            return false;
        }

        if (!ValidationUtil.isValidPassword(student.getPassword())) {
            System.out.println("Password does not meet security requirements!");
            System.out.println(ValidationUtil.getPasswordRequirements());
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
