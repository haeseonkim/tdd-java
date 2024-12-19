package io.hhplus.tdd.point;

import io.hhplus.tdd.point.service.PointHistoryService;
import io.hhplus.tdd.point.service.UserPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceFacade {
    private final UserPointService userPointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint getUserPoint(long userId) {
        return userPointService.getUserPoint(userId);
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryService.getPointHistory(userId);
    }

    public UserPoint chargeUserPoint(long userId, long newPoint) {
        // 1. 포인트 충전
        UserPoint updatedPoint = userPointService.chargeUserPoint(userId, newPoint);

        // 2. 히스토리 insert
        pointHistoryService.savePointHistory(userId, newPoint, TransactionType.CHARGE);

        return updatedPoint;
    }

    public UserPoint unChargeUserPoint(long userId, long usePoint) {
        // 1. 포인트 사용
        UserPoint updatedPoint = userPointService.unChargeUserPoint(userId, usePoint);

        // 2. 히스토리 insert
        pointHistoryService.savePointHistory(userId, usePoint, TransactionType.USE);

        return updatedPoint;
    }



}
