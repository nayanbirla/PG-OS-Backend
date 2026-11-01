package com.pgos.tenancy;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenancyRepository extends JpaRepository<Tenancy, Long> {

    boolean existsByBedIdAndStatus(Long bedId, Tenancy.Status status);

    List<Tenancy> findByStatus(Tenancy.Status status);

    List<Tenancy> findByTenantIdOrderByStartDateDesc(Long tenantId);

    List<Tenancy> findByTenantIdAndStatus(Long tenantId, Tenancy.Status status);

    List<Tenancy> findByBedRoomPropertyIdAndStatus(Long propertyId, Tenancy.Status status);
}