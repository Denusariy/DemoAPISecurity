package ru.denusariy.demoapisecurity.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class UserNotFoundException extends RuntimeException {
    private final String message = "Пользователь с данным email не найден!";
    private final HttpStatus status = HttpStatus.NOT_FOUND;
}
