package com.ums.system.model;

import java.time.LocalDateTime;

public class Payment {
    private int id;
    private int userId;
    private int level;
    private double amount;
    private String currency;
    private String description;
    private String paymentMethod; // CARD, BANK_TRANSFER, CASH
    private String transactionId;
    private String status; // SUCCESS, FAILED, PENDING
    private LocalDateTime createdAt;

    public Payment(int userId, int level, double amount, String currency,
                   String description, String paymentMethod) {
        this.userId = userId;
        this.level = level;
        this.amount = amount;
        this.currency = (currency == null || currency.isEmpty()) ? "EGP" : currency;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
        this.createdAt = LocalDateTime.now();
    }

    // Constructor for retrieving from database
    public Payment(int id, int userId, int level, double amount, String currency,
                   String description, String paymentMethod, String transactionId,
                   String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.level = level;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.paymentMethod = paymentMethod;
        this.transactionId = transactionId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", userId=" + userId +
                ", level=" + level +
                ", amount=" + amount +
                ", status='" + status + '\'' +
                ", transactionId='" + transactionId + '\'' +
                '}';
    }
}

