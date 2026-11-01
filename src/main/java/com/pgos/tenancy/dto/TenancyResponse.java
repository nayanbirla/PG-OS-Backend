package com.pgos.tenancy.dto;

import com.pgos.tenancy.Tenancy;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TenancyResponse(
        Long id,
        Long propertyId,
        String roomNumber,
        String bedNumber,
        Long tenantId,
        String tenantName,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal monthlyRent,
        String status
) {
    public static TenancyResponse from(Tenancy t) {
        return new TenancyResponse(
                t.getId(),
                t.getBed().getRoom().getProperty().getId(),
                t.getBed().getRoom().getRoomNumber(),
                t.getBed().getBedNumber(),
                t.getTenant().getId(),
                t.getTenant().getFullName(),
                t.getStartDate(),
                t.getEndDate(),
                t.getMonthlyRent(),
                t.getStatus().name());
    }
}