package ru.denusariy.demoapisecurity.domain.dto.response.errors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public class ErrorResponseDTO {
    private final HttpStatus status;
    private final String message;
}
