package com.bol.assignment.mancalagame.service;

import com.bol.assignment.mancalagame.model.*;

public interface MancalaGameService {
    Game create(GameStartRequest request);

    Game update(GameUpdateRequest request);

    Game reset(GameResetRequest request);

    void remove(GameExitRequest request);
}
