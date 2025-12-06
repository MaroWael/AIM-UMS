package com.ums.system.dao;

import com.ums.system.model.Payment;
import java.util.List;

public interface PaymentDAO {
    boolean save(Payment payment);
    Payment findById(int id);
    List<Payment> findByUserId(int userId);
    List<Payment> findByLevel(int level);
    List<Payment> findByStatus(String status);
    boolean hasUserPaidForLevel(int userId, int level);
    List<Payment> findAll();
    boolean update(Payment payment);
    boolean delete(int id);
}

