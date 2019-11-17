package com.finalcola.sql.ex;

/**
 * @author: yuanyou.
 * @date: 2019-11-11 20:07
 */
public class NotSupportYetException extends RuntimeException {

    /**
     * Instantiates a new Not support yet exception.
     */
    public NotSupportYetException() {
        super();
    }

    /**
     * Instantiates a new Not support yet exception.
     *
     * @param message the message
     */
    public NotSupportYetException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Not support yet exception.
     *
     * @param message the message
     * @param cause   the cause
     */
    public NotSupportYetException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Instantiates a new Not support yet exception.
     *
     * @param cause the cause
     */
    public NotSupportYetException(Throwable cause) {
        super(cause);
    }
}
