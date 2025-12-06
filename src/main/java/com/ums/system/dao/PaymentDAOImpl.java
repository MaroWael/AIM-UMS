package com.ums.system.dao;

import com.ums.system.model.Payment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAOImpl implements PaymentDAO {
    private final Connection connection;

    public PaymentDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public boolean save(Payment payment) {
        String sql = "INSERT INTO payments (user_id, level, amount, currency, " +
                    "description, payment_method, transaction_id, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, payment.getUserId());
            ps.setInt(2, payment.getLevel());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getCurrency());
            ps.setString(5, payment.getDescription());
            ps.setString(6, payment.getPaymentMethod());
            ps.setString(7, payment.getTransactionId());
            ps.setString(8, payment.getStatus());
            ps.setTimestamp(9, Timestamp.valueOf(payment.getCreatedAt()));

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    payment.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Payment findById(int id) {
        String sql = "SELECT * FROM payments WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractPaymentFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Payment> findByUserId(int userId) {
        String sql = "SELECT * FROM payments WHERE user_id = ? ORDER BY created_at DESC";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> findByLevel(int level) {
        String sql = "SELECT * FROM payments WHERE level = ? ORDER BY created_at DESC";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, level);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public List<Payment> findByStatus(String status) {
        String sql = "SELECT * FROM payments WHERE status = ? ORDER BY created_at DESC";
        List<Payment> payments = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public boolean hasUserPaidForLevel(int userId, int level) {
        String sql = "SELECT COUNT(*) FROM payments WHERE user_id = ? AND level = ? AND status = 'SUCCESS'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, level);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Payment> findAll() {
        String sql = "SELECT * FROM payments ORDER BY created_at DESC";
        List<Payment> payments = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                payments.add(extractPaymentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payments;
    }

    @Override
    public boolean update(Payment payment) {
        String sql = "UPDATE payments SET user_id = ?, level = ?, amount = ?, " +
                    "currency = ?, description = ?, payment_method = ?, " +
                    "transaction_id = ?, status = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, payment.getUserId());
            ps.setInt(2, payment.getLevel());
            ps.setDouble(3, payment.getAmount());
            ps.setString(4, payment.getCurrency());
            ps.setString(5, payment.getDescription());
            ps.setString(6, payment.getPaymentMethod());
            ps.setString(7, payment.getTransactionId());
            ps.setString(8, payment.getStatus());
            ps.setInt(9, payment.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM payments WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Helper method to extract Payment object from ResultSet
     */
    private Payment extractPaymentFromResultSet(ResultSet rs) throws SQLException {
        return new Payment(
            rs.getInt("id"),
            rs.getInt("user_id"),
            rs.getInt("level"),
            rs.getDouble("amount"),
            rs.getString("currency"),
            rs.getString("description"),
            rs.getString("payment_method"),
            rs.getString("transaction_id"),
            rs.getString("status"),
            rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}

