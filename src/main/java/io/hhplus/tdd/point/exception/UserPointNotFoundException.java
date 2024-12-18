package io.hhplus.tdd.point.exception;

public class UserPointNotFoundException extends RuntimeException {
    public UserPointNotFoundException(long userId) {
        super("UserPoint with ID " + userId + " not found");
    }
}
