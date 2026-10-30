package com.pgos.payment;

import com.pgos.common.BaseEntity;
import com.pgos.tenancy.Tenancy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "rent_payments")
@Getter
@Setter
@NoArgsConstructor
public class RentPayment extends BaseEntity {

    public enum Status { PENDING, PAID, FAILED }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenancy_id", nullable = false)
    private Tenancy tenancy;

    /** First day of the month this payment covers, e.g. 2026-11-01. */
    @Column(name = "period_month", nullable = false)
    private LocalDate periodMonth;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "paid_at")
    private Instant paidAt;

    /** Reference returned by the payment gateway, paid directly to the owner. */
    @Column(name = "gateway_reference", length = 100)
    private String gatewayReference;

    public RentPayment(Tenancy tenancy, LocalDate periodMonth, BigDecimal amount) {
        this.tenancy = tenancy;
        this.periodMonth = periodMonth;
        this.amount = amount;
    }
}