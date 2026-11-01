package com.pgos.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long bedId,
        @NotNull @FutureOrPresent LocalDate requestedStartDate,
        @Size(max = 500) String message
) {}