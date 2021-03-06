package com.game.mancala.service.impl;

import com.game.mancala.model.Game;
import com.game.mancala.model.Pit;
import com.game.mancala.model.Status;
import com.game.mancala.service.GameResultService;

import java.util.stream.IntStream;

import static com.game.mancala.service.impl.GameInitializerServiceImpl.*;

public class GameResultServiceImpl implements GameResultService {

    @Override
    public Game decide(Game game) {
        checkForWinner(game);
        return game;
    }

    private void checkForWinner(Game game) {
        int playerOneCount = playerOneAllPitsCount(game);
        int playerTwoCount = playerTwoAllPitsCount(game);
        if (playerOneCount == NO_STONES || playerTwoCount == NO_STONES) {
            var playerOneMainPit = game.getPit(RIGHT_MAIN_PIT_ID);
            var playerTwoMainPit = game.getPit(LEFT_MAIN_PIT_ID);

            playerOneCount += playerOneMainPit.getStones();
            playerTwoCount += playerTwoMainPit.getStones();

            if (playerOneCount == playerTwoCount) {
                game.setStatus(Status.DRAW);
            } else if (playerOneCount > playerTwoCount) {
                game.setStatus(Status.WON);
                game.setWinner(game.getPlayerOneName());
            } else {
                game.setStatus(Status.WON);
                game.setWinner(game.getPlayerTwoName());
            }
        }
    }

    private int playerOneAllPitsCount(Game game) {
        return IntStream.range(1, RIGHT_MAIN_PIT_ID)
                .mapToObj(game::getPit)
                .mapToInt(Pit::getStones)
                .sum();
    }

    private int playerTwoAllPitsCount(Game game) {
        return IntStream.range(RIGHT_MAIN_PIT_ID + 1, LEFT_MAIN_PIT_ID)
                .mapToObj(game::getPit)
                .mapToInt(Pit::getStones)
                .sum();
    }
}
