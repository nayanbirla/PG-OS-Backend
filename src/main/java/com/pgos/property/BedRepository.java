package com.pgos.property;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByRoomId(Long roomId);

    boolean existsByRoomIdAndBedNumber(Long roomId, String bedNumber);

    @Query("""
            select b from Bed b
            join fetch b.room r
            join fetch r.property p
            where b.status = :status
            order by p.city, p.name, r.roomNumber, b.bedNumber
            """)
    List<Bed> findWithPropertyByStatus(@Param("status") Bed.BedStatus status);

    @Query("""
            select b from Bed b
            join fetch b.room r
            where r.property.id = :propertyId and b.status = :status
            order by r.roomNumber, b.bedNumber
            """)
    List<Bed> findByPropertyIdAndStatus(@Param("propertyId") Long propertyId,
                                        @Param("status") Bed.BedStatus status);
}