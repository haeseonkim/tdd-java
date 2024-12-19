package io.hhplus.tdd.point;

import io.hhplus.tdd.point.facade.PointHistoryFacade;
import io.hhplus.tdd.point.facade.UserPointFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointFacade userPointFacade;
    private final PointHistoryFacade pointHistoryFacade;

    public UserPoint getUserPoint(long userId) {
        return userPointFacade.getUserPoint(userId);
    }

    public List<PointHistory> getPointHistory(long userId) {
        return pointHistoryFacade.getPointHistory(userId);
    }

    public UserPoint chargeUserPoint(long userId, long newPoint) {
        // 1. 포인트 충전
        UserPoint updatedPoint = userPointFacade.chargeUserPoint(userId, newPoint);

        // 2. 히스토리 insert
        pointHistoryFacade.savePointHistory(userId, newPoint, TransactionType.CHARGE);

        return updatedPoint;
    }

    public UserPoint unChargeUserPoint(long userId, long usePoint) {
        // 1. 포인트 사용
        UserPoint updatedPoint = userPointFacade.unChargeUserPoint(userId, usePoint);

        // 2. 히스토리 insert
        pointHistoryFacade.savePointHistory(userId, usePoint, TransactionType.USE);

        return updatedPoint;
    }



}
