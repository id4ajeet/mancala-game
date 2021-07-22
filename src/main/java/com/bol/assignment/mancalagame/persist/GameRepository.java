package com.bol.assignment.mancalagame.persist;

import com.bol.assignment.mancalagame.model.Game;

import java.util.Optional;

public interface GameRepository {
    void init();

    Game save(Game game);

    Optional<Game> find(String gameId);

    Game remove(String gameId);
}
