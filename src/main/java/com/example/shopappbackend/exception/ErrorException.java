package com.example.shopappbackend.exception;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorException<T> {
    private Date timestamp;
    private T message;
    private String details;

    public ErrorException(T message, String details) {
        this.timestamp = new Date();
        this.message = message;
        this.details = details;
    }
}
