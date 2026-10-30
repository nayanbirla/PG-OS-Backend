package com.pgos.property;

import com.pgos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "beds",
        uniqueConstraints = @UniqueConstraint(columnNames = {"room_id", "bed_number"}))
@Getter
@Setter
@NoArgsConstructor
public class Bed extends BaseEntity {

    public enum BedStatus { AVAILABLE, OCCUPIED, MAINTENANCE }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "bed_number", nullable = false, length = 20)
    private String bedNumber;

    @Column(name = "monthly_rent", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyRent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BedStatus status = BedStatus.AVAILABLE;

    public Bed(Room room, String bedNumber, BigDecimal monthlyRent) {
        this.room = room;
        this.bedNumber = bedNumber;
        this.monthlyRent = monthlyRent;
    }
}