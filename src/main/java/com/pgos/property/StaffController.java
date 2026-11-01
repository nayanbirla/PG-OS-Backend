package com.pgos.property;

import com.pgos.property.dto.AddMemberRequest;
import com.pgos.property.dto.MemberResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties/{pid}/members")
@RequiredArgsConstructor
public class StaffController {

    private final PropertyService propertyService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse add(@AuthenticationPrincipal Long userId,
                              @PathVariable Long pid,
                              @Valid @RequestBody AddMemberRequest request) {
        return propertyService.addMember(userId, pid, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<MemberResponse> list(@AuthenticationPrincipal Long userId, @PathVariable Long pid) {
        return propertyService.listMembers(userId, pid);
    }
}