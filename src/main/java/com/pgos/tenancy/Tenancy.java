package com.pgos.tenancy;

import com.pgos.common.BaseEntity;
import com.pgos.property.Bed;
import com.pgos.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tenancies")
@Getter
@Setter
@NoArgsConstructor
public class Tenancy extends BaseEntity {

    public enum Status { ACTIVE, ENDED }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private User tenant;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    /** Snapshot of the rent agreed at move-in, so later bed price changes don't affect this tenancy. */
    @Column(name = "monthly_rent", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.ACTIVE;

    public Tenancy(Bed bed, User tenant, LocalDate startDate, BigDecimal monthlyRent) {
        this.bed = bed;
        this.tenant = tenant;
        this.startDate = startDate;
        this.monthlyRent = monthlyRent;
    }
}