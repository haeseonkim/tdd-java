package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.NotEnoughPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointTable userPointTable;
    private final ConcurrentHashMap<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public static final long MAXIMUM_POINT_LIMIT = 100 * 100 * 100;

    public UserPoint getUserPoint(long userId) {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());
        try{
            lock.lock();

            UserPoint userPoint = userPointTable.selectById(userId);
            if(userPoint == null) {
                throw new UserPointNotFoundException(userId);
            }

            return userPoint;

        }finally {
            lock.unlock();
        }
    }

    public UserPoint chargeUserPoint(long userId, long newPoint) {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());

        try{
            lock.lock();

            UserPoint userPoint = userPointTable.selectById(userId);
            UserPoint updatedPoint;

            if (userPoint == null) {
                updatedPoint = userPointTable.insertOrUpdate(userId, newPoint);
            } else {
                long totalPoint = userPoint.point() + newPoint;
                if (totalPoint > MAXIMUM_POINT_LIMIT) {
                    throw new PointLimitExceededException(userId);
                }
                updatedPoint = userPointTable.insertOrUpdate(userId, totalPoint);
            }

            return updatedPoint;
        } finally {
            lock.unlock();
        }

    }

    public UserPoint unChargeUserPoint(long userId, long newPoint) {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, k -> new ReentrantLock());

        try{
            lock.lock();

            UserPoint userPoint = userPointTable.selectById(userId);
            UserPoint updatedPoint;
            if (userPoint == null) {
                throw new UserPointNotFoundException(userId);
            }else {
                long totalPoint = userPoint.point() - newPoint;
                if(totalPoint < 0) {
                    throw new NotEnoughPointException(userId);
                }
                updatedPoint = userPointTable.insertOrUpdate(userId, totalPoint);
            }

            return updatedPoint;
        } finally {
            lock.unlock();
        }

    }
}
