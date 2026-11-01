package com.pgos.tenancy;

import com.pgos.property.PropertyAccessService;
import com.pgos.tenancy.dto.CreateVacateRequest;
import com.pgos.tenancy.dto.TenancyResponse;
import com.pgos.tenancy.dto.VacateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.pgos.tenancy.TenancyLifecycleService; // same package, no import needed
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.time.ZoneId;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenancyService {

    private final TenancyRepository tenancyRepository;
    private final VacateRequestRepository vacateRepository;
    private final PropertyAccessService access;

    @Value("${app.timezone}")
    private String timezone;

    private final TenancyLifecycleService lifecycleService;

    // ---------- Customer ----------

    @Transactional(readOnly = true)
    public List<TenancyResponse> myTenancies(Long tenantId) {
        return tenancyRepository.findByTenantIdOrderByStartDateDesc(tenantId).stream()
                .map(TenancyResponse::from)
                .toList();
    }

    @Transactional
    public VacateResponse requestVacate(Long tenantId, Long tenancyId, CreateVacateRequest req) {
        Tenancy tenancy = findTenancy(tenancyId);
        if (!tenancy.getTenant().getId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your tenancy");
        }
        if (tenancy.getStatus() != Tenancy.Status.ACTIVE) {
            throw conflict("Tenancy is not active");
        }
        if (vacateRepository.existsByTenancyIdAndStatus(tenancyId, VacateRequest.Status.PENDING)) {
            throw conflict("A vacate request is already pending for this tenancy");
        }
        if (req.requestedVacateDate().isBefore(tenancy.getStartDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Vacate date cannot be before move-in date");
        }
        if (tenancy.getEndDate() != null) {
            throw conflict("A move-out date is already approved for this tenancy");
        }

        VacateRequest vacate = new VacateRequest(
                tenancy, tenancy.getTenant(), req.requestedVacateDate(), req.reason());
        return VacateResponse.from(vacateRepository.save(vacate));
    }

    // ---------- Owner / staff ----------

    @Transactional(readOnly = true)
    public List<TenancyResponse> listTenancies(Long userId, Long propertyId, Tenancy.Status status) {
        access.requireAccess(userId, propertyId);
        return tenancyRepository.findByBedRoomPropertyIdAndStatus(propertyId, status).stream()
                .map(TenancyResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<VacateResponse> listVacateRequests(Long ownerId, Long propertyId, VacateRequest.Status status) {
        access.requireOwner(ownerId, propertyId);
        return vacateRepository.findByTenancyBedRoomPropertyIdAndStatus(propertyId, status).stream()
                .map(VacateResponse::from)
                .toList();
    }

    @Transactional
    public VacateResponse approveVacate(Long ownerId, Long vacateId) {
        VacateRequest vacate = findVacate(vacateId);
        access.requireOwner(ownerId, propertyIdOf(vacate));

        if (vacate.getStatus() != VacateRequest.Status.PENDING) {
            throw conflict("Only pending requests can be approved");
        }
        Tenancy tenancy = vacate.getTenancy();
        if (tenancy.getStatus() != Tenancy.Status.ACTIVE) {
            throw conflict("Tenancy is no longer active");
        }

        vacate.setStatus(VacateRequest.Status.APPROVED);
        tenancy.setEndDate(vacate.getRequestedVacateDate());

        // Move-outs dated today or earlier end immediately. Future ones wait for the daily job.
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        if (!vacate.getRequestedVacateDate().isAfter(today)) {
            lifecycleService.complete(vacate);
        }
        return VacateResponse.from(vacate);
    }

    @Transactional
    public VacateResponse rejectVacate(Long ownerId, Long vacateId) {
        VacateRequest vacate = findVacate(vacateId);
        access.requireOwner(ownerId, propertyIdOf(vacate));

        if (vacate.getStatus() != VacateRequest.Status.PENDING) {
            throw conflict("Only pending requests can be rejected");
        }
        vacate.setStatus(VacateRequest.Status.REJECTED);
        return VacateResponse.from(vacate);
    }

    // ---------- Helpers ----------

    private Tenancy findTenancy(Long id) {
        return tenancyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tenancy not found"));
    }

    private VacateRequest findVacate(Long id) {
        return vacateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Vacate request not found"));
    }

    private Long propertyIdOf(VacateRequest vacate) {
        return vacate.getTenancy().getBed().getRoom().getProperty().getId();
    }

    private ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }
}