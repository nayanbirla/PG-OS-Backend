package com.pgos.complaint;

import com.pgos.complaint.dto.ComplaintRequest;
import com.pgos.complaint.dto.ComplaintResponse;
import com.pgos.property.PropertyAccessService;
import com.pgos.tenancy.Tenancy;
import com.pgos.tenancy.TenancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final TenancyRepository tenancyRepository;
    private final PropertyAccessService access;

    @Transactional
    public ComplaintResponse raise(Long tenantId, Long tenancyId, ComplaintRequest req) {
        Tenancy tenancy = ownedTenancy(tenantId, tenancyId);
        Complaint complaint = new Complaint(tenancy, tenancy.getTenant(), req.title(), req.description());
        return ComplaintResponse.from(complaintRepository.save(complaint));
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> myComplaints(Long tenantId, Long tenancyId) {
        ownedTenancy(tenantId, tenancyId);
        return complaintRepository.findByTenancyIdOrderByCreatedAtDesc(tenancyId).stream()
                .map(ComplaintResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ComplaintResponse> listForProperty(Long userId, Long propertyId, Complaint.Status status) {
        access.requireAccess(userId, propertyId);
        return complaintRepository.findByTenancyBedRoomPropertyIdAndStatus(propertyId, status).stream()
                .map(ComplaintResponse::from)
                .toList();
    }

    @Transactional
    public ComplaintResponse updateStatus(Long userId, Long complaintId, Complaint.Status status) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Complaint not found"));
        Long propertyId = complaint.getTenancy().getBed().getRoom().getProperty().getId();
        access.requireAccess(userId, propertyId);

        complaint.setStatus(status);
        return ComplaintResponse.from(complaint);
    }

    private Tenancy ownedTenancy(Long tenantId, Long tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenancy not found"));
        if (!tenancy.getTenant().getId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your tenancy");
        }
        return tenancy;
    }
}