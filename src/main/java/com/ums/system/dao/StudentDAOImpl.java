package com.ums.system.dao;

import com.ums.system.model.*;
import java.sql.*;
import java.util.*;

public class StudentDAOImpl implements UserDAO<Student> {
    private final Connection connection;

    public StudentDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Student s) {
        String userSql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String studentSql = "INSERT INTO students (user_id, level, major, grade, department) VALUES (?, ?, ?, ?, ?)";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psStudent = connection.prepareStatement(studentSql)
        ) {
            psUser.setString(1, s.getName());
            psUser.setString(2, s.getEmail());
            psUser.setString(3, s.getPassword());
            psUser.setString(4, s.getRole().toString());
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            psStudent.setInt(1, userId);
            psStudent.setInt(2, s.getLevel());
            psStudent.setString(3, s.getMajor());
            psStudent.setDouble(4, s.getGrade());
            psStudent.setString(5, s.getDepartmentName().toString());
            psStudent.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Student s) {
        String userSql = "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?";
        String studentSql = "UPDATE students SET level=?, major=?, grade=?, department=? WHERE user_id=?";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql);
                PreparedStatement psStudent = connection.prepareStatement(studentSql)
        ) {
            psUser.setString(1, s.getName());
            psUser.setString(2, s.getEmail());
            psUser.setString(3, s.getPassword());
            psUser.setString(4, s.getRole().toString());
            psUser.setInt(5, s.getId());
            psUser.executeUpdate();

            psStudent.setInt(1, s.getLevel());
            psStudent.setString(2, s.getMajor());
            psStudent.setDouble(3, s.getGrade());
            psStudent.setString(4, s.getDepartmentName().toString());
            psStudent.setInt(5, s.getId());
            psStudent.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM users WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Student getById(int id) {
        String sql = """
            SELECT u.*, s.level, s.major, s.grade, s.department
            FROM users u
            JOIN students s ON u.id = s.user_id
            WHERE u.id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Student(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("level"),
                        rs.getString("major"),
                        null,
                        0,
                        Department.valueOf(rs.getString("department")),
                        rs.getDouble("grade")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Student> getAll() {
        List<Student> list = new ArrayList<>();
        String sql = """
            SELECT u.*, s.level, s.major, s.grade, s.department
            FROM users u
            JOIN students s ON u.id = s.user_id
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Student(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        rs.getInt("level"),
                        rs.getString("major"),
                        null,
                        0,
                        Department.valueOf(rs.getString("department")),
                        rs.getDouble("grade")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
