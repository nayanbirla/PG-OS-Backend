package com.pgos.tenancy.dto;

import com.pgos.tenancy.VacateRequest;

import java.time.LocalDate;

public record VacateResponse(
        Long id,
        Long tenancyId,
        Long requestedById,
        String requestedByName,
        LocalDate requestedVacateDate,
        String reason,
        String status
) {
    public static VacateResponse from(VacateRequest v) {
        return new VacateResponse(
                v.getId(),
                v.getTenancy().getId(),
                v.getRequestedBy().getId(),
                v.getRequestedBy().getFullName(),
                v.getRequestedVacateDate(),
                v.getReason(),
                v.getStatus().name());
    }
}