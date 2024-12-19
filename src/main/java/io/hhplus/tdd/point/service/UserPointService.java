package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.NotEnoughPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import io.hhplus.tdd.point.lock.LockManager;
import io.hhplus.tdd.point.lock.LockWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPointService {
    private final UserPointTable userPointTable;
    private final LockManager userPointLocks = new LockManager();

    public static final long MAXIMUM_POINT_LIMIT = 100_00_00L;

    public UserPoint getUserPoint(long userId) {
        UserPoint userPoint = userPointTable.selectById(userId);
        if (userPoint == null) {
            throw new UserPointNotFoundException(userId);
        }
        return userPoint;
    }

    public UserPoint chargeUserPoint(long userId, long newPoint) {
        return handlePointUpdate(userId, newPoint, true);
    }

    public UserPoint unChargeUserPoint(long userId, long newPoint) {
        return handlePointUpdate(userId, newPoint, false);
    }

    private UserPoint handlePointUpdate(long userId, long pointDelta, boolean isCharge) {
        LockWrapper lock = userPointLocks.getLock(userId);

        try {
            lock.lock();

            UserPoint userPoint = userPointTable.selectById(userId);

            if (isCharge && userPoint == null) {
                return userPointTable.insertOrUpdate(userId, pointDelta);
            }

            validateUserPoint(userId, userPoint, pointDelta, isCharge);

            long updatedPoint = calculateUpdatedPoint(userPoint.point(), pointDelta, isCharge);

            return userPointTable.insertOrUpdate(userId, updatedPoint);
        } finally {
            lock.unlock();
        }
    }

    private void validateUserPoint(long userId, UserPoint userPoint, long pointDelta, boolean isCharge) {
        if (userPoint == null && !isCharge) {
            throw new UserPointNotFoundException(userId);
        }

        if (!isCharge && userPoint.point() < pointDelta) {
            throw new NotEnoughPointException(userId);
        }

        if (isCharge && userPoint != null && userPoint.point() + pointDelta > MAXIMUM_POINT_LIMIT) {
            throw new PointLimitExceededException(userId);
        }
    }

    private long calculateUpdatedPoint(long currentPoint, long pointDelta, boolean isCharge) {
        return isCharge ? currentPoint + pointDelta : currentPoint - pointDelta;
    }
}