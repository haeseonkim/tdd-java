package io.hhplus.tdd;

import io.hhplus.tdd.point.exception.NotEnoughPointException;
import io.hhplus.tdd.point.exception.PointLimitExceededException;
import io.hhplus.tdd.point.exception.UserPointNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class ApiControllerAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = UserPointNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserPointNotFoundException(UserPointNotFoundException e) {
        return ResponseEntity.status(404).body(e.toErrorResponse());
    }

    @ExceptionHandler(value = PointLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handlePointLimitExceededException(PointLimitExceededException e) {
        return ResponseEntity.status(400).body(e.toErrorResponse());
    }

    @ExceptionHandler(value = NotEnoughPointException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughPointException(NotEnoughPointException e) {
        return ResponseEntity.status(400).body(e.toErrorResponse());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(400)
                .body(new ErrorResponse("400", e.getMessage()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity.status(500).body(new ErrorResponse("500", "에러가 발생했습니다."));
    }
}
