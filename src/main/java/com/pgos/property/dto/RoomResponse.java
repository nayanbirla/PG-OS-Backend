package com.pgos.property.dto;

import com.pgos.property.Room;

public record RoomResponse(Long id, String roomNumber, Integer floor, String description) {
    public static RoomResponse from(Room r) {
        return new RoomResponse(r.getId(), r.getRoomNumber(), r.getFloor(), r.getDescription());
    }
}