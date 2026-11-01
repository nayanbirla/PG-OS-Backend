package com.pgos.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface RentPaymentRepository extends JpaRepository<RentPayment, Long> {

    boolean existsByTenancyIdAndPeriodMonth(Long tenancyId, LocalDate periodMonth);

    List<RentPayment> findByTenancyBedRoomPropertyIdAndPeriodMonth(Long propertyId, LocalDate periodMonth);

    List<RentPayment> findByTenancyIdOrderByPeriodMonthDesc(Long tenancyId);
}