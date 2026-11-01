package com.pgos.scheduler;

import com.pgos.payment.RentPaymentService;
import com.pgos.tenancy.TenancyLifecycleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyJobs {

    private final RentPaymentService rentPaymentService;
    private final TenancyLifecycleService lifecycleService;

    @Value("${app.timezone}")
    private String timezone;

    @Scheduled(cron = "${app.jobs.daily-cron}", zone = "${app.timezone}")
    public void scheduledRun() {
        runAll();
    }

    public JobRunResult runAll() {
        LocalDate today = LocalDate.now(ZoneId.of(timezone));
        int dues = rentPaymentService.generateDuesForAllActiveTenancies(today);
        int ended = lifecycleService.completeDueVacates(today);
        log.info("Daily jobs for {}: {} rent dues created, {} tenancies ended", today, dues, ended);
        return new JobRunResult(today, dues, ended);
    }

    public record JobRunResult(LocalDate date, int rentDuesCreated, int tenanciesEnded) {}
}