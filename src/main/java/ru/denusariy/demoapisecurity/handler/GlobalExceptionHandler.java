package ru.denusariy.demoapisecurity.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.denusariy.demoapisecurity.domain.dto.response.errors.ErrorResponseDTO;
import ru.denusariy.demoapisecurity.exception.ItemNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handle(ItemNotFoundException e) {
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handle(UsernameNotFoundException e) {
        return new ErrorResponseDTO(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
