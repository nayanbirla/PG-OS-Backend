package com.pgos.booking;

import com.pgos.booking.dto.BookingResponse;
import com.pgos.booking.dto.CreateBookingRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    // ----- Customer -----

    @PostMapping("/api/v1/bookings")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@AuthenticationPrincipal Long userId,
                                  @Valid @RequestBody CreateBookingRequest request) {
        return bookingService.create(userId, request);
    }

    @GetMapping("/api/v1/bookings/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<BookingResponse> mine(@AuthenticationPrincipal Long userId) {
        return bookingService.myRequests(userId);
    }

    @PostMapping("/api/v1/bookings/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    public BookingResponse cancel(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return bookingService.cancel(userId, id);
    }

    // ----- Owner -----

    @GetMapping("/api/v1/properties/{pid}/bookings")
    @PreAuthorize("hasRole('OWNER')")
    public List<BookingResponse> listForProperty(@AuthenticationPrincipal Long userId,
                                                 @PathVariable Long pid,
                                                 @RequestParam(defaultValue = "PENDING") BookingRequest.Status status) {
        return bookingService.listForProperty(userId, pid, status);
    }

    @PostMapping("/api/v1/bookings/{id}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public BookingResponse approve(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return bookingService.approve(userId, id);
    }

    @PostMapping("/api/v1/bookings/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public BookingResponse reject(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return bookingService.reject(userId, id);
    }
}