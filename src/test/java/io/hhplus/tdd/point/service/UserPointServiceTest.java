package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.UserPoint;
import io.hhplus.tdd.point.exception.NotEnoughPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserPointServiceTest {

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private UserPointService userPointFacade;

    @Nested
    @DisplayName("유저 포인트 조회 서비스 테스트")
    class GetUserPointTest{
        @Test
        void 유저_포인트_조회시_없을때_UserPointNotFoundException() {
            // given
            long userId = 1L;
            when(userPointTable.selectById(userId)).thenReturn(null);

            // when & then
            assertThrows(UserPointNotFoundException.class, () -> userPointFacade.getUserPoint(userId));
        }

        @Test
        void 유저_포인트_조회시_있으면_성공(){
            // given
            UserPoint userPoint = UserPoint.empty(1L);
            when(userPointTable.selectById(1L)).thenReturn(userPoint);

            // when
            UserPoint actual = userPointFacade.getUserPoint(1L);

            // then
            assertEquals(userPoint, actual);
        }
    }

    @Nested
    @DisplayName("포인트 충전 서비스 테스트")
    class ChargeUserPointTest{
        @Test
        void 포인트_충전_한도초과_PointLimitExceededException() {
            // given
            long userId = 1L;
            long newPoint = 100 * 100 * 101L;
            UserPoint userPoint = UserPoint.empty(userId);
            when(userPointTable.selectById(userId)).thenReturn(userPoint);

            // when & then
            assertThrows(PointLimitExceededException.class, () -> userPointFacade.chargeUserPoint(userId, newPoint));
        }

        @Test
        void 포인트_충전_null_일때_insert_성공() {
            // given
            long userId = 1L;
            long newPoint = 100L;
            UserPoint expectedUserPoint = new UserPoint(userId, newPoint, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(null);
            when(userPointTable.insertOrUpdate(userId, newPoint)).thenReturn(expectedUserPoint);

            // when
            UserPoint actual = userPointFacade.chargeUserPoint(userId, newPoint);

            // then
            assertEquals(expectedUserPoint.id(), actual.id());
            assertEquals(expectedUserPoint.point(), actual.point());
        }

        @Test
        void 포인트_충전_update_성공(){
            // given
            long userId = 1L;
            long originalPoint = 100L;
            long newPoint = 200L;
            UserPoint userPoint = new UserPoint(userId, originalPoint, System.currentTimeMillis());
            UserPoint expectedUserPoint = new UserPoint(userId, originalPoint + newPoint, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(userId, originalPoint + newPoint)).thenReturn(expectedUserPoint);

            // when
            UserPoint actual = userPointFacade.chargeUserPoint(userId, newPoint);

            // then
            assertEquals(expectedUserPoint.id(), actual.id());
            assertEquals(expectedUserPoint.point(), actual.point());
        }
    }

    @Nested
    @DisplayName("포인트 사용 서비스 테스트")
    class UnChargeUserPointTest {
        @Test
        void 포인트_사용_포인트_null이면_UserPointNotFoundException(){
            // given
            long userId = 1L;
            when(userPointTable.selectById(userId)).thenReturn(null);

            // when & then
            assertThrows(UserPointNotFoundException.class, () -> userPointFacade.unChargeUserPoint(userId, 100L));
        }

        @Test
        void 포인트_사용_포인트_모자라면_NotEnoughPointException(){
            // given
            long userId = 1L;
            long originPoint = 50L;
            long usePoint = 100L;
            UserPoint userPoint = new UserPoint(userId, originPoint, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(userPoint);

            // when & then
            assertThrows(NotEnoughPointException.class, () -> userPointFacade.unChargeUserPoint(userId, usePoint));
        }

        @Test
        void 포인트_사용_성공(){
            // given
            long userId = 1L;
            long originPoint = 150L;
            long usePoint = 100L;
            UserPoint userPoint = new UserPoint(userId, originPoint, System.currentTimeMillis());
            UserPoint expectedUserPoint = new UserPoint(userId, originPoint - usePoint, System.currentTimeMillis());
            when(userPointTable.selectById(userId)).thenReturn(userPoint);
            when(userPointTable.insertOrUpdate(userId, originPoint - usePoint)).thenReturn(expectedUserPoint);

            // when
            UserPoint actual = userPointFacade.unChargeUserPoint(userId, usePoint);

            // then
            assertEquals(expectedUserPoint.id(), actual.id());
            assertEquals(expectedUserPoint.point(), actual.point());
        }
    }
}
