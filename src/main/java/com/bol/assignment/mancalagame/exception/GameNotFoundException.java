package com.bol.assignment.mancalagame.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "GameId not found")
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameId) {
        super(String.format("Game %s not found", gameId));
    }
}
