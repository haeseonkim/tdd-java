package io.hhplus.tdd.point.exception;

public class NotEnoughPointException extends RuntimeException {
    public NotEnoughPointException(long userId) {
        super("UserPoint with ID " + userId + " is not enough");
    }
}
