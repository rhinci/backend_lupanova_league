package com.hobbyhorse.league.security;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class LoginRateLimiter {

    private static final int MAX_FAILURES = 5;
    private static final long WINDOW_MS = 60_000;

    private final Map<String, Deque<Long>> failures = new ConcurrentHashMap<>();

    public void checkAllowed(String key) {
        Deque<Long> times = failures.get(key);
        if (times == null) {
            return;
        }
        synchronized (times) {
            prune(times);
            if (times.size() >= MAX_FAILURES) {
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                        "Слишком много неудачных попыток входа, попробуйте через минуту");
            }
        }
    }

    public void recordFailure(String key) {
        Deque<Long> times = failures.computeIfAbsent(key, k -> new ArrayDeque<>());
        synchronized (times) {
            prune(times);
            times.addLast(System.currentTimeMillis());
        }
    }

    public void reset(String key) {
        failures.remove(key);
    }

    private void prune(Deque<Long> times) {
        long limit = System.currentTimeMillis() - WINDOW_MS;
        while (!times.isEmpty() && times.peekFirst() < limit) {
            times.removeFirst();
        }
    }
}