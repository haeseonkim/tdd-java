package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class InputValidatorTest {

    private final InputValidator inputValidator = new InputValidator();

    @Nested
    @DisplayName("userId에 대한 입력값 테스트")
    class CheckInputValueTest{

        @Test
        void userId_값이_0_이하_일때_IllegalArgumentException(){
            // given
            long userId = -1L;

            // when & then
            assertThrows(IllegalArgumentException.class, () -> inputValidator.checkInputValue(userId));
        }

        @Test
        void userId_값이_0_초과_일때_성공(){
            // given
            long userId = 1L;

            // when & then
            assertDoesNotThrow(() -> inputValidator.checkInputValue(userId));
        }
    }

    @Nested
    @DisplayName("userId, newPoint 둘다에 대한 입력값 테스트")
    class BothCheckInputValueTest{
        @Test
        void userId_값이_0_이하_일때_IllegalArgumentException(){
            // given
            long userId = -1L;

            // when & then
            assertThrows(IllegalArgumentException.class, () -> inputValidator.checkInputValue(userId));
        }

        @Test
        void amount_값이_0_이하_일때_IllegalArgumentException(){
            // given
            long amount = -1L;

            // when & then
            assertThrows(IllegalArgumentException.class, () -> inputValidator.checkInputValue(amount));
        }
    }
}
