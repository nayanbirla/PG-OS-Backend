package com.pgos.property;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByPropertyId(Long propertyId);
    Optional<Room> findByIdAndPropertyId(Long id, Long propertyId);
    boolean existsByPropertyIdAndRoomNumber(Long propertyId, String roomNumber);
}