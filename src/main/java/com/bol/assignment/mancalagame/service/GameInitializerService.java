package com.bol.assignment.mancalagame.service;

import com.bol.assignment.mancalagame.model.Game;

public interface GameInitializerService {
    Game init(String playerOneName, String playerTwoName);

    Game reset(Game game);
}
