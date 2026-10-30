package com.pgos.complaint;

import com.pgos.common.BaseEntity;
import com.pgos.tenancy.Tenancy;
import com.pgos.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
public class Complaint extends BaseEntity {

    public enum Status { OPEN, IN_PROGRESS, RESOLVED }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenancy_id", nullable = false)
    private Tenancy tenancy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "raised_by_id", nullable = false)
    private User raisedBy;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Status status = Status.OPEN;

    public Complaint(Tenancy tenancy, User raisedBy, String title, String description) {
        this.tenancy = tenancy;
        this.raisedBy = raisedBy;
        this.title = title;
        this.description = description;
    }
}