package io.hhplus.tdd.point.lock;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LockManager {
    private final ConcurrentHashMap<Long, LockWrapper> locks = new ConcurrentHashMap<>();

    public LockWrapper getLock(long userId) {
        return locks.computeIfAbsent(userId, id -> new LockWrapper());
    }

    public void cleanUpLocks() {
        long now = System.currentTimeMillis();
        Iterator<Map.Entry<Long, LockWrapper>> iterator = locks.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Long, LockWrapper> entry = iterator.next();
            LockWrapper lock = entry.getValue();

            // 마지막 사용이 10분이 지났고, 대기중인 스레드가 없는 유저 락 제거
            if(now - lock.getLastAccessTime() > 60 * 10 * 1000 && !lock.hasQueuedThreads()){
                iterator.remove();
            }
        }
    }
}
