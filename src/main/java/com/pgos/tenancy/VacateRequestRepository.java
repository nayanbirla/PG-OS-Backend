package com.pgos.tenancy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VacateRequestRepository extends JpaRepository<VacateRequest, Long> {

    boolean existsByTenancyIdAndStatus(Long tenancyId, VacateRequest.Status status);

    List<VacateRequest> findByTenancyBedRoomPropertyIdAndStatus(Long propertyId, VacateRequest.Status status);

    List<VacateRequest> findByStatusAndRequestedVacateDateLessThanEqual(VacateRequest.Status status, LocalDate date);
}