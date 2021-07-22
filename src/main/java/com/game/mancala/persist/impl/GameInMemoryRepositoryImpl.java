package com.game.mancala.persist.impl;

import com.game.mancala.model.Game;
import com.game.mancala.persist.GameRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
public class GameInMemoryRepositoryImpl implements GameRepository {

    private HashMap<String, Game> gamesMap;

    @Override
    public void init() {
        gamesMap = new HashMap<>();
        log.info("Game Repository initialized");
    }

    @Override
    public Game save(Game game) {
        log.info("Saving Game {}", game.getGameId());
        gamesMap.put(game.getGameId(), game);
        return game;
    }

    @Override
    public Optional<Game> find(String gameId) {
        log.info("Finding Game {}", gameId);
        return Optional.ofNullable(gamesMap.get(gameId));
    }

    @Override
    public Game remove(String gameId) {
        log.info("Removing Game {}", gameId);
        return gamesMap.remove(gameId);
    }
}
