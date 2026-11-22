package com.hobbyhorse.league.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.hobbyhorse.league.model.Team;
import com.hobbyhorse.league.model.User;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class AccessPolicy {

    public void checkCanModify(HttpServletRequest request, Team team) {
        User user = (User) request.getAttribute(AuthInterceptor.CURRENT_USER);
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется вход");
        }
        if (team.getOwnerId() == null || !team.getOwnerId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Нельзя изменять чужую запись");
        }
    }
}