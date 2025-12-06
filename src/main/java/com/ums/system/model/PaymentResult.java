package com.ums.system.model;

import java.time.LocalDateTime;

public class PaymentResult {
    private boolean success;
    private String status;
    private String transactionId;
    private String errorMessage;
    private LocalDateTime processedAt;

    public PaymentResult(boolean success, String status, String transactionId) {
        this.success = success;
        this.status = status;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
}

