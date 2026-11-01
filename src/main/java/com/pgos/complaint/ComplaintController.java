package com.pgos.complaint;

import com.pgos.complaint.dto.ComplaintRequest;
import com.pgos.complaint.dto.ComplaintResponse;
import com.pgos.complaint.dto.UpdateComplaintStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    // ----- Tenant -----

    @PostMapping("/api/v1/tenancies/{id}/complaints")
    @PreAuthorize("hasRole('CUSTOMER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse raise(@AuthenticationPrincipal Long userId,
                                   @PathVariable Long id,
                                   @Valid @RequestBody ComplaintRequest request) {
        return complaintService.raise(userId, id, request);
    }

    @GetMapping("/api/v1/tenancies/{id}/complaints")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<ComplaintResponse> myComplaints(@AuthenticationPrincipal Long userId, @PathVariable Long id) {
        return complaintService.myComplaints(userId, id);
    }

    // ----- Owner / staff -----

    @GetMapping("/api/v1/properties/{pid}/complaints")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<ComplaintResponse> list(@AuthenticationPrincipal Long userId,
                                        @PathVariable Long pid,
                                        @RequestParam(defaultValue = "OPEN") Complaint.Status status) {
        return complaintService.listForProperty(userId, pid, status);
    }

    @PatchMapping("/api/v1/complaints/{id}/status")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public ComplaintResponse updateStatus(@AuthenticationPrincipal Long userId,
                                          @PathVariable Long id,
                                          @Valid @RequestBody UpdateComplaintStatusRequest request) {
        return complaintService.updateStatus(userId, id, request.status());
    }
}