ALTER TABLE players
    ADD CONSTRAINT uq_players_team_number UNIQUE (team_id, number);