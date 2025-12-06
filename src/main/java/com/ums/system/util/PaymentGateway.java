package com.ums.system.util;

import com.ums.system.model.PaymentRequest;
import com.ums.system.model.PaymentResult;

public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
    PaymentResult checkPaymentStatus(String transactionId);
}

