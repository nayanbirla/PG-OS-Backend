package com.pgos.payment.dto;

import com.pgos.payment.RentPayment;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record RentPaymentResponse(
        Long id,
        Long tenancyId,
        String tenantName,
        LocalDate periodMonth,
        BigDecimal amount,
        String status,
        Instant paidAt
) {
    public static RentPaymentResponse from(RentPayment p) {
        return new RentPaymentResponse(
                p.getId(),
                p.getTenancy().getId(),
                p.getTenancy().getTenant().getFullName(),
                p.getPeriodMonth(),
                p.getAmount(),
                p.getStatus().name(),
                p.getPaidAt());
    }
}