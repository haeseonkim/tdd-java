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
        // 입력값 확인
        checkInputValue(userId);

        UserPoint userPoint = userPointTable.selectById(userId);
        if(userPoint == null) {
            throw new UserPointNotFoundException(userId);
        }
        return userPoint;
    }

    public List<PointHistory> getPointHistory(Long userId) {
        // 입력값 확인
        checkInputValue(userId);

        return pointHistoryTable.selectAllByUserId(userId);
    }

    public UserPoint chargeUserPoint(Long userId, Long newPoint) {
        // 1. 입력값 확인
        checkInputValue(userId, newPoint);

        // 2. 포인트 충전
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

        // 3. 히스토리 insert
        pointHistoryTable.insert(userId, newPoint, TransactionType.CHARGE, System.currentTimeMillis());

        return updatedPoint;
    }

    public UserPoint unChargeUserPoint(Long userId, Long newPoint) {
        // 1. 입력값 확인
        checkInputValue(userId, newPoint);

        // 2. 포인트 사용
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

        // 3. 히스토리 insert
        pointHistoryTable.insert(userId, newPoint, TransactionType.CHARGE, System.currentTimeMillis());

        return updatedPoint;
    }

    private void checkInputValue(Long userId){
        if(userId == null){
            throw new IllegalArgumentException("userId cannot be null");
        }
    }

    private void checkInputValue(Long userId, Long newPoint){
        if (userId == null || newPoint == null) {
            throw new IllegalArgumentException("userId or amount cannot be null");
        }

        if (newPoint <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }

        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }
    }

}
