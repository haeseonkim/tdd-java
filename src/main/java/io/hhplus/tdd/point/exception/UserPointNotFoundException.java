package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.ErrorResponse;

public class UserPointNotFoundException extends RuntimeException {
    private final long userId;

    public UserPointNotFoundException(long userId) {
        this.userId = userId;
    }

    public ErrorResponse toErrorResponse() {
        return new ErrorResponse("404", "User point not found for userId: " + userId);
    }
}
