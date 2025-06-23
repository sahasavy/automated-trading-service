package invest.automate.trade.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Handles application-level exceptions globally.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TradingServiceException.class)
    public ResponseEntity<String> handleTradingException(TradingServiceException e) {
        log.error("Trading exception occurred", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity.internalServerError().body("Unexpected error occurred");
    }
}
