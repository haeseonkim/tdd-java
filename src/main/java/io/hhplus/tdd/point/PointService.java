package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public static final long MAXIMUM_POINT_LIMIT = 100 * 100 * 100;

    public void getUserPoint(Long userId) {}
    public void getPointHistory(Long userId) {}
    public void chargeUserPoint(Long userId, Long newPoint) {}
    public void unChargeUserPoint(Long userId, Long newPoint) {}
}
