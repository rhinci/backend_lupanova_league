package com.hobbyhorse.league.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.hobbyhorse.league.model.Team;

import java.math.BigDecimal;
import com.hobbyhorse.league.model.TeamStats;

@Repository
public class TeamRepository {

    private static final RowMapper<Team> TEAM_MAPPER = (rs, rowNum) -> {
        Team team = new Team();
        team.setId(rs.getLong("id"));
        team.setName(rs.getString("name"));
        team.setCity(rs.getString("city"));
        team.setMaxPlayers(rs.getInt("max_players"));
        return team;
    };

    private final JdbcTemplate jdbc;

    public TeamRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Team save(Team team) {
        if (team.getId() == null) {
            Long id = jdbc.queryForObject(
                    "INSERT INTO teams (name, city, max_players) VALUES (?, ?, ?) RETURNING id",
                    Long.class,
                    team.getName(), team.getCity(), team.getMaxPlayers());
            team.setId(id);
        } else {
            jdbc.update(
                    "UPDATE teams SET name = ?, city = ?, max_players = ? WHERE id = ?",
                    team.getName(), team.getCity(), team.getMaxPlayers(), team.getId());
        }
        return team;
    }

    public List<Team> findAll() {
        return jdbc.query(
                "SELECT id, name, city, max_players FROM teams ORDER BY id",
                TEAM_MAPPER);
    }

    public List<Team> searchByName(String query) {
        String escaped = query
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return jdbc.query(
                "SELECT id, name, city, max_players FROM teams WHERE name ILIKE ? ORDER BY id",
                TEAM_MAPPER, "%" + escaped + "%");
    }

    public Optional<Team> findById(Long id) {
        return jdbc.query(
                "SELECT id, name, city, max_players FROM teams WHERE id = ?",
                TEAM_MAPPER, id)
                .stream()
                .findFirst();
    }

    public Optional<Team> findByIdForUpdate(Long id) {
        return jdbc.query(
                "SELECT id, name, city, max_players FROM teams WHERE id = ? FOR UPDATE",
                TEAM_MAPPER, id)
                .stream()
                .findFirst();
    }

    public boolean deleteById(Long id) {
        return jdbc.update("DELETE FROM teams WHERE id = ?", id) > 0;
    }

    public List<TeamStats> stats() {
        return jdbc.query(
                "SELECT t.id, t.name, t.city, "
                        + "count(p.id) AS players_count, "
                        + "round(avg(extract(year FROM age(p.birth_date))), 1) AS average_age "
                        + "FROM teams t LEFT JOIN players p ON p.team_id = t.id "
                        + "GROUP BY t.id, t.name, t.city "
                        + "ORDER BY t.id",
                (rs, rowNum) -> {
                    BigDecimal avg = rs.getBigDecimal("average_age");
                    return new TeamStats(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("city"),
                            rs.getInt("players_count"),
                            avg == null ? null : avg.doubleValue());
                });
    }
}