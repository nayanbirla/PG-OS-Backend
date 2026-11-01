package com.pgos.admin;

import com.pgos.admin.dto.OwnerSummary;
import com.pgos.user.OwnerVerificationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/owners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public List<OwnerSummary> list(
            @RequestParam(defaultValue = "PENDING") OwnerVerificationStatus status) {
        return adminService.listOwners(status);
    }

    @PostMapping("/{id}/verify")
    public OwnerSummary verify(@PathVariable Long id) {
        return adminService.verify(id);
    }

    @PostMapping("/{id}/reject")
    public OwnerSummary reject(@PathVariable Long id) {
        return adminService.reject(id);
    }
}