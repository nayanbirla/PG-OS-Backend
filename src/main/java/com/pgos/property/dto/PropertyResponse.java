package com.pgos.property.dto;

import com.pgos.property.Property;

public record PropertyResponse(
        Long id,
        Long ownerId,
        String name,
        String addressLine,
        String city,
        String pincode,
        String description
) {
    public static PropertyResponse from(Property p) {
        return new PropertyResponse(p.getId(), p.getOwner().getId(), p.getName(),
                p.getAddressLine(), p.getCity(), p.getPincode(), p.getDescription());
    }
}