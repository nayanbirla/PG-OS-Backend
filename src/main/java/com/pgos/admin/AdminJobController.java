package com.pgos.admin;

import com.pgos.scheduler.DailyJobs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/jobs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminJobController {

    private final DailyJobs dailyJobs;

    @PostMapping("/run")
    public DailyJobs.JobRunResult run() {
        return dailyJobs.runAll();
    }
}