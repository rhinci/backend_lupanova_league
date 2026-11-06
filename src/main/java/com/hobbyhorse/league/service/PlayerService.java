package com.hobbyhorse.league.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.Player;
import com.hobbyhorse.league.model.Team;
import com.hobbyhorse.league.repository.PlayerRepository;
import com.hobbyhorse.league.repository.TeamRepository;

@Service
public class PlayerService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public PlayerService(TeamRepository teamRepository, PlayerRepository playerRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional
    public Player addPlayer(Long teamId, Player player) {
        Team team = teamRepository.findByIdForUpdate(teamId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Команда не найдена"));

        if (playerRepository.countByTeamId(teamId) >= team.getMaxPlayers()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "В команде уже максимум игроков");
        }

        player.setId(null);
        player.setTeamId(teamId);
        return playerRepository.save(player);
    }
}