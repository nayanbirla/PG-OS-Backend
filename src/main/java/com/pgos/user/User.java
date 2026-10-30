package com.pgos.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    @Column(unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "owner_verification_status", length = 20)
    private OwnerVerificationStatus ownerVerificationStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public User(String fullName, String phone, String email, Role role, AccountStatus accountStatus) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.accountStatus = accountStatus;
        if (role == Role.OWNER) {
            this.ownerVerificationStatus = OwnerVerificationStatus.PENDING;
        }
    }

    /** Creates a walk-in tenant account; the tenant claims it later by setting a password. */
    public static User invitedCustomer(String fullName, String phone) {
        return new User(fullName, phone, null, Role.CUSTOMER, AccountStatus.INVITED);
    }
}