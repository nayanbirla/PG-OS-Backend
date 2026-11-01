package com.pgos.tenancy;

import com.pgos.property.Bed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TenancyLifecycleService {

    private final VacateRequestRepository vacateRepository;

    /** Ends the tenancy and frees the bed for an approved move-out. */
    @Transactional
    public void complete(VacateRequest vacate) {
        Tenancy tenancy = vacate.getTenancy();
        tenancy.setStatus(Tenancy.Status.ENDED);
        tenancy.getBed().setStatus(Bed.BedStatus.AVAILABLE);
        vacate.setStatus(VacateRequest.Status.COMPLETED);
    }

    /** Completes every approved move-out whose date has arrived. */
    @Transactional
    public int completeDueVacates(LocalDate today) {
        List<VacateRequest> due = vacateRepository
                .findByStatusAndRequestedVacateDateLessThanEqual(VacateRequest.Status.APPROVED, today);
        due.forEach(this::complete);
        return due.size();
    }
}