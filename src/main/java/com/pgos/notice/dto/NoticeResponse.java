package com.pgos.notice.dto;

import com.pgos.notice.Notice;

import java.time.Instant;

public record NoticeResponse(
        Long id,
        Long propertyId,
        String authorName,
        String title,
        String body,
        Instant createdAt
) {
    public static NoticeResponse from(Notice n) {
        return new NoticeResponse(
                n.getId(),
                n.getProperty().getId(),
                n.getAuthor().getFullName(),
                n.getTitle(),
                n.getBody(),
                n.getCreatedAt());
    }
}