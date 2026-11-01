package com.pgos.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {

    List<BookingRequest> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    List<BookingRequest> findByBedRoomPropertyIdAndStatus(Long propertyId, BookingRequest.Status status);

    List<BookingRequest> findByBedIdAndStatus(Long bedId, BookingRequest.Status status);

    boolean existsByCustomerIdAndBedIdAndStatus(Long customerId, Long bedId, BookingRequest.Status status);
}