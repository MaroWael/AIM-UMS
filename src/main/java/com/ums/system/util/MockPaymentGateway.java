package com.ums.system.util;

import com.ums.system.model.PaymentRequest;
import com.ums.system.model.PaymentResult;

/**
 * Mock Payment Gateway for simulation purposes
 * Simulates payment processing with realistic behavior for level fees
 */
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Simulate processing delay (1-2 seconds)
        simulateDelay(1000 + (int)(Math.random() * 1000));

        // Validation checks
        if (request.getAmount() <= 0) {
            PaymentResult result = new PaymentResult(false, "FAILED", null);
            result.setErrorMessage("Invalid amount. Amount must be greater than 0.");
            return result;
        }

        if (request.getAmount() > 999999) {
            PaymentResult result = new PaymentResult(false, "FAILED", null);
            result.setErrorMessage("Amount exceeds maximum transaction limit.");
            return result;
        }

        // Simulate 95% success rate (5% random failures)
        boolean isSuccessful = Math.random() > 0.10;

        if (isSuccessful) {
            String transactionId = generateTransactionId();
            PaymentResult result = new PaymentResult(true, "SUCCESS", transactionId);
            return result;
        } else {
            // Simulate different types of failures
            String[] errorMessages = {
                "Insufficient funds",
                "Payment declined by bank",
                "Card expired",
                "Invalid payment method",
                "Network timeout"
            };
            String errorMsg = errorMessages[(int)(Math.random() * errorMessages.length)];

            PaymentResult result = new PaymentResult(false, "FAILED", null);
            result.setErrorMessage(errorMsg);
            return result;
        }
    }


    @Override
    public PaymentResult checkPaymentStatus(String transactionId) {
        // Quick status check, minimal delay
        simulateDelay(200);

        if (transactionId == null || transactionId.isEmpty()) {
            PaymentResult result = new PaymentResult(false, "UNKNOWN", null);
            result.setErrorMessage("Invalid transaction ID");
            return result;
        }

        return new PaymentResult(true, "COMPLETED", transactionId);
    }

    /**
     * Generates a unique transaction ID
     */
    private String generateTransactionId() {
        return "TXN_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * Simulates network/processing delay
     */
    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

