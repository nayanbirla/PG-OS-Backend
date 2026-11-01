package com.pgos.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePropertyRequest(
        @NotBlank @Size(max = 120) String name,
        @NotBlank @Size(max = 255) String addressLine,
        @NotBlank @Size(max = 80) String city,
        @Size(max = 10) String pincode,
        @Size(max = 1000) String description
) {}