package com.ums.system.dao;

import com.ums.system.model.Instructor;
import com.ums.system.model.Department;
import com.ums.system.model.Role;

import java.sql.*;
import java.util.*;

public class InstructorDAOImpl implements UserDAO<Instructor> {
    private final Connection connection;

    public InstructorDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Instructor i) {
        String userSql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String instructorSql = "INSERT INTO instructors (user_id, department) VALUES (?, ?)";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psInstructor = connection.prepareStatement(instructorSql)
        ) {

            psUser.setString(1, i.getName());
            psUser.setString(2, i.getEmail());
            psUser.setString(3, i.getPassword());
            psUser.setString(4, i.getRole().toString());
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            psInstructor.setInt(1, userId);
            psInstructor.setString(2, i.getDepartment().toString());
            psInstructor.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Instructor i) {
        String userSql = "UPDATE users SET name=?, email=?, password=?, role=? WHERE id=?";
        String instructorSql = "UPDATE instructors SET department=? WHERE user_id=?";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql);
                PreparedStatement psInstructor = connection.prepareStatement(instructorSql)
        ) {
            psUser.setString(1, i.getName());
            psUser.setString(2, i.getEmail());
            psUser.setString(3, i.getPassword());
            psUser.setString(4, i.getRole().toString());
            psUser.setInt(5, i.getId());
            psUser.executeUpdate();

            psInstructor.setString(1, i.getDepartment().toString());
            psInstructor.setInt(2, i.getId());
            psInstructor.executeUpdate();

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
    public Instructor getById(int id) {
        String sql = """
            SELECT u.*, i.department
            FROM users u
            JOIN instructors i ON u.id = i.user_id
            WHERE u.id = ?
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Instructor(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        Department.valueOf(rs.getString("department")),
                        null
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Instructor> getAll() {
        List<Instructor> list = new ArrayList<>();
        String sql = """
            SELECT u.*, i.department
            FROM users u
            JOIN instructors i ON u.id = i.user_id
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Instructor(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        Department.valueOf(rs.getString("department")),
                        null
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
