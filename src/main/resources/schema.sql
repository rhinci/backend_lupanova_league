CREATE TABLE IF NOT EXISTS teams (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    city        VARCHAR(255) NOT NULL,
    max_players INT NOT NULL CHECK (max_players >= 1)
);