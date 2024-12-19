package io.hhplus.tdd.point.lock;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LockScheduler {
    private final LockManager lockManager;

    @Scheduled(fixedRate = 600000, initialDelay = 5000)
    public void cleanUpLocks(){
        lockManager.cleanUpLocks();
    }
}
