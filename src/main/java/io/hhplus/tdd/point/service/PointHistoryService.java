package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import io.hhplus.tdd.point.lock.LockManager;
import io.hhplus.tdd.point.lock.LockWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryTable pointHistoryTable;
    private final LockManager pointHistoryLock = new LockManager();

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public void savePointHistory(long userId, long point, TransactionType transactionType) {
        LockWrapper lock = pointHistoryLock.getLock(userId);

        try{
            lock.lock();

            pointHistoryTable.insert(userId, point, transactionType, System.currentTimeMillis());
        }finally {
            lock.unlock();
        }
    }
}
