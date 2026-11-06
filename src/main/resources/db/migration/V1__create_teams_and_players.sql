CREATE TABLE teams (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    city        VARCHAR(255) NOT NULL,
    max_players INT NOT NULL CHECK (max_players >= 1)
);

CREATE TABLE players (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    team_id    BIGINT NOT NULL REFERENCES teams (id) ON DELETE CASCADE,
    full_name  VARCHAR(255) NOT NULL,
    number     INT NOT NULL CHECK (number >= 1),
    birth_date DATE NOT NULL,
    horse_name VARCHAR(100)
);