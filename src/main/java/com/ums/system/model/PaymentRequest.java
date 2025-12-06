package com.ums.system.model;

public class PaymentRequest {
    private int userId;
    private int level;  // Student's level (1, 2, 3, 4)
    private double amount;
    private String currency;
    private String description;
    private String paymentMethod;

    public PaymentRequest(int userId, int level, double amount,
                         String currency, String description, String paymentMethod) {
        this.userId = userId;
        this.level = level;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.paymentMethod = paymentMethod;
    }

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
}

