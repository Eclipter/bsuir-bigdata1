package by.bsuir.bigdata.exception;

public class BigDataServiceException extends RuntimeException {

    public BigDataServiceException() {
        super();
    }

    public BigDataServiceException(String message) {
        super(message);
    }

    public BigDataServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BigDataServiceException(Throwable cause) {
        super(cause);
    }

    protected BigDataServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
