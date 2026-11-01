package com.pgos.payment;

import com.pgos.payment.dto.RentPaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RentPaymentController {

    private final RentPaymentService paymentService;

    @PostMapping("/api/v1/properties/{pid}/rent-payments/generate")
    @PreAuthorize("hasRole('OWNER')")
    public List<RentPaymentResponse> generate(@AuthenticationPrincipal Long userId,
                                              @PathVariable Long pid,
                                              @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        return paymentService.generateDues(userId, pid, period);
    }

    @GetMapping("/api/v1/properties/{pid}/rent-payments")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<RentPaymentResponse> list(@AuthenticationPrincipal Long userId,
                                          @PathVariable Long pid,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate period) {
        return paymentService.listForProperty(userId, pid, period);
    }

    @PostMapping("/api/v1/rent-payments/{id}/mark-paid")
    @PreAuthorize("hasRole('OWNER')")
    public RentPaymentResponse markPaid(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return paymentService.markPaid(userId, id);
    }

    @GetMapping("/api/v1/tenancies/{id}/payments")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<RentPaymentResponse> myPayments(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return paymentService.myPayments(userId, id);
    }
}