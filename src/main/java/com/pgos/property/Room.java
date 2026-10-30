package com.pgos.property;

import com.pgos.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rooms",
        uniqueConstraints = @UniqueConstraint(columnNames = {"property_id", "room_number"}))
@Getter
@Setter
@NoArgsConstructor
public class Room extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "room_number", nullable = false, length = 20)
    private String roomNumber;

    private Integer floor;

    @Column(length = 500)
    private String description;

    public Room(Property property, String roomNumber, Integer floor, String description) {
        this.property = property;
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.description = description;
    }
}