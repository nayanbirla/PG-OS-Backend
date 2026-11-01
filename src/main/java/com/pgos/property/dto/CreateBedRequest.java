package com.pgos.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateBedRequest(
        @NotBlank @Size(max = 20) String bedNumber,
        @NotNull @DecimalMin("0.00") BigDecimal monthlyRent
) {}