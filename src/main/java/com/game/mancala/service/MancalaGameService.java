package com.game.mancala.service;

import com.game.mancala.model.*;

public interface MancalaGameService {
    Game create(GameStartRequest request);

    Game update(GameUpdateRequest request);

    Game reset(GameResetRequest request);

    void remove(GameExitRequest request);
}
