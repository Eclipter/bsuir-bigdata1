package by.bsuir.bigdata.aggregation.exception;

public class AggregationException extends RuntimeException {

    public AggregationException() {
        super();
    }

    public AggregationException(String message) {
        super(message);
    }

    public AggregationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AggregationException(Throwable cause) {
        super(cause);
    }

    protected AggregationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
