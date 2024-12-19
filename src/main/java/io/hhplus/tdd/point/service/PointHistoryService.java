package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryTable pointHistoryTable;
    private final ConcurrentHashMap<Long, ReentrantLock> pointHistoryLock = new ConcurrentHashMap<>();

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public void savePointHistory(long userId, long point, TransactionType transactionType) {
        ReentrantLock lock = pointHistoryLock.computeIfAbsent(userId, k -> new ReentrantLock());

        try{
            lock.lock();

            pointHistoryTable.insert(userId, point, transactionType, System.currentTimeMillis());
        }finally {
            lock.unlock();
        }
    }
}
