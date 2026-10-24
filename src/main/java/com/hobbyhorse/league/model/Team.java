package com.hobbyhorse.league.model;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public class Team {

    private Long id;

    @NotBlank(message = "Название обязательно")
    @Size(max = 100, message = "Название не длиннее 100 символов")
    private String name;

    @NotBlank(message = "Город обязателен")
    private String city;

    @NotNull(message = "Максимум игроков обязателен")
    @Min(value = 1, message = "Максимум игроков не меньше 1")
    private Integer maxPlayers;

    public Team() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

}