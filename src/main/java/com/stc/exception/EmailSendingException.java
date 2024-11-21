package com.stc.exception;

public class EmailSendingException  extends RuntimeException{
    public EmailSendingException(String message) {
        super(message);
    }
}
