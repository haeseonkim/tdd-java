package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class PointControllerIntegrationTest {

    @Autowired
    private PointServiceFacade pointServiceFacade;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        long userId = 1L;
        long userId2 = 2L;
        pointServiceFacade.chargeUserPoint(userId, 1000);
        pointServiceFacade.chargeUserPoint(userId2, 1000);
    }

    @Test
    void pointTest() throws Exception {
        // given
        long userId = 1L;

        // when & then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId));
    }

    @Test
    void historyTest() throws Exception {
        // given
        long userId = 1L;

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void 충전_서비스_호출() throws Exception {
        // given
        long userId = 1L;
        long amount = 500L;
        long initPoint = pointServiceFacade.getUserPoint(userId).point();

        // 단순 숫자를 요청 바디로 전달
        String plainRequestBody = String.valueOf(amount);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입은 그대로 유지
                        .content(plainRequestBody))             // 단순 숫자 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(initPoint + amount)); // 결과 값 검증
    }

    @Test
    void 포인트_사용_서비스_호출() throws Exception {
        // given
        long userId = 1L;
        long amount = 500L;
        long initPoint = pointServiceFacade.getUserPoint(userId).point();

        // 단순 숫자를 요청 바디로 전달
        String plainRequestBody = String.valueOf(amount);

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입은 그대로 유지
                        .content(plainRequestBody))             // 단순 숫자 전달
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.point").value(initPoint - amount)); // 결과 값 검증
    }

    @Nested
    @DisplayName("동시성 테스트")
    class ConcurrencyTest {
        @Test
        void 동시에_여러개_스레드_충전_시도() throws Exception {
            // given
            long userId = 1L;
            int numberOfThreads = 10;
            long amount = 100L;
            long initPoint = pointServiceFacade.getUserPoint(userId).point();

            // 스레드 풀 생성
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            // 진행 중인 스레드 갯수 관리
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            // when
            for(int i = 0; i < numberOfThreads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.chargeUserPoint(userId, amount);
                    latch.countDown();
                });
            }

            // 모든 스레드가 끝날때까지 기다림
            latch.await();
            executorService.shutdown();

            // then
            // 포인트 상태 확인
            UserPoint userPoint = pointServiceFacade.getUserPoint(userId);
            assertEquals(initPoint + (amount * numberOfThreads), userPoint.point());
        }

        @Test
        void 동시에_여러개_스레드_포인트_사용_시도() throws Exception {
            // given
            long userId = 1L;
            int numberOfThreads = 10;
            long amount = 100L;
            long initPoint = pointServiceFacade.getUserPoint(userId).point();

            // 스레드 풀 생성
            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            // 진행 중인 스레드 갯수 관리
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            // when
            for(int i = 0; i < numberOfThreads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.unChargeUserPoint(userId, amount);
                    latch.countDown();
                });
            }

            // 모든 스레드가 끝날때까지 기다림
            latch.await();
            executorService.shutdown();

            // then
            // 포인트 상태 확인
            UserPoint userPoint = pointServiceFacade.getUserPoint(userId);
            assertEquals(initPoint - (amount * numberOfThreads), userPoint.point());
        }

        @Test
        void 동시에_충전_사용_시도() throws InterruptedException {
            // given
            long userId = 1L;
            int chargeThreads = 5;
            int useThreads = 4;
            int totalThreads = chargeThreads + useThreads;
            long chargeAmount = 100L;
            long useAmount = 50L;
            long initPoint = pointServiceFacade.getUserPoint(userId).point();

            // 스레드 풀 생성
            ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
            // 진행 중인 스레드 갯수 관리
            CountDownLatch latch = new CountDownLatch(totalThreads);

            // when
            for(int i = 0; i < chargeThreads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.chargeUserPoint(userId, chargeAmount);
                    System.out.println("Charged: " + chargeAmount);
                    latch.countDown();
                });
            }

            for(int i = 0; i < useThreads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.unChargeUserPoint(userId, useAmount);
                    System.out.println("used: " + useAmount);
                    latch.countDown();
                });
            }

            // 모든 스레드가 끝날때까지 기다림
            latch.await();
            executorService.shutdown();

            // then
            // 포인트 상태 확인
            UserPoint userPoint = pointServiceFacade.getUserPoint(userId);
            assertEquals(initPoint + (chargeAmount * chargeThreads) - (useAmount * useThreads), userPoint.point());
        }

        @Test
        void 여러_사용자_동시_요청() throws Exception {
            // given
            long userId1 = 1L;
            long userId2 = 2L;
            int user1Threads = 10;
            int user2Threads = 5;
            int totalThreads = user1Threads + user2Threads;
            long amount1 = 100L;
            long amount2 = 50L;
            long initPoint1 = pointServiceFacade.getUserPoint(userId1).point();
            long initPoint2 = pointServiceFacade.getUserPoint(userId2).point();

            // 스레드 풀 생성
            ExecutorService executorService = Executors.newFixedThreadPool(totalThreads);
            // 진행 중인 스레드 갯수 관리
            CountDownLatch latch = new CountDownLatch(totalThreads);

            // when
            for(int i = 0; i < user1Threads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.chargeUserPoint(userId1, amount1);
                    System.out.println("user1: " + amount1);
                    latch.countDown();
                });
            }

            for(int i = 0; i < user2Threads; i++){
                executorService.execute(() -> {
                    pointServiceFacade.unChargeUserPoint(userId2, amount2);
                    System.out.println("user2: " + amount2);
                    latch.countDown();
                });
            }

            // 모든 스레드가 끝날때까지 기다림
            latch.await();
            executorService.shutdown();

            // then
            // 포인트 상태 확인
            UserPoint userPoint1 = pointServiceFacade.getUserPoint(userId1);
            UserPoint userPoint2 = pointServiceFacade.getUserPoint(userId2);
            assertEquals(initPoint1 + (user1Threads * amount1), userPoint1.point());
            assertEquals(initPoint2 - (user2Threads * amount2), userPoint2.point());
        }
    }
}
