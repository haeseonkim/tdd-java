package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.ErrorResponse;

public class NotEnoughPointException extends RuntimeException {
    private final long userId;

    public NotEnoughPointException(long userId) {
        this.userId = userId;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse("400", "Not enough points for userId: " + userId);
    }
}
