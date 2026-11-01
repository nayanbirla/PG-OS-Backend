package com.pgos.listing.dto;

import com.pgos.property.Bed;
import com.pgos.property.Property;

import java.math.BigDecimal;
import java.util.List;

public record PropertyListingResponse(
        Long id,
        String name,
        String addressLine,
        String city,
        String pincode,
        String description,
        List<AvailableBed> availableBeds
) {
    public record AvailableBed(Long bedId, String roomNumber, String bedNumber, BigDecimal monthlyRent) {
        public static AvailableBed from(Bed b) {
            return new AvailableBed(b.getId(), b.getRoom().getRoomNumber(), b.getBedNumber(), b.getMonthlyRent());
        }
    }

    public static PropertyListingResponse of(Property p, List<AvailableBed> beds) {
        return new PropertyListingResponse(p.getId(), p.getName(), p.getAddressLine(),
                p.getCity(), p.getPincode(), p.getDescription(), beds);
    }
}