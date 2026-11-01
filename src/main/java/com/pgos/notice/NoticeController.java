package com.pgos.notice;

import com.pgos.notice.dto.NoticeRequest;
import com.pgos.notice.dto.NoticeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/api/v1/properties/{pid}/notices")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    @ResponseStatus(HttpStatus.CREATED)
    public NoticeResponse post(@AuthenticationPrincipal Long userId,
                               @PathVariable Long pid,
                               @Valid @RequestBody NoticeRequest request) {
        return noticeService.post(userId, pid, request);
    }

    @GetMapping("/api/v1/properties/{pid}/notices")
    @PreAuthorize("hasAnyRole('OWNER','STAFF')")
    public List<NoticeResponse> list(@AuthenticationPrincipal Long userId, @PathVariable Long pid) {
        return noticeService.listForProperty(userId, pid);
    }

    @GetMapping("/api/v1/notices/mine")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<NoticeResponse> mine(@AuthenticationPrincipal Long userId) {
        return noticeService.myNotices(userId);
    }
}