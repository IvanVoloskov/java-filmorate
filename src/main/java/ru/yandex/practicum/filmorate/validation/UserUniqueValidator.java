package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@Component
public class UserUniqueValidator {

    private final JdbcTemplate jdbcTemplate;

    public UserUniqueValidator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void validateEmailUnique(String email, Integer currentUserId) {
        if (email == null || email.isBlank()) {
            return;
        }

        try {
            String sql;
            Integer count;

            if (currentUserId == null) {
                sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
                count = jdbcTemplate.queryForObject(sql, Integer.class, email.trim());
            } else {
                // Проверка для обновления существующего пользователя
                sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?) AND user_id != ?";
                count = jdbcTemplate.queryForObject(sql, Integer.class, email.trim(), currentUserId);
            }

            if (count != null && count > 0) {
                log.warn("Попытка использовать уже занятый email: {}", email);
                throw new ValidationException("Этот email уже используется");
            }
        } catch (DuplicateKeyException e) {
            log.warn("Нарушение уникальности email в БД: {}", email);
            throw new ValidationException("Этот email уже используется");
        }
    }
}