package com.ums.system.dao;

import com.ums.system.model.Course;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAOImpl implements CourseDAO {
    private final Connection connection;

    public CourseDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean insert(Course c) {
        String sql = "INSERT INTO courses (code, course_name, level, major, lecture_time, instructor_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getCourseName());
            ps.setString(3, c.getLevel());
            ps.setString(4, c.getMajor());
            ps.setString(5, c.getLectureTime());
            ps.setInt(6, c.getInstructorId());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(Course c) {
        if (!existsByCode(c.getCode())) {
            System.out.println(" No course found with code " + c.getCode());
            return;
        }

        String sql = "UPDATE courses SET course_name=?, level=?, major=?, lecture_time=?, instructor_id=? WHERE code=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, c.getCourseName());
            ps.setString(2, c.getLevel());
            ps.setString(3, c.getMajor());
            ps.setString(4, c.getLectureTime());
            ps.setInt(5, c.getInstructorId());
            ps.setString(6, c.getCode());
            ps.executeUpdate();
            System.out.println("Course updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(String code) {
        if (!existsByCode(code)) {
            System.out.println("No course found with code " + code);
            return;
        }

        String sql = "DELETE FROM courses WHERE code=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.executeUpdate();
            System.out.println("Course deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Course getByCode(String code) {
        String sql = "SELECT * FROM courses WHERE code=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Course(
                        rs.getString("code"),
                        rs.getString("course_name"),
                        rs.getString("level"),
                        rs.getString("major"),
                        rs.getString("lecture_time"),
                        null,
                        null,
                        rs.getInt("instructor_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Course> getAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses";
        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                courses.add(new Course(
                        rs.getString("code"),
                        rs.getString("course_name"),
                        rs.getString("level"),
                        rs.getString("major"),
                        rs.getString("lecture_time"),
                        null,
                        null,
                        rs.getInt("instructor_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public List<Course> getByInstructorId(int instructorId) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE instructor_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                courses.add(new Course(
                        rs.getString("code"),
                        rs.getString("course_name"),
                        rs.getString("level"),
                        rs.getString("major"),
                        rs.getString("lecture_time"),
                        null,
                        null,
                        rs.getInt("instructor_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return courses;
    }

    @Override
    public boolean existsByCode(String code) {
        String sql = "SELECT 1 FROM courses WHERE code=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, code);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
