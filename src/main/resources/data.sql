INSERT INTO mpa_ratings (code, name) VALUES
('G', 'General Audiences'),
('PG', 'Parental Guidance Suggested'),
('PG-13', 'Parents Strongly Cautioned'),
('R', 'Restricted'),
('NC-17', 'Adults Only');

INSERT INTO genres (name) VALUES
('Комедия'),
('Драма'),
('Мультфильм'),
('Триллер'),
('Документальный'),
('Боевик');

INSERT INTO users (email, login, name, birthday) VALUES
('user1@test.com', 'user1', 'User One', '1990-01-01'),
('user2@test.com', 'user2', 'User Two', '1995-05-15');

INSERT INTO films (name, description, release_date, duration, mpa_id) VALUES
('Тестовый фильм 1', 'Описание фильма 1', '2020-01-01', 120, 1),
('Тестовый фильм 2', 'Описание фильма 2', '2021-05-15', 90, 2);
