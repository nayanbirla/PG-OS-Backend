package com.pgos.complaint;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByTenancyIdOrderByCreatedAtDesc(Long tenancyId);

    List<Complaint> findByTenancyBedRoomPropertyIdAndStatus(Long propertyId, Complaint.Status status);
}