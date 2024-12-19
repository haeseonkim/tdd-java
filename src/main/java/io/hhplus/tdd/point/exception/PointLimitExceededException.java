package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.ErrorResponse;

public class PointLimitExceededException extends RuntimeException {
    private final long userId;

    public PointLimitExceededException(long userId) {
        this.userId = userId;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse("400", "Point limit exceeded for userId: " + userId);
    }
}
