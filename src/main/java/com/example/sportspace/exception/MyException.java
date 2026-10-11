package com.example.sportspace.exception;
import org.springframework.http.HttpStatus;
public class MyException extends RuntimeException {
    private final HttpStatus status;
    public MyException(HttpStatus status, String message) { super(message); this.status = status; }
    public HttpStatus getStatus() { return status; }
}
