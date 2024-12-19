package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class InputValidator {
    public void checkInputValue(Long userId){
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }
    }

    public void checkInputValue(Long userId, Long newPoint){
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }

        if (newPoint == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }

        if (newPoint <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
    }
}
