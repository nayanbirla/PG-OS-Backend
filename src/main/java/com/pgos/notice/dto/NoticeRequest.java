package com.pgos.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticeRequest(
        @NotBlank @Size(max = 150) String title,
        @NotBlank @Size(max = 3000) String body
) {}