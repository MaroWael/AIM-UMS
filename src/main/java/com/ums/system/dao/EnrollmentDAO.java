package com.ums.system.dao;

import com.ums.system.model.Course;
import com.ums.system.model.Student;
import java.util.List;

public interface EnrollmentDAO {
    void enrollStudentInCourse(int studentId, String courseCode);
    void removeStudentFromCourse(int studentId, String courseCode);
    List<Course> getCoursesByStudentId(int studentId);
    List<Student> getStudentsByCourseCode(String courseCode);
}
