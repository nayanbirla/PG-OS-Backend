package com.pgos.listing.dto;

import com.pgos.property.Bed;

import java.math.BigDecimal;

public record ListingBedResponse(
        Long bedId,
        String bedNumber,
        BigDecimal monthlyRent,
        Long propertyId,
        String propertyName,
        String city,
        String roomNumber,
        Integer floor
) {
    public static ListingBedResponse from(Bed b) {
        var room = b.getRoom();
        var property = room.getProperty();
        return new ListingBedResponse(
                b.getId(),
                b.getBedNumber(),
                b.getMonthlyRent(),
                property.getId(),
                property.getName(),
                property.getCity(),
                room.getRoomNumber(),
                room.getFloor());
    }
}