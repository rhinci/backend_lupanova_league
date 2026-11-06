package com.hobbyhorse.league.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.hobbyhorse.league.model.Player;

@Repository
public class PlayerRepository {

    private static final RowMapper<Player> PLAYER_MAPPER = (rs, rowNum) -> {
        Player player = new Player();
        player.setId(rs.getLong("id"));
        player.setTeamId(rs.getLong("team_id"));
        player.setFullName(rs.getString("full_name"));
        player.setNumber(rs.getInt("number"));
        player.setBirthDate(rs.getObject("birth_date", LocalDate.class));
        player.setHorseName(rs.getString("horse_name"));
        return player;
    };

    private final JdbcTemplate jdbc;

    public PlayerRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Player save(Player player) {
        Long id = jdbc.queryForObject(
                "INSERT INTO players (team_id, full_name, number, birth_date, horse_name) "
                        + "VALUES (?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                player.getTeamId(), player.getFullName(), player.getNumber(),
                player.getBirthDate(), player.getHorseName());
        player.setId(id);
        return player;
    }

    public List<Player> findByTeamId(Long teamId) {
        return jdbc.query(
                "SELECT id, team_id, full_name, number, birth_date, horse_name "
                        + "FROM players WHERE team_id = ? ORDER BY id",
                PLAYER_MAPPER, teamId);
    }

    public int countByTeamId(Long teamId) {
        Integer count = jdbc.queryForObject(
                "SELECT count(*) FROM players WHERE team_id = ?",
                Integer.class, teamId);
        return count == null ? 0 : count;
    }
}