package com.ums.system.dao;

import com.ums.system.model.Student;
import com.ums.system.model.Course;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAOImpl implements EnrollmentDAO {

    private final Connection connection;
    private final StudentDAOImpl studentDAO;
    private final CourseDAOImpl courseDAO;

    public EnrollmentDAOImpl(Connection connection) {
        this.connection = connection;
        this.studentDAO = new StudentDAOImpl(connection);
        this.courseDAO = new CourseDAOImpl(connection);
    }

    @Override
    public void enrollStudentInCourse(int studentId, String courseCode) {
        try {

            if (!courseDAO.existsByCode(courseCode)) {
                System.err.println("Cannot enroll: Course with code " + courseCode + " does not exist.");
                return;
            }

            Student student = studentDAO.getById(studentId);
            Course course = courseDAO.getByCode(courseCode);


            String studentLevel = String.valueOf(student.getLevel());
            if (!studentLevel.equals(course.getLevel())) {
                System.err.println("Cannot enroll: Course level (" + course.getLevel() +
                                 ") does not match student's level (" + studentLevel + ").");
                return;
            }

            if (!student.getMajor().equalsIgnoreCase(course.getMajor())) {
                System.err.println("Cannot enroll: Course major (" + course.getMajor() +
                                 ") does not match student's major (" + student.getMajor() + ").");
                return;
            }

            String checkSql = "SELECT COUNT(*) FROM student_courses WHERE student_id=? AND course_code=?";
            try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
                checkPs.setInt(1, studentId);
                checkPs.setString(2, courseCode);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Student is already enrolled in course " + courseCode);
                    return;
                }
            }

            String sql = "INSERT INTO student_courses (student_id, course_code) VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setString(2, courseCode);
                ps.executeUpdate();
                System.out.println("Student " + studentId + " enrolled in course " + courseCode + " successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error enrolling student " + studentId + " in course " + courseCode);
            e.printStackTrace();
        }
    }

    @Override
    public void removeStudentFromCourse(int studentId, String courseCode) {
        try {
            String checkSql = "SELECT COUNT(*) FROM student_courses WHERE student_id=? AND course_code=?";
            try (PreparedStatement checkPs = connection.prepareStatement(checkSql)) {
                checkPs.setInt(1, studentId);
                checkPs.setString(2, courseCode);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    System.err.println("Cannot remove: Student " + studentId + " is not enrolled in course " + courseCode);
                    return;
                }
            }

            String sql = "DELETE FROM student_courses WHERE student_id=? AND course_code=?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                ps.setString(2, courseCode);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error removing student " + studentId + " from course " + courseCode);
            e.printStackTrace();
        }
    }

    @Override
    public List<Course> getCoursesByStudentId(int studentId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT course_code FROM student_courses WHERE student_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String courseCode = rs.getString("course_code");
                Course course = courseDAO.getByCode(courseCode);
                courses.add(course);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching courses for student " + studentId);
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public List<Student> getStudentsByCourseCode(String courseCode) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT student_id FROM student_courses WHERE course_code=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, courseCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int studentId = rs.getInt("student_id");
                Student student = studentDAO.getById(studentId);
                if (student != null) students.add(student);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching students for course " + courseCode);
            e.printStackTrace();
        }
        return students;
    }
}
