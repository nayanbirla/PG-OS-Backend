package com.pgos.tenancy;

import com.pgos.tenancy.dto.CreateVacateRequest;
import com.pgos.tenancy.dto.TenancyResponse;
import com.pgos.tenancy.dto.VacateResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TenancyController {

    private final TenancyService tenancyService;

    // ----- Customer -----

    @GetMapping("/api/v1/tenancies/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<TenancyResponse> mine(@AuthenticationPrincipal Long userId) {
        return tenancyService.myTenancies(userId);
    }

    @PostMapping("/api/v1/tenancies/{id}/vacate-requests")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public VacateResponse requestVacate(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long id,
                                        @Valid @RequestBody CreateVacateRequest request) {
        return tenancyService.requestVacate(userId, id, request);
    }

    // ----- Owner / staff -----

    @GetMapping("/api/v1/properties/{pid}/tenancies")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<TenancyResponse> listTenancies(@AuthenticationPrincipal Long userId,
                                               @PathVariable Long pid,
                                               @RequestParam(defaultValue = "ACTIVE") Tenancy.Status status) {
        return tenancyService.listTenancies(userId, pid, status);
    }

    @GetMapping("/api/v1/properties/{pid}/vacate-requests")
    @PreAuthorize("hasRole('OWNER')")
    public List<VacateResponse> listVacateRequests(@AuthenticationPrincipal Long userId,
                                                   @PathVariable Long pid,
                                                   @RequestParam(defaultValue = "PENDING") VacateRequest.Status status) {
        return tenancyService.listVacateRequests(userId, pid, status);
    }

    @PostMapping("/api/v1/vacate-requests/{id}/approve")
    @PreAuthorize("hasRole('OWNER')")
    public VacateResponse approve(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return tenancyService.approveVacate(userId, id);
    }

    @PostMapping("/api/v1/vacate-requests/{id}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public VacateResponse reject(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return tenancyService.rejectVacate(userId, id);
    }
}