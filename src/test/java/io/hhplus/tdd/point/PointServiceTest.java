package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    @Nested
    @DisplayName("유저 포인트 조회 서비스 테스트")
    class GetUserPointTest{
        @Test
        void 유저_포인트_조회시_없을때_UserPointNotFoundException() {
            // given
            long userId = 1L;
            when(userPointTable.selectById(userId)).thenReturn(null);

            // when & then
            assertThrows(UserPointNotFoundException.class, () -> pointService.getUserPoint(userId));
        }

        @Test
        void 유저_포인트_조회시_있으면_성공(){
            // given
            UserPoint userPoint = UserPoint.empty(1L);
            when(userPointTable.selectById(1L)).thenReturn(userPoint);

            // when
            UserPoint actual = pointService.getUserPoint(1L);

            // then
            assertEquals(userPoint, actual);
        }
    }

    @Nested
    @DisplayName("유저 포인트 히스토리 조회 서비스 테스트")
    class GetPointHistoryTest{
        @Test
        void 유저_포인트_히스토리_조회_성공() {
            // given
            long userId = 1L;
            List<PointHistory> expectedHistories = List.of(
                    new PointHistory(1L, userId, 1000L, TransactionType.CHARGE, System.currentTimeMillis()),
                    new PointHistory(2L, userId, 500L, TransactionType.USE, System.currentTimeMillis())
            );
            when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(expectedHistories);

            // when
            List<PointHistory> actualHistories = pointService.getPointHistory(userId);

            // then
            assertEquals(expectedHistories, actualHistories);
        }

        @Test
        void 유저_포인트_히스토리_조회_빈값_반환_성공(){
            // given
            long userId = 1L;
            when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(List.of());

            // when
            List<PointHistory> actualHistories = pointService.getPointHistory(userId);

            // then
            assertTrue(actualHistories.isEmpty());
        }
    }
}
