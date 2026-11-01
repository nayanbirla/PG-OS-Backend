package com.pgos.property.dto;

import com.pgos.property.PropertyMember;

public record MemberResponse(Long id, Long userId, String fullName, String phone, String memberRole) {
    public static MemberResponse from(PropertyMember m) {
        return new MemberResponse(m.getId(), m.getUser().getId(), m.getUser().getFullName(),
                m.getUser().getPhone(), m.getMemberRole().name());
    }
}