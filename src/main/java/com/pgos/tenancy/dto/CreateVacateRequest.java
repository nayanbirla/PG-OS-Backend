package com.pgos.tenancy.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateVacateRequest(
        @NotNull @FutureOrPresent LocalDate requestedVacateDate,
        @Size(max = 500) String reason
) {}