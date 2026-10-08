package com.trainticket.service;

import com.trainticket.entity.Payment;

import java.math.BigDecimal;

/**
 * Simple refund rules for payments.
 */
public class RefundPolicy {

    private static final BigDecimal REFUND_RATE = new BigDecimal("0.9");

    public boolean isRefundable(Payment payment) {
        if (payment == null || payment.getPrice() == null) {
            return false;
        }
        try {
            return new BigDecimal(payment.getPrice()).signum() > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public BigDecimal refundAmount(Payment payment) {
        if (!isRefundable(payment)) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(payment.getPrice()).multiply(REFUND_RATE);
    }
}
