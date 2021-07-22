package com.game.mancala.service.impl;

import com.game.mancala.model.Game;
import com.game.mancala.service.MancalaSowService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MancalaSowServiceImpl implements MancalaSowService {

    @Override
    public Game sow(Game game, int pitId) {
        if (isMainPitId(pitId) || isInvalidRequest(game, pitId) || game.getPit(pitId).isEmpty()) {
            log.warn("Game {} - {} is not correct pit to pick for player - {}", game.getGameId(), pitId, game.getCurrentPlayer());
            return game;
        }

        var selected = game.getPit(pitId);
        int stones = selected.getStones();
        selected.setStones(GameInitializerServiceImpl.NO_STONES);   //Update the current pit to zero as stones are picked

        int currentIndex = pitId;
        //handle all stones except the last one
        while (stones > 1) {
            currentIndex = handleSow(game, currentIndex);
            stones--;
        }

        currentIndex = handleLastStone(game, currentIndex);

        // Switch the turn if the last pit is not main pit
        if (!isMainPitId(currentIndex)) {
            game.switchTurn();
            log.info("Game {} - Next turn is of {}", game.getGameId(), game.getCurrentPlayer());
        }
        return game;
    }

    private int handleLastStone(Game game, int currentIndex) {
        currentIndex = currentIndex % GameInitializerServiceImpl.LEFT_MAIN_PIT_ID + 1;

        if (skipOppositePlayerMainPit(game, currentIndex)) {
            currentIndex = currentIndex % GameInitializerServiceImpl.LEFT_MAIN_PIT_ID + 1;
        }

        var currentPit = game.getPit(currentIndex);
        if (isMainPitId(currentIndex)) {
            //Case last stone is in main pit
            currentPit.increaseStone();
        } else {
            //Case last stone in empty pit other then main
            //7 6  5  4  3  2  1
            //  8  9 10 11 12 13 14
            var oppositePit = game.getPit(GameInitializerServiceImpl.LEFT_MAIN_PIT_ID - currentIndex);

            //take opposite pit stone if last stone lands in own empty pit
            if (currentPit.isEmpty() && isCurrentPlayerPit(game, currentIndex) && !oppositePit.isEmpty()) {
                int currentPlayerMainPitIndex = game.getCurrentPlayerMainPitIndex();
                var currentPlayerMainPit = game.getPit(currentPlayerMainPitIndex);

                currentPlayerMainPit.addStones(oppositePit.getStones() + 1); //add other player stones + one in hand
                oppositePit.setStones(GameInitializerServiceImpl.NO_STONES); //clear other player stone
            } else {
                currentPit.increaseStone();
            }
        }
        return currentIndex;
    }

    private int handleSow(Game game, int currentIndex) {
        currentIndex = currentIndex % GameInitializerServiceImpl.LEFT_MAIN_PIT_ID + 1;
        if (skipOppositePlayerMainPit(game, currentIndex)) {
            currentIndex = currentIndex % GameInitializerServiceImpl.LEFT_MAIN_PIT_ID + 1;
        }

        game.getPit(currentIndex).increaseStone();
        return currentIndex;
    }

    private boolean skipOppositePlayerMainPit(Game game, int currentIndex) {
        return (currentIndex == GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID && game.isPlayerTwo()) || (currentIndex == GameInitializerServiceImpl.LEFT_MAIN_PIT_ID && game.isPlayerOne());
    }

    private boolean isCurrentPlayerPit(Game game, int currentIndex) {
        return (currentIndex < GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID && game.isPlayerOne()) || (currentIndex > GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID && game.isPlayerTwo());
    }

    private boolean isInvalidRequest(Game game, int pidId) {
        return game.isPlayerOne() && pidId > GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID || game.isPlayerTwo() && pidId < GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID;
    }

    private boolean isMainPitId(int pidId) {
        return pidId == GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID || pidId == GameInitializerServiceImpl.LEFT_MAIN_PIT_ID;
    }
}
