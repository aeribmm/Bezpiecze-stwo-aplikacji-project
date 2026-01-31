package com.s58703.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    private int status;
    private String message;
    private Map<String, String> errors; // Для валидационных ошибок
    private LocalDateTime timestamp;

    // Конструктор для простых ошибок (без Map)
    public ErrorResponse(int status, String message, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.timestamp = timestamp;
    }
}