package com.s58703.demo.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String massage) {
        super(massage);
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
