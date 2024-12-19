package io.hhplus.tdd.point;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        long userId = 1L;
        pointServiceFacade.chargeUserPoint(userId, 1000);
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

    @Nested
    @DisplayName("포인트 충전 요청 테스트")
    class ChargeTest{

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
    }
}
