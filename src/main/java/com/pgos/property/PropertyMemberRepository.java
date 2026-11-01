package com.pgos.property;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PropertyMemberRepository extends JpaRepository<PropertyMember, Long> {

    boolean existsByPropertyIdAndUserId(Long propertyId, Long userId);

    List<PropertyMember> findByPropertyId(Long propertyId);

    List<PropertyMember> findByUserId(Long userId);
}