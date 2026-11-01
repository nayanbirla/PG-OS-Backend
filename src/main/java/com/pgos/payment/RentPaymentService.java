package com.pgos.payment;

import com.pgos.payment.dto.RentPaymentResponse;
import com.pgos.property.PropertyAccessService;
import com.pgos.tenancy.Tenancy;
import com.pgos.tenancy.TenancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentPaymentService {

    private final RentPaymentRepository paymentRepository;
    private final TenancyRepository tenancyRepository;
    private final PropertyAccessService access;

    /** Creates one PENDING payment per active tenancy for the month. Existing ones are skipped. */
    @Transactional
    public List<RentPaymentResponse> generateDues(Long ownerId, Long propertyId, LocalDate period) {
        access.requireOwner(ownerId, propertyId);
        LocalDate month = period.withDayOfMonth(1);
        LocalDate monthEnd = month.withDayOfMonth(month.lengthOfMonth());

        return tenancyRepository.findByBedRoomPropertyIdAndStatus(propertyId, Tenancy.Status.ACTIVE).stream()
                .filter(t -> !t.getStartDate().isAfter(monthEnd))
                .filter(t -> !paymentRepository.existsByTenancyIdAndPeriodMonth(t.getId(), month))
                .map(t -> paymentRepository.save(new RentPayment(t, month, t.getMonthlyRent())))
                .map(RentPaymentResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RentPaymentResponse> listForProperty(Long userId, Long propertyId, LocalDate period) {
        access.requireAccess(userId, propertyId);
        return paymentRepository.findByTenancyBedRoomPropertyIdAndPeriodMonth(propertyId, period.withDayOfMonth(1))
                .stream()
                .map(RentPaymentResponse::from)
                .toList();
    }

    @Transactional
    public RentPaymentResponse markPaid(Long ownerId, Long paymentId) {
        RentPayment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> notFound("Payment not found"));
        Long propertyId = payment.getTenancy().getBed().getRoom().getProperty().getId();
        access.requireOwner(ownerId, propertyId);

        if (payment.getStatus() == RentPayment.Status.PAID) {
            throw conflict("Payment is already marked as paid");
        }
        payment.setStatus(RentPayment.Status.PAID);
        payment.setPaidAt(Instant.now());
        return RentPaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public List<RentPaymentResponse> myPayments(Long tenantId, Long tenancyId) {
        Tenancy tenancy = tenancyRepository.findById(tenancyId)
                .orElseThrow(() -> notFound("Tenancy not found"));
        if (!tenancy.getTenant().getId().equals(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your tenancy");
        }
        return paymentRepository.findByTenancyIdOrderByPeriodMonthDesc(tenancyId).stream()
                .map(RentPaymentResponse::from)
                .toList();
    }

    private ResponseStatusException notFound(String msg) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
    }

    private ResponseStatusException conflict(String msg) {
        return new ResponseStatusException(HttpStatus.CONFLICT, msg);
    }

    @Transactional
    public int generateDuesForAllActiveTenancies(LocalDate period) {
        LocalDate month = period.withDayOfMonth(1);
        LocalDate monthEnd = month.withDayOfMonth(month.lengthOfMonth());
        int created = 0;

        for (Tenancy tenancy : tenancyRepository.findByStatus(Tenancy.Status.ACTIVE)) {
            if (tenancy.getStartDate().isAfter(monthEnd)) continue;
            if (paymentRepository.existsByTenancyIdAndPeriodMonth(tenancy.getId(), month)) continue;
            paymentRepository.save(new RentPayment(tenancy, month, tenancy.getMonthlyRent()));
            created++;
        }
        return created;
    }
}