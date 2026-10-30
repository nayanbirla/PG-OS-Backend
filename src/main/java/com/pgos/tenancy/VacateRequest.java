package com.pgos.tenancy;

import com.pgos.common.BaseEntity;
import com.pgos.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vacate_requests")
@Getter
@Setter
@NoArgsConstructor
public class VacateRequest extends BaseEntity {

    public enum Status { PENDING, APPROVED, REJECTED, COMPLETED }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenancy_id", nullable = false)
    private Tenancy tenancy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "requested_by_id", nullable = false)
    private User requestedBy;

    @Column(name = "requested_vacate_date", nullable = false)
    private LocalDate requestedVacateDate;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.PENDING;

    public VacateRequest(Tenancy tenancy, User requestedBy, LocalDate requestedVacateDate, String reason) {
        this.tenancy = tenancy;
        this.requestedBy = requestedBy;
        this.requestedVacateDate = requestedVacateDate;
        this.reason = reason;
    }
}