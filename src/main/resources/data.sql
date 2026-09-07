INSERT INTO friends_status (id, name) VALUES
                                          (1, 'IGNORED'),
                                          (2, 'ACCEPTED');

INSERT INTO mpa_ratings (id, name) VALUES
                                       (1, 'G'),
                                       (2, 'PG'),
                                       (3, 'PG-13'),
                                       (4, 'R'),
                                       (5, 'NC-17');

INSERT INTO genres (id, name) VALUES
                                  (1, 'Комедия'),
                                  (2, 'Драма'),
                                  (3, 'Мультфильм'),
                                  (4, 'Триллер'),
                                  (5, 'Документальный'),
                                  (6, 'Боевик');

INSERT INTO users (email, login, name, birthday) VALUES
                                                     ('ivan@example.com', 'ivan_login', 'Ivan', '1990-05-10'),
                                                     ('anna@example.com', 'anna_login', 'Anna', '1992-08-20'),
                                                     ('petr@example.com', 'petr_login', 'Petr', '1988-03-15');

INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES
                                                                                 ('Inception', 'A thief who steals information from the subconscious during the dream state is given a task to plant an idea into the mind of a CEO.', '2010-07-16', 148, 4),
                                                                                 ('The Godfather', 'The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.', '1972-03-24', 175, 4),
                                                                                 ('La La Land', 'While navigating their careers in Los Angeles, a jazz pianist and an aspiring actress fall in love while pursuing their dreams.', '2016-12-09', 128, 2),
                                                                                 ('Interstellar', 'A team of explorers travel through a wormhole in space in an attempt to ensure humanity''s survival.', '2014-11-07', 169, 4),
                                                                                 ('The Dark Knight', 'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.', '2008-07-18', 152, 4),
                                                                                 ('Pulp Fiction', 'The lives of two mob hitmen, a boxer, a gangster and his wife intertwine in four tales of violence and redemption.', '1994-10-14', 154, 4),
                                                                                 ('Forrest Gump', 'The presidencies of Kennedy and Johnson, the Vietnam War, the Watergate scandal and other historical events unfold from the perspective of an Alabama man with an IQ of 75.', '1994-07-06', 142, 3),
                                                                                 ('The Matrix', 'A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.', '1999-03-31', 136, 4),
                                                                                 ('Fight Club', 'An insomniac office worker and a devil-may-care soap maker form an underground fight club that evolves into much more.', '1999-10-15', 139, 4);

INSERT INTO film_genres (film_id, genre_id) VALUES (1, 5), (1, 3);
INSERT INTO film_genres (film_id, genre_id) VALUES (2, 2);
INSERT INTO film_genres (film_id, genre_id) VALUES (3, 1), (3, 2);
INSERT INTO film_genres (film_id, genre_id) VALUES (4, 5);
INSERT INTO film_genres (film_id, genre_id) VALUES
                                                ((SELECT id FROM films WHERE name = 'The Dark Knight'), 2),
                                                ((SELECT id FROM films WHERE name = 'The Dark Knight'), 4),
                                                ((SELECT id FROM films WHERE name = 'Pulp Fiction'), 2),
                                                ((SELECT id FROM films WHERE name = 'Pulp Fiction'), 6),
                                                ((SELECT id FROM films WHERE name = 'Forrest Gump'), 2),
                                                ((SELECT id FROM films WHERE name = 'Forrest Gump'), 1),
                                                ((SELECT id FROM films WHERE name = 'The Matrix'), 5),
                                                ((SELECT id FROM films WHERE name = 'The Matrix'), 6),
                                                ((SELECT id FROM films WHERE name = 'Fight Club'), 2),
                                                ((SELECT id FROM films WHERE name = 'Fight Club'), 6);

INSERT INTO friends (user_id, friend_id, friends_status_id) VALUES (1, 2, 1);
INSERT INTO friends (user_id, friend_id, friends_status_id) VALUES (2, 1, 1); -- двусторонняя связь
INSERT INTO friends (user_id, friend_id, friends_status_id) VALUES (3, 1, 2);

INSERT INTO likes (user_id, film_id) VALUES (1, 1);
INSERT INTO likes (user_id, film_id) VALUES (1, 2);
INSERT INTO likes (user_id, film_id) VALUES (2, 3);
