package com.pgos.booking.dto;

import com.pgos.booking.BookingRequest;

import java.time.LocalDate;

public record BookingResponse(
        Long id,
        Long bedId,
        Long propertyId,
        Long customerId,
        String customerName,
        LocalDate requestedStartDate,
        String message,
        String status
) {
    public static BookingResponse from(BookingRequest b) {
        return new BookingResponse(
                b.getId(),
                b.getBed().getId(),
                b.getBed().getRoom().getProperty().getId(),
                b.getCustomer().getId(),
                b.getCustomer().getFullName(),
                b.getRequestedStartDate(),
                b.getMessage(),
                b.getStatus().name());
    }
}