package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(value = "ru.yandex.practicum.filmorate")
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.warn("Ошибка валидации: {}", e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // 404
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.warn("Объект не найден: {}", e.getMessage());
        return new ErrorResponse("error", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Внутренняя ошибка сервера: ", e);
        return new ErrorResponse("error", "Внутренняя ошибка сервера");
    }
}

