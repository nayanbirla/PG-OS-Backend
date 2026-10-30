package com.pgos.property;

import com.pgos.common.BaseEntity;
import com.pgos.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
public class Property extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(nullable = false, length = 80)
    private String city;

    @Column(length = 10)
    private String pincode;

    @Column(length = 1000)
    private String description;

    public Property(User owner, String name, String addressLine, String city, String pincode, String description) {
        this.owner = owner;
        this.name = name;
        this.addressLine = addressLine;
        this.city = city;
        this.pincode = pincode;
        this.description = description;
    }
}