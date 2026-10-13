package com.hobbyhorse.league.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hobbyhorse.league.model.Team;
import com.hobbyhorse.league.repository.TeamRepository;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Team create(@RequestBody Team team) {
        team.setId(null);
        return teamRepository.save(team);
    }
}
