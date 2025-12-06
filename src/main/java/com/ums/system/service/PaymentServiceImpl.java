package com.ums.system.service;

import com.ums.system.dao.PaymentDAO;
import com.ums.system.dao.PaymentDAOImpl;
import com.ums.system.model.Payment;
import com.ums.system.model.PaymentRequest;
import com.ums.system.model.PaymentResult;
import com.ums.system.util.MockPaymentGateway;
import com.ums.system.util.PaymentGateway;

import java.sql.Connection;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    private final PaymentDAO paymentDAO;
    private final PaymentGateway paymentGateway;

    // Level fee structure in EGP (Egyptian Pounds)
    private static final double LEVEL_1_FEE = 15000.00;  // Freshman
    private static final double LEVEL_2_FEE = 17500.00;  // Sophomore
    private static final double LEVEL_3_FEE = 20000.00;  // Junior
    private static final double LEVEL_4_FEE = 22500.00;  // Senior

    public PaymentServiceImpl(Connection connection) {
        this.paymentDAO = new PaymentDAOImpl(connection);
        this.paymentGateway = new MockPaymentGateway();
    }

    @Override
    public Payment processPayment(PaymentRequest request) {
        // Validate request
        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        if (request.getLevel() < 1 || request.getLevel() > 4) {
            throw new IllegalArgumentException("Invalid level. Level must be between 1 and 4");
        }

        // Check if user already paid for this level
        if (paymentDAO.hasUserPaidForLevel(request.getUserId(), request.getLevel())) {
            throw new IllegalArgumentException("You have already paid for Level " + request.getLevel());
        }

        // Create payment record with PENDING status
        Payment payment = new Payment(
            request.getUserId(),
            request.getLevel(),
            request.getAmount(),
            request.getCurrency(),
            request.getDescription(),
            request.getPaymentMethod()
        );
        payment.setStatus("PENDING");

        // Process payment through gateway
        PaymentResult result = paymentGateway.processPayment(request);

        // Update payment with result
        payment.setTransactionId(result.getTransactionId());
        payment.setStatus(result.getStatus());

        // Save to database
        if (paymentDAO.save(payment)) {
            return payment;
        } else {
            throw new RuntimeException("Failed to save payment to database");
        }
    }


    @Override
    public Payment getPaymentById(int id) {
        return paymentDAO.findById(id);
    }

    @Override
    public List<Payment> getPaymentsByUserId(int userId) {
        return paymentDAO.findByUserId(userId);
    }

    @Override
    public List<Payment> getPaymentsByLevel(int level) {
        return paymentDAO.findByLevel(level);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentDAO.findAll();
    }

    @Override
    public boolean hasUserPaidForLevel(int userId, int level) {
        return paymentDAO.hasUserPaidForLevel(userId, level);
    }

    @Override
    public double calculateLevelFee(int level) {
        switch (level) {
            case 1: return LEVEL_1_FEE;
            case 2: return LEVEL_2_FEE;
            case 3: return LEVEL_3_FEE;
            case 4: return LEVEL_4_FEE;
            default: throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    @Override
    public double getTotalRevenue() {
        List<Payment> successfulPayments = paymentDAO.findByStatus("SUCCESS");
        return successfulPayments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    @Override
    public double getTotalRevenueByLevel(int level) {
        List<Payment> payments = paymentDAO.findByLevel(level);
        return payments.stream()
                .filter(p -> "SUCCESS".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}

