package com.game.mancala.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameTest {

    @Test
    void isPlayerOneTurn() {
        //GIVEN
        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player1")
                .build();

        //WHEN
        boolean isPlayerOne = game.isPlayerOne();

        //THEN
        assertTrue(isPlayerOne);
    }

    @Test
    void isPlayerTwoTurn() {
        //GIVEN
        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .build();

        //WHEN
        boolean isPlayerTwo = game.isPlayerTwo();

        //THEN
        assertTrue(isPlayerTwo);
    }

    @Test
    void testGetCurrentPlayerMainPit() {
        //GIVEN
        int player2MainPitIndex = 13;
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 6)).collect(Collectors.toList());
        pits.get(6).setStones(0);
        pits.get(player2MainPitIndex).setStones(0);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .pits(pits)
                .build();

        //WHEN
        int mainPitIndex = game.getCurrentPlayerMainPitIndex();
        var mainPit = game.getPit(mainPitIndex);

        //THEN
        assertEquals(player2MainPitIndex + 1, mainPitIndex);
        assertEquals(0, mainPit.getStones());
    }

    @Test
    void testSwitchTurn() {
        //GIVEN
        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player1")
                .build();

        //WHEN
        game.switchTurn();

        //THEN
        assertEquals("Player2", game.getCurrentPlayer());
    }

    @Test
    void testBuilder() {
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 0)).collect(Collectors.toList());
        pits.get(6).setStones(36);
        pits.get(13).setStones(36);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .status(Status.DRAW)
                .winner("NA")
                .pits(pits)
                .build();

        assertEquals("100", game.getGameId());
        assertEquals("Player1", game.getPlayerOneName());
        assertEquals("Player2", game.getPlayerTwoName());
        assertEquals("Player2", game.getCurrentPlayer());
        assertEquals("NA", game.getWinner());
        Assertions.assertEquals(Status.DRAW, game.getStatus());
        assertEquals(14, game.getPits().size());

        //check updates to game
        game.setGameId("200");
        game.setPlayerOneName("Player-1");
        game.setPlayerTwoName("Player-2");
        game.setCurrentPlayer("Player-1");
        game.setStatus(Status.WON);
        game.setWinner("Player-1");
        game.setPits(pits);

        assertEquals("200", game.getGameId());
        assertEquals("Player-1", game.getPlayerOneName());
        assertEquals("Player-2", game.getPlayerTwoName());
        assertEquals("Player-1", game.getCurrentPlayer());
        assertEquals("Player-1", game.getWinner());
        Assertions.assertEquals(Status.WON, game.getStatus());
        assertEquals(14, game.getPits().size());
    }

    @Test
    void testEquals() {
        var pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 6)).collect(Collectors.toList());
        pits.get(6).setStones(0);
        pits.get(13).setStones(0);

        var game1 = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player1")
                .status(Status.IN_PROGRESS)
                .pits(pits)
                .build();

        var game2 = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player1")
                .status(Status.IN_PROGRESS)
                .pits(List.copyOf(pits))
                .build();

        assertEquals(game1, game2);
        assertEquals(game1.hashCode(), game2.hashCode());
    }
}