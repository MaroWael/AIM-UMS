package com.ums.system.dao;

import com.ums.system.model.Admin;
import com.ums.system.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAOImpl implements UserDAO<Admin> {

    private final Connection connection;

    public AdminDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void insert(Admin admin) {
        String userSql = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String adminSql = "INSERT INTO admins (user_id) VALUES (?)";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psAdmin = connection.prepareStatement(adminSql)
        ) {

            psUser.setString(1, admin.getName());
            psUser.setString(2, admin.getEmail());
            psUser.setString(3, admin.getPassword());
            psUser.setString(4, admin.getRole().toString());
            psUser.executeUpdate();

            ResultSet rs = psUser.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

            psAdmin.setInt(1, userId);
            psAdmin.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Admin admin) {
        String sql = "UPDATE users SET name=?, email=?, password=? WHERE id=? AND role='ADMIN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPassword());
            ps.setInt(4, admin.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=? AND role='ADMIN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Admin getById(int id) {
        String sql = """
            SELECT u.*
            FROM users u
            JOIN admins a ON u.id = a.user_id
            WHERE u.id = ? AND u.role = 'ADMIN'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Admin> getAll() {
        List<Admin> admins = new ArrayList<>();
        String sql = """
            SELECT u.*
            FROM users u
            JOIN admins a ON u.id = a.user_id
            WHERE u.role = 'ADMIN'
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(new Admin(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}