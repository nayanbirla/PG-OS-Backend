package com.pgos.property.dto;

import com.pgos.property.PropertyMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotBlank String phone,
        @NotNull PropertyMember.MemberRole memberRole
) {}