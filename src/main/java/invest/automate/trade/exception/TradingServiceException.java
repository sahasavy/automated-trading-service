package invest.automate.trade.exception;

/**
 * Custom exception class for business logic errors.
 */
public class TradingServiceException extends RuntimeException {
    public TradingServiceException(String message) {
        super(message);
    }

    public TradingServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
