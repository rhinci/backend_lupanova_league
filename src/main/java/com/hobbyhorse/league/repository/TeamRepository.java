package com.hobbyhorse.league.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.hobbyhorse.league.model.Team;

@Repository
public class TeamRepository {

    private final Map<Long, Team> teams = new ConcurrentSkipListMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Team save(Team team) {
        if (team.getId() == null) {
            team.setId(nextId.getAndIncrement());
        }
        teams.put(team.getId(), team);
        return team;
    }

    public List<Team> findAll() {
        return new ArrayList<>(teams.values());
    }

    public Optional<Team> findById(Long id) {
        return Optional.ofNullable(teams.get(id));
    }

    public boolean deleteById(Long id) {
        return teams.remove(id) != null;
    }
}