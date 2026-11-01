package com.pgos.complaint.dto;

import com.pgos.complaint.Complaint;

import java.time.Instant;

public record ComplaintResponse(
        Long id,
        Long tenancyId,
        String raisedByName,
        String title,
        String description,
        String status,
        Instant createdAt
) {
    public static ComplaintResponse from(Complaint c) {
        return new ComplaintResponse(
                c.getId(),
                c.getTenancy().getId(),
                c.getRaisedBy().getFullName(),
                c.getTitle(),
                c.getDescription(),
                c.getStatus().name(),
                c.getCreatedAt());
    }
}