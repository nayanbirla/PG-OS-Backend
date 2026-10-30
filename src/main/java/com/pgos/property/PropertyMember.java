package com.pgos.property;

import com.pgos.common.BaseEntity;
import com.pgos.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "property_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"property_id", "user_id"}))
@Getter
@Setter
@NoArgsConstructor
public class PropertyMember extends BaseEntity {

    public enum MemberRole { MANAGER, CARETAKER }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "member_role", nullable = false, length = 20)
    private MemberRole memberRole;

    public PropertyMember(Property property, User user, MemberRole memberRole) {
        this.property = property;
        this.user = user;
        this.memberRole = memberRole;
    }
}