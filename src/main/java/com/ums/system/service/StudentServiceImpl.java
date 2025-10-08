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
    public void addStudent(Student student) {
        studentDAO.insert(student);
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
    public List<Student> getAllStudents() {
        return studentDAO.getAll();
    }
}
