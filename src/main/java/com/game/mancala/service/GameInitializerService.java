package com.game.mancala.service;

import com.game.mancala.model.Game;

public interface GameInitializerService {
    Game init(String playerOneName, String playerTwoName);

    Game reset(Game game);
}
