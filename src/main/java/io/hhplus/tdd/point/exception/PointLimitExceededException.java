package io.hhplus.tdd.point.exception;

public class PointLimitExceededException extends RuntimeException {
    public PointLimitExceededException(long userId) {
        super("UserPoint with ID " + userId + " exceeded limit");
    }
}
