package com.lifegame.mvp.schedule;

import com.lifegame.mvp.service.RankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankScheduler {

    private final RankService rankService;

    @Scheduled(cron = "0 0 * * * *")
    public void refreshCurrentWeekRank() {
        log.info("Refreshing current week rank...");
        rankService.refreshCurrentWeekRank();
    }

    @Scheduled(cron = "0 0 0 * * MON")
    public void snapshotLastWeekRank() {
        log.info("Snapshotting last week rank...");
        rankService.snapshotLastWeekRank();
    }
}
