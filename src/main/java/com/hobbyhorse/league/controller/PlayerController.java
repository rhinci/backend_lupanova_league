package com.hobbyhorse.league.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.Player;
import com.hobbyhorse.league.repository.PlayerRepository;
import com.hobbyhorse.league.repository.TeamRepository;
import com.hobbyhorse.league.service.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams/{teamId}/players")
public class PlayerController {

    private final TeamRepository teamRepository;
    private final PlayerService playerService;
    private final PlayerRepository playerRepository;

    public PlayerController(TeamRepository teamRepository,
                            PlayerService playerService,
                            PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerService = playerService;
        this.playerRepository = playerRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(@PathVariable Long teamId, @Valid @RequestBody Player player) {
        return playerService.addPlayer(teamId, player);
    }

    @GetMapping
    public List<Player> list(@PathVariable Long teamId) {
        checkTeamExists(teamId);
        return playerRepository.findByTeamId(teamId);
    }

    private void checkTeamExists(Long teamId) {
        if (teamRepository.findById(teamId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Команда не найдена");
        }
    }
}