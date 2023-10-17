package com.system205.telegram.exceptions;

public class SendMessageException extends RuntimeException {
    public SendMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
