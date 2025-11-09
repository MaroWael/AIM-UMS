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

    // ----------------- Helper Methods -----------------

    private boolean adminExistsById(int id) {
        String sql = "SELECT COUNT(*) FROM admins WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----------------- CRUD Operations -----------------

    @Override
    public void insert(Admin admin) {
        if (emailExists(admin.getEmail())) {
            System.out.println("Admin with email " + admin.getEmail() + " already exists.");
            return;
        }

        String insertUser = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, ?)";
        String insertAdmin = "INSERT INTO admins (user_id) VALUES (?)";

        try (
                PreparedStatement psUser = connection.prepareStatement(insertUser, Statement.RETURN_GENERATED_KEYS);
                PreparedStatement psAdmin = connection.prepareStatement(insertAdmin)
        ) {
            psUser.setString(1, admin.getName());
            psUser.setString(2, admin.getEmail());
            psUser.setString(3, admin.getPassword());
            psUser.setString(4, admin.getRole().toString());
            psUser.executeUpdate();

            try (ResultSet rs = psUser.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    psAdmin.setInt(1, userId);
                    psAdmin.executeUpdate();
                } else {
                    System.out.println("Failed to retrieve generated user ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Admin admin) {
        if (!adminExistsById(admin.getId())) {
            System.out.println("Admin with ID " + admin.getId() + " does not exist.");
            return;
        }

        String sql = "UPDATE users SET name=?, email=?, password=? WHERE id=? AND role='ADMIN'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, admin.getName());
            ps.setString(2, admin.getEmail());
            ps.setString(3, admin.getPassword());
            ps.setInt(4, admin.getId());
            int updated = ps.executeUpdate();

            if (updated > 0)
                System.out.println("Admin updated successfully.");
            else
                System.out.println("Failed to update admin (check ID or role).");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        if (!adminExistsById(id)) {
            System.out.println("Admin with ID " + id + " does not exist.");
            return;
        }

        String deleteAdmin = "DELETE FROM admins WHERE user_id=?";
        String deleteUser = "DELETE FROM users WHERE id=? AND role='ADMIN'";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement psAdmin = connection.prepareStatement(deleteAdmin);
                 PreparedStatement psUser = connection.prepareStatement(deleteUser)) {

                psAdmin.setInt(1, id);
                psAdmin.executeUpdate();

                psUser.setInt(1, id);
                psUser.executeUpdate();

                connection.commit();
                System.out.println("Admin deleted successfully.");
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
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
                        rs.getInt("id"),
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
                        rs.getInt("id"),
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

    public Admin getByEmail(String email) {
        String sql = """
            SELECT u.*
            FROM users u
            JOIN admins a ON u.id = a.user_id
            WHERE u.email = ? AND u.role = 'ADMIN'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Admin(
                        rs.getInt("id"),
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
}
