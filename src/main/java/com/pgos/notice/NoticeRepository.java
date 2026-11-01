package com.pgos.notice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByPropertyIdOrderByCreatedAtDesc(Long propertyId);

    List<Notice> findByPropertyIdInOrderByCreatedAtDesc(Collection<Long> propertyIds);
}