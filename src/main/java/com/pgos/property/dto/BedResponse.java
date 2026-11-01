package com.pgos.property.dto;

import com.pgos.property.Bed;

import java.math.BigDecimal;

public record BedResponse(Long id, String bedNumber, BigDecimal monthlyRent, String status) {
    public static BedResponse from(Bed b) {
        return new BedResponse(b.getId(), b.getBedNumber(), b.getMonthlyRent(), b.getStatus().name());
    }
}