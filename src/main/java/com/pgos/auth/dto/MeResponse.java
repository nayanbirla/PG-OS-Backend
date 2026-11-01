package com.pgos.auth.dto;

import java.time.LocalDate;
import java.util.List;

public record MeResponse(
        Long id,
        String fullName,
        String email,
        String phone,
        String role,
        String accountStatus,
        String ownerVerificationStatus,
        List<PropertyAccess> properties,
        List<ActiveTenancy> activeTenancies
) {
    /** relation is OWNER, MANAGER or CARETAKER */
    public record PropertyAccess(Long propertyId, String name, String city, String relation) {}

    public record ActiveTenancy(Long tenancyId, Long propertyId, String roomNumber, String bedNumber,
                                LocalDate startDate) {}
}