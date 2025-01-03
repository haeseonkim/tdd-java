package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

@Component
public class InputValidator {
    public void checkInputValue(Long userId){
        if (userId <= 0) {
            throw new IllegalArgumentException("userId must be greater than 0");
        }
    }

    public void checkInputValue(Long userId, Long amount){
        checkInputValue(userId);

        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be greater than 0");
        }
    }
}
