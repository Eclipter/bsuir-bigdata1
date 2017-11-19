package by.bsuir.bigdata.youtube.exception;

public class YoutubeException extends RuntimeException {

    public YoutubeException() {
        super();
    }

    public YoutubeException(String message) {
        super(message);
    }

    public YoutubeException(String message, Throwable cause) {
        super(message, cause);
    }

    public YoutubeException(Throwable cause) {
        super(cause);
    }

    protected YoutubeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
