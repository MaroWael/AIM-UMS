package com.ums.system.service;

import com.ums.system.dao.StudentDAOImpl;
import com.ums.system.model.Student;

import java.sql.Connection;
import java.util.List;

public class StudentServiceImpl implements StudentService {
    private final StudentDAOImpl studentDAO;

    public StudentServiceImpl(Connection connection) {
        this.studentDAO = new StudentDAOImpl(connection);
    }

    @Override
    public boolean addStudent(Student student) {
        // Check if student with this email already exists before insertion
        Student existing = studentDAO.getByEmail(student.getEmail());
        if (existing != null) {
            System.out.println("⚠️ Student with email " + student.getEmail() + " already exists.");
            return false; // Email already exists, creation failed
        }
        studentDAO.insert(student);
        // Verify the student was actually created
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
}
