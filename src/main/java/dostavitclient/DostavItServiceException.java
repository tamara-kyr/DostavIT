package dostavitclient;

public class DostavItServiceException extends Exception {

    public DostavItServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DostavItServiceException(String message) {
        super(message);
    }

    public DostavItServiceException(Throwable cause) {
        super(cause);
    }

    public DostavItServiceException() {
        super();
    }
}
