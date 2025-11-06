package com.ums.system.dao;

import com.ums.system.model.Instructor;
import com.ums.system.model.Department;
import com.ums.system.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAOImpl implements UserDAO<Instructor> {

    private final Connection connection;

    public InstructorDAOImpl(Connection connection) {
        this.connection = connection;
    }

    // ----------------- Helper Methods -----------------

    public boolean instructorExistsById(int id) {
        String sql = "SELECT COUNT(*) FROM instructors WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ----------------- CRUD Operations -----------------

    @Override
    public void insert(Instructor i) {
        if (emailExists(i.getEmail())) {
            System.out.println("⚠️ Instructor with email " + i.getEmail() + " already exists.");
            return;
        }

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

            try (ResultSet rs = psUser.getGeneratedKeys()) {
                if (rs.next()) {
                    int userId = rs.getInt(1);
                    psInstructor.setInt(1, userId);
                    psInstructor.setString(2, i.getDepartment().toString());
                    psInstructor.executeUpdate();
                } else {
                    System.out.println("❌ Failed to retrieve generated user ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Instructor i) {
        if (!instructorExistsById(i.getId())) {
            System.out.println("⚠️ Instructor with ID " + i.getId() + " does not exist.");
            return;
        }

        String userSql = "UPDATE users SET name=?, email=?, password=? WHERE id=? AND role='INSTRUCTOR'";
        String instructorSql = "UPDATE instructors SET department=? WHERE user_id=?";

        try (
                PreparedStatement psUser = connection.prepareStatement(userSql);
                PreparedStatement psInstructor = connection.prepareStatement(instructorSql)
        ) {
            psUser.setString(1, i.getName());
            psUser.setString(2, i.getEmail());
            psUser.setString(3, i.getPassword());
            psUser.setInt(4, i.getId());
            psUser.executeUpdate();

            psInstructor.setString(1, i.getDepartment().toString());
            psInstructor.setInt(2, i.getId());
            psInstructor.executeUpdate();

            System.out.println("✅ Instructor updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        if (!instructorExistsById(id)) {
            System.out.println("⚠️ Instructor with ID " + id + " does not exist.");
            return;
        }

        String deleteInstructor = "DELETE FROM instructors WHERE user_id=?";
        String deleteUser = "DELETE FROM users WHERE id=? AND role='INSTRUCTOR'";

        try {
            connection.setAutoCommit(false);

            try (
                    PreparedStatement psInstructor = connection.prepareStatement(deleteInstructor);
                    PreparedStatement psUser = connection.prepareStatement(deleteUser)
            ) {
                psInstructor.setInt(1, id);
                psInstructor.executeUpdate();

                psUser.setInt(1, id);
                psUser.executeUpdate();

                connection.commit();
                System.out.println("✅ Instructor deleted successfully.");
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
    public Instructor getById(int id) {
        String sql = """
            SELECT u.*, i.department
            FROM users u
            JOIN instructors i ON u.id = i.user_id
            WHERE u.id = ? AND u.role = 'INSTRUCTOR'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            Role.valueOf(rs.getString("role")),
                            Department.valueOf(rs.getString("department"))
                    );
                }
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
            WHERE u.role = 'INSTRUCTOR'
        """;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Instructor(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password"),
                        Role.valueOf(rs.getString("role")),
                        Department.valueOf(rs.getString("department"))
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Instructor getByEmail(String email) {
        String sql = """
            SELECT u.*, i.department
            FROM users u
            JOIN instructors i ON u.id = i.user_id
            WHERE u.email = ? AND u.role = 'INSTRUCTOR'
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Instructor(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password"),
                            Role.valueOf(rs.getString("role")),
                            Department.valueOf(rs.getString("department"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
