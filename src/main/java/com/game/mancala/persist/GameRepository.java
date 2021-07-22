package com.game.mancala.persist;

import com.game.mancala.model.Game;

import java.util.Optional;

public interface GameRepository {
    void init();

    Game save(Game game);

    Optional<Game> find(String gameId);

    Game remove(String gameId);
}
