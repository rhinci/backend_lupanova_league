package com.hobbyhorse.league.model;

import java.time.LocalDate;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public class Player {

    private Long id;
    private Long teamId;

    @NotBlank(message = "ФИО обязательно")
    @Size(max = 255, message = "ФИО не длиннее 255 символов")
    private String fullName;

    @NotNull(message = "Номер обязателен")
    @Min(value = 1, message = "Номер не меньше 1")
    private Integer number;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    private LocalDate birthDate;

    @Size(max = 100, message = "Кличка лошади не длиннее 100 символов")
    private String horseName;

    public Player() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Integer getNumber() { return number; }
    public void setNumber(Integer number) { this.number = number; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getHorseName() { return horseName; }
    public void setHorseName(String horseName) { this.horseName = horseName; }
}