package com.bol.assignment.mancalagame.service.impl;

import com.bol.assignment.mancalagame.model.Game;
import com.bol.assignment.mancalagame.model.Pit;
import com.bol.assignment.mancalagame.model.Status;
import com.bol.assignment.mancalagame.service.GameInitializerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class GameInitializerServiceImpl implements GameInitializerService {
    public static final int RIGHT_MAIN_PIT_ID = 7;
    public static final int LEFT_MAIN_PIT_ID = 14;
    public static final int NO_STONES = 0;

    @NonNull
    private final int initialPitStonesCount;

    @Override
    public Game init(String playerOneName, String playerTwoName) {
        return init(playerOneName, playerTwoName, UUID.randomUUID().toString());
    }

    @Override
    public Game reset(Game game) {
        return init(game.getPlayerOneName(), game.getPlayerTwoName(), game.getGameId());
    }

    private Game init(String playerOneName, String playerTwoName, String gameId) {
        List<Pit> pits = IntStream.range(1, 15)
                .mapToObj(id -> new Pit(id, initialPitStonesCount))
                .collect(Collectors.toList());

        pits.get(RIGHT_MAIN_PIT_ID - 1).setStones(NO_STONES);
        pits.get(LEFT_MAIN_PIT_ID - 1).setStones(NO_STONES);

        return Game.builder()
                .gameId(gameId)
                .playerOneName(playerOneName)
                .playerTwoName(playerTwoName)
                .pits(pits)
                .currentPlayer(playerOneName)
                .status(Status.IN_PROGRESS)
                .build();
    }
}
