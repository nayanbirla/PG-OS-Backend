package com.pgos.property;

import com.pgos.property.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyResponse create(@AuthenticationPrincipal Long userId,
                                   @Valid @RequestBody CreatePropertyRequest request) {
        return propertyService.createProperty(userId, request);
    }

    @GetMapping
    @PreAuthorize("hasRole('OWNER')")
    public List<PropertyResponse> listMine(@AuthenticationPrincipal Long userId) {
        return propertyService.listOwnerProperties(userId);
    }

    @GetMapping("/{pid}")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public PropertyResponse get(@AuthenticationPrincipal Long userId, @PathVariable Long pid) {
        return propertyService.getProperty(userId, pid);
    }
}