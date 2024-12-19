package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.NotEnoughPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public static final long MAXIMUM_POINT_LIMIT = 100 * 100 * 100;

    public UserPoint getUserPoint(Long userId) {
        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint == null) {
            throw new UserPointNotFoundException(userId);
        }
        return userPoint;
    }

    public List<PointHistory> getPointHistory(Long userId) {
        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargeUserPoint(Long userId, Long newPoint) {
        // 1. 포인트 충전
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

        // 2. 히스토리 insert
        pointHistoryTable.insert(userId, newPoint, TransactionType.CHARGE, System.currentTimeMillis());

        return updatedPoint;
    }

    public UserPoint unChargeUserPoint(Long userId, Long newPoint) {
        // 1. 포인트 사용
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

        // 2. 히스토리 insert
        pointHistoryTable.insert(userId, newPoint, TransactionType.USE, System.currentTimeMillis());

        return updatedPoint;
    }



}
