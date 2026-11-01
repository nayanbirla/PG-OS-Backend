package com.pgos.admin.dto;

import com.pgos.user.User;

import java.time.Instant;

public record OwnerSummary(
        Long id,
        String fullName,
        String email,
        String phone,
        String ownerVerificationStatus,
        Instant createdAt
) {
    public static OwnerSummary from(User u) {
        return new OwnerSummary(
                u.getId(),
                u.getFullName(),
                u.getEmail(),
                u.getPhone(),
                u.getOwnerVerificationStatus() == null ? null : u.getOwnerVerificationStatus().name(),
                u.getCreatedAt());
    }
}