package com.ums.system.service;

import com.ums.system.model.Payment;
import com.ums.system.model.PaymentRequest;
import java.util.List;

public interface PaymentService {
    Payment processPayment(PaymentRequest request);
    Payment getPaymentById(int id);
    List<Payment> getPaymentsByUserId(int userId);
    List<Payment> getPaymentsByLevel(int level);
    List<Payment> getAllPayments();
    boolean hasUserPaidForLevel(int userId, int level);
    double calculateLevelFee(int level);
    double getTotalRevenue();
    double getTotalRevenueByLevel(int level);
}

