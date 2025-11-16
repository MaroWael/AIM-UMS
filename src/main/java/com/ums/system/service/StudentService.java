package com.ums.system.service;

import com.ums.system.model.Student;
import java.util.List;

public interface StudentService {
    boolean addStudent(Student student);
    void updateStudent(Student student);
    void deleteStudent(int id);
    Student getStudentById(int id);
    Student getStudentByEmail(String email);
    List<Student> getAllStudents();
    void updateStudentGrade(int studentId, double grade);
}
