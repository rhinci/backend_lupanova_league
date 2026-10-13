package com.hobbyhorse.league.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestParam;

import com.hobbyhorse.league.model.Team;
import com.hobbyhorse.league.repository.TeamRepository;
import com.hobbyhorse.league.error.BadRequestException;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamRepository teamRepository;

    public TeamController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Team create(@Valid @RequestBody Team team) {
        team.setId(null);
        return teamRepository.save(team);
    }

    @GetMapping
    public List<Team> list(
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(required = false) Integer limit) {
        if (offset < 0) {
            throw new BadRequestException("offset не может быть отрицательным", "offset");
        }
        if (limit != null && limit < 1) {
            throw new BadRequestException("limit должен быть не меньше 1", "limit");
        }
        long maxCount = (limit == null) ? Long.MAX_VALUE : limit;
        return teamRepository.findAll().stream()
                .filter(team -> city == null || team.getCity().equalsIgnoreCase(city))
                .skip(offset)
                .limit(maxCount)
                .toList();
    }

    @GetMapping("/{id}")
    public Team get(@PathVariable Long id) {
        return findOrThrow(id);
    }

    @PatchMapping("/{id}")
    public Team update(@PathVariable Long id, @RequestBody Team changes) {
        Team team = findOrThrow(id);
        if (changes.getName() != null && changes.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Название команды не может быть пустым");
        }
        if (changes.getName() != null) {
            team.setName(changes.getName());
        }
        if (changes.getCity() != null) {
            team.setCity(changes.getCity());
        }
        if (changes.getMaxPlayers() != null) {
            team.setMaxPlayers(changes.getMaxPlayers());
        }
        return teamRepository.save(team);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        findOrThrow(id);
        teamRepository.deleteById(id);
    }

    private Team findOrThrow(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Команда не найдена"));
    }
}
