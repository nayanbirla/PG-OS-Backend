package com.pgos.notice;

import com.pgos.notice.dto.NoticeRequest;
import com.pgos.notice.dto.NoticeResponse;
import com.pgos.property.Property;
import com.pgos.property.PropertyAccessService;
import com.pgos.tenancy.Tenancy;
import com.pgos.tenancy.TenancyRepository;
import com.pgos.user.User;
import com.pgos.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final TenancyRepository tenancyRepository;
    private final UserRepository userRepository;
    private final PropertyAccessService access;

    @Transactional
    public NoticeResponse post(Long userId, Long propertyId, NoticeRequest req) {
        Property property = access.requireAccess(userId, propertyId);
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return NoticeResponse.from(noticeRepository.save(
                new Notice(property, author, req.title(), req.body())));
    }

    @Transactional(readOnly = true)
    public List<NoticeResponse> listForProperty(Long userId, Long propertyId) {
        access.requireAccess(userId, propertyId);
        return noticeRepository.findByPropertyIdOrderByCreatedAtDesc(propertyId).stream()
                .map(NoticeResponse::from)
                .toList();
    }

    /** Notices from every property where the tenant currently has an active tenancy. */
    @Transactional(readOnly = true)
    public List<NoticeResponse> myNotices(Long tenantId) {
        List<Long> propertyIds = tenancyRepository.findByTenantIdAndStatus(tenantId, Tenancy.Status.ACTIVE)
                .stream()
                .map(t -> t.getBed().getRoom().getProperty().getId())
                .distinct()
                .toList();
        if (propertyIds.isEmpty()) {
            return List.of();
        }
        return noticeRepository.findByPropertyIdInOrderByCreatedAtDesc(propertyIds).stream()
                .map(NoticeResponse::from)
                .toList();
    }
}