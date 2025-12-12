CREATE DATABASE fp_pbo;

USE fp_pbo;

CREATE TABLE players (
    player_id INT AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    total_wins INT DEFAULT 0,
    total_games INT DEFAULT 0,
    best_time DOUBLE DEFAULT NULL,
    CONSTRAINT players_pk PRIMARY KEY (player_id)
);

CREATE TABLE difficulties (
    difficulty_id INT AUTO_INCREMENT,
    difficulty_name VARCHAR(20) NOT NULL UNIQUE,
    rows_count INT NOT NULL,
    cols_count INT NOT NULL,
    mines_count INT NOT NULL,
    CONSTRAINT difficulty_pk PRIMARY KEY (difficulty_id)
);

CREATE TABLE games (
    game_id INT AUTO_INCREMENT,
    player_id INT NOT NULL,
    difficulty_id INT NOT NULL,
    duration_seconds DOUBLE NOT NULL,
    result ENUM('WIN','LOSE') NOT NULL,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT games_pk PRIMARY KEY (game_id)
);

ALTER TABLE games ADD CONSTRAINT games_playerid_fk FOREIGN KEY (player_id) REFERENCES players(player_id);
ALTER TABLE games ADD CONSTRAINT games_difficultyid_fk FOREIGN KEY (difficulty_id) REFERENCES difficulties(difficulty_id);

CREATE TABLE multiplayer_games (
    mp_game_id INT AUTO_INCREMENT,
    difficulty_id INT NOT NULL,
    winner_player_id INT,
    played_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT mp_games_pk PRIMARY KEY (mp_game_id)
);

ALTER TABLE multiplayer_games ADD CONSTRAINT mp_playersid_fk FOREIGN KEY (winner_player_id) REFERENCES players(player_id);
ALTER TABLE multiplayer_games ADD CONSTRAINT mp_difficultyid_fk FOREIGN KEY (difficulty_id) REFERENCES difficulties(difficulty_id);

CREATE TABLE multiplayer_participants (
    mp_game_id INT NOT NULL,
    player_id INT NOT NULL,
    elimination_order INT,
    time_alive_seconds DOUBLE,

    CONSTRAINT mp_game_id_pk PRIMARY KEY (mp_game_id, player_id)
);

ALTER TABLE multiplayer_participants ADD CONSTRAINT mp_game_id_fk FOREIGN KEY (mp_game_id) REFERENCES multiplayer_games(mp_game_id);
ALTER TABLE multiplayer_participants ADD CONSTRAINT mp_player_id_fk FOREIGN KEY (player_id) REFERENCES players(player_id);


INSERT INTO difficulties (difficulty_name, rows_count, cols_count, mines_count)
VALUES
('EASY', 8, 8, 10),
('MEDIUM', 14, 14, 40),
('HARD', 20, 20, 99);
