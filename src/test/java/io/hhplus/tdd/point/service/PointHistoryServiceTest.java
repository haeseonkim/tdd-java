package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @InjectMocks
    private PointHistoryService pointHistoryService;

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
            List<PointHistory> actualHistories = pointHistoryService.getPointHistory(userId);

            // then
            assertEquals(expectedHistories, actualHistories);
        }

        @Test
        void 유저_포인트_히스토리_조회_빈값_반환_성공(){
            // given
            long userId = 1L;
            when(pointHistoryTable.selectAllByUserId(userId)).thenReturn(List.of());

            // when
            List<PointHistory> actualHistories = pointHistoryService.getPointHistory(userId);

            // then
            assertTrue(actualHistories.isEmpty());
        }
    }

    @Nested
    @DisplayName("유저 포인트 히스토리 insert 서비스 테스트")
    class SavePointHistoryTest{
        @Test
        void 포인트_히스토리_저장_성공(){
            // given
            long userId = 1L;
            long newPoint = 100L;
            TransactionType transactionType = TransactionType.CHARGE;

            // when
            pointHistoryService.savePointHistory(userId, newPoint, transactionType);

            // then
            verify(pointHistoryTable).insert(
                    eq(userId),
                    eq(newPoint),
                    eq(transactionType),
                    anyLong()
            );
        }
    }
}
