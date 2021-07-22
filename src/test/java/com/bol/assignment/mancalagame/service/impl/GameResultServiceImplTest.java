package com.bol.assignment.mancalagame.service.impl;

import com.bol.assignment.mancalagame.model.Game;
import com.bol.assignment.mancalagame.model.Pit;
import com.bol.assignment.mancalagame.service.GameResultService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.bol.assignment.mancalagame.model.Status.*;
import static org.junit.jupiter.api.Assertions.*;

class GameResultServiceImplTest {

    private GameResultService gameResultService;

    @BeforeEach
    void setUp() {
        gameResultService = new GameResultServiceImpl();
    }

    @Test
    void checkWinnerWhenPlayerOneAllPitsAreEmpty() {

        //GIVEN player2 have more stones 25+10+5+2 = 42 > 30
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 0)).collect(Collectors.toList());
        pits.get(6).setStones(30); //main pit for player one
        pits.get(13).setStones(25);//main pit for player two

        pits.get(8).setStones(10);
        pits.get(9).setStones(5);
        pits.get(11).setStones(2);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .status(IN_PROGRESS)
                .pits(pits)
                .build();

        //WHEN
        gameResultService.decide(game);

        //THEN
        assertEquals("Player2", game.getWinner());
        assertEquals(WON, game.getStatus());
    }

    @Test
    void checkWinnerWhenPlayerOneAllPitsAreEmptyAndWinner() {

        //GIVEN player2 have less stones 25+5+2 = 32 < 40
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 0)).collect(Collectors.toList());
        pits.get(6).setStones(40); //main pit for player one
        pits.get(13).setStones(25);//main pit for player two

        pits.get(9).setStones(5);
        pits.get(11).setStones(2);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .status(IN_PROGRESS)
                .pits(pits)
                .build();

        //WHEN
        gameResultService.decide(game);

        //THEN
        assertEquals("Player1", game.getWinner());
        assertEquals(WON, game.getStatus());
    }

    @Test
    void checkWinnerWhenPlayerTwoAllPitsAreEmptyAndDraw() {

        //GIVEN
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 0)).collect(Collectors.toList());
        pits.get(6).setStones(30); //main pit for player one
        pits.get(13).setStones(36);//main pit for player two

        pits.get(2).setStones(4);
        pits.get(4).setStones(2);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .status(IN_PROGRESS)
                .pits(pits)
                .build();

        //WHEN
        gameResultService.decide(game);

        //THEN
        assertNull(game.getWinner());
        assertEquals(DRAW, game.getStatus());
    }

    @Test
    void checkWinnerWhenInProgress() {

        //GIVEN
        List<Pit> pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 0)).collect(Collectors.toList());
        pits.get(6).setStones(20); //main pit for player one
        pits.get(13).setStones(25);//main pit for player two

        pits.get(9).setStones(5);
        pits.get(11).setStones(2);

        pits.get(2).setStones(12);

        var game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player2")
                .status(IN_PROGRESS)
                .pits(pits)
                .build();

        //WHEN
        gameResultService.decide(game);

        //THEN
        assertNull(game.getWinner());
        assertEquals(IN_PROGRESS, game.getStatus());
    }
}