package com.game.mancala.service.impl;

import ch.qos.logback.classic.Logger;
import com.game.mancala.log.TestLogAppender;
import com.game.mancala.model.Game;
import com.game.mancala.model.Pit;
import com.game.mancala.model.Status;
import com.game.mancala.service.MancalaSowService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MancalaSowServiceImplTest {

    private static final int PLAYER_ONE_MAIN_PIT_ID = 7;
    private static final int PLAYER_TWO_MAIN_PIT_ID = 14;

    private MancalaSowService mancalaSowService;
    private TestLogAppender logAppender = new TestLogAppender();

    @BeforeEach
    void setUp() {
        logAppender.clear();
        ((Logger) LoggerFactory.getLogger(MancalaSowServiceImpl.class)).addAppender(logAppender);

        mancalaSowService = new MancalaSowServiceImpl();
    }

    @Test
    void testSowFromMainPitOfPlayer1() {
        //GIVEN
        var game = createGame(true);

        //WHEN
        var sowGame = mancalaSowService.sow(game, PLAYER_ONE_MAIN_PIT_ID);

        //THEN
        var gameNoChange = createGame(true);
        Assertions.assertEquals(gameNoChange, sowGame);
        assertTrue(logAppender.logs().contains("Game 100 - 7 is not correct pit to pick for player - Player1"));
    }

    @Test
    void testSowFromMainPitOfPlayer2() {
        //GIVEN
        var game = createGame(false);

        //WHEN
        var sowGame = mancalaSowService.sow(game, PLAYER_TWO_MAIN_PIT_ID);

        //THEN
        var gameNoChange = createGame(false);
        Assertions.assertEquals(gameNoChange, sowGame);
        assertTrue(logAppender.logs().contains("Game 100 - 14 is not correct pit to pick for player - Player2"));
    }


    @Test
    void testSowInvalidMoveOppositePlayerOnePitSelected() {
        //GIVEN
        var game = createGame(true);

        //WHEN
        var sowGame = mancalaSowService.sow(game, 10); //10 belongs to player2

        //THEN
        var gameNoChange = createGame(true);
        Assertions.assertEquals(gameNoChange, sowGame);
        assertTrue(logAppender.logs().contains("Game 100 - 10 is not correct pit to pick for player - Player1"));
    }

    @Test
    void testSowInvalidMoveOppositePlayerTwoPitSelected() {
        //GIVEN
        var game = createGame(false);

        //WHEN
        var sowGame = mancalaSowService.sow(game, 4); //4 belongs to player1

        //THEN
        var gameNoChange = createGame(false);
        Assertions.assertEquals(gameNoChange, sowGame);
        assertTrue(logAppender.logs().contains("Game 100 - 4 is not correct pit to pick for player - Player2"));
    }

    @Test
    void testSowInvalidMoveCurrentPitIsEmpty() {
        //GIVEN
        var game = createGame(false);
        game.getPit(10).setStones(0);

        //WHEN
        var sowGame = mancalaSowService.sow(game, 10);

        //THEN
        Assertions.assertEquals(game, sowGame);
        assertTrue(logAppender.logs().contains("Game 100 - 10 is not correct pit to pick for player - Player2"));
    }

    @Test
    void testSowValidFirstMove() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);

        //WHEN
        var sowGame = mancalaSowService.sow(game, 1);

        //THEN
        var gameNoChange = createGame(true);
        Assertions.assertNotEquals(gameNoChange, sowGame);

        assertEquals(0, sowGame.getPit(1).getStones());
        assertEquals(7, sowGame.getPit(2).getStones());
        assertEquals(7, sowGame.getPit(3).getStones());
        assertEquals(7, sowGame.getPit(4).getStones());
        assertEquals(7, sowGame.getPit(5).getStones());
        assertEquals(7, sowGame.getPit(6).getStones());

        assertEquals(1, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(6, sowGame.getPit(8).getStones());
        assertEquals(6, sowGame.getPit(9).getStones());
        assertEquals(6, sowGame.getPit(10).getStones());
        assertEquals(6, sowGame.getPit(11).getStones());
        assertEquals(6, sowGame.getPit(12).getStones());
        assertEquals(6, sowGame.getPit(13).getStones());

        assertEquals(0, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in Main pit , same player will continue
        Assertions.assertTrue(game.isPlayerOne());
        assertFalse(logAppender.logs().contains("Game 100 - Next turn is of"));
    }

    @Test
    void testSowValidMoveSkipOpponentMainPitForPlayer1() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);
        setPlayerOnePits(game, 16, Arrays.asList(2, 10, 0, 8, 4, 4)); // 16 + (2+10+0+8+4+4) = 34
        setPlayerTwoPits(game, 12, Arrays.asList(1, 9, 2, 8, 4, 2)); // 12 + (1+9+2+8+4+2) = 38

        //WHEN
        var sowGame = mancalaSowService.sow(game, 5); //position 5 have value 10

        //THEN
        assertEquals(5, sowGame.getPit(1).getStones());
        assertEquals(5, sowGame.getPit(2).getStones());
        assertEquals(8, sowGame.getPit(3).getStones());
        assertEquals(0, sowGame.getPit(4).getStones());
        assertEquals(0, sowGame.getPit(5).getStones());
        assertEquals(3, sowGame.getPit(6).getStones());

        assertEquals(17, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(2, sowGame.getPit(8).getStones());
        assertEquals(10, sowGame.getPit(9).getStones());
        assertEquals(3, sowGame.getPit(10).getStones());
        assertEquals(9, sowGame.getPit(11).getStones());
        assertEquals(5, sowGame.getPit(12).getStones());
        assertEquals(3, sowGame.getPit(13).getStones());

        //No change in opponent main pit
        assertEquals(12, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerTwo());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player2"));
    }

    @Test
    void testSowValidMoveSkipOpponentMainPitForLastStoneForPlayer1() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);
        setPlayerOnePits(game, 16, Arrays.asList(2, 0, 10, 8, 4, 4)); // 16 + (2+0+10+8+4+4) = 34
        setPlayerTwoPits(game, 12, Arrays.asList(1, 9, 2, 8, 4, 2)); // 12 + (1+9+2+8+4+2) = 38

        //WHEN
        var sowGame = mancalaSowService.sow(game, 4); //position 4 have value 10

        //THEN
        assertEquals(5, sowGame.getPit(1).getStones());
        assertEquals(4, sowGame.getPit(2).getStones());
        assertEquals(8, sowGame.getPit(3).getStones());
        assertEquals(0, sowGame.getPit(4).getStones());
        assertEquals(1, sowGame.getPit(5).getStones());
        assertEquals(3, sowGame.getPit(6).getStones());

        assertEquals(17, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(2, sowGame.getPit(8).getStones());
        assertEquals(10, sowGame.getPit(9).getStones());
        assertEquals(3, sowGame.getPit(10).getStones());
        assertEquals(9, sowGame.getPit(11).getStones());
        assertEquals(5, sowGame.getPit(12).getStones());
        assertEquals(3, sowGame.getPit(13).getStones());

        //No change in opponent main pit
        assertEquals(12, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerTwo());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player2"));
    }

    @Test
    void testSowValidMoveSkipOpponentMainPitForPlayer2() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(false);
        setPlayerOnePits(game, 17, Arrays.asList(5, 5, 0, 0, 5, 5)); // 17 + (5+5+0+0+5+5) = 37
        setPlayerTwoPits(game, 10, Arrays.asList(0, 1, 2, 5, 5, 12)); // 10 + (0+1+2+5+5+12) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 13); //position 13 have value 12

        //THEN
        assertEquals(6, sowGame.getPit(1).getStones());
        assertEquals(6, sowGame.getPit(2).getStones());
        assertEquals(1, sowGame.getPit(3).getStones());
        assertEquals(1, sowGame.getPit(4).getStones());
        assertEquals(6, sowGame.getPit(5).getStones());
        assertEquals(6, sowGame.getPit(6).getStones());
        //No change in opponent main pit
        assertEquals(17, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(1, sowGame.getPit(8).getStones());
        assertEquals(2, sowGame.getPit(9).getStones());
        assertEquals(3, sowGame.getPit(10).getStones());
        assertEquals(6, sowGame.getPit(11).getStones());
        assertEquals(6, sowGame.getPit(12).getStones());
        assertEquals(0, sowGame.getPit(13).getStones());

        assertEquals(11, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerOne());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player1"));
    }

    @Test
    void testSowValidMoveSkipOpponentMainPitForLastStoneForPlayer2() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(false);
        setPlayerOnePits(game, 17, Arrays.asList(5, 5, 0, 0, 5, 5)); // 17 + (5+5+0+0+5+5) = 37
        setPlayerTwoPits(game, 10, Arrays.asList(1, 12, 2, 5, 5, 0)); // 10 + (1+12+2+5+5+0) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 9); //position 9 have value 12

        //THEN
        assertEquals(6, sowGame.getPit(1).getStones());
        assertEquals(6, sowGame.getPit(2).getStones());
        assertEquals(1, sowGame.getPit(3).getStones());
        assertEquals(1, sowGame.getPit(4).getStones());
        assertEquals(6, sowGame.getPit(5).getStones());
        assertEquals(6, sowGame.getPit(6).getStones());
        //No change in opponent main pit
        assertEquals(17, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(2, sowGame.getPit(8).getStones());
        assertEquals(0, sowGame.getPit(9).getStones());
        assertEquals(3, sowGame.getPit(10).getStones());
        assertEquals(6, sowGame.getPit(11).getStones());
        assertEquals(6, sowGame.getPit(12).getStones());
        assertEquals(1, sowGame.getPit(13).getStones());

        assertEquals(11, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerOne());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player1"));
    }

    @Test
    void testSowValidMoveLastStoneLandsInCurrentPlayer1EmptyPit() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);
        setPlayerOnePits(game, 14, Arrays.asList(5, 0, 5, 5, 3, 5)); // 14 + (5+0+5+5+3+5) = 37
        setPlayerTwoPits(game, 10, Arrays.asList(1, 12, 2, 5, 5, 0)); // 10 + (1+12+2+5+5+0) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 2); //position 2 have value 3

        //THEN
        assertEquals(5, sowGame.getPit(1).getStones());
        assertEquals(0, sowGame.getPit(2).getStones());
        assertEquals(6, sowGame.getPit(3).getStones());
        assertEquals(6, sowGame.getPit(4).getStones());
        assertEquals(0, sowGame.getPit(5).getStones());
        assertEquals(5, sowGame.getPit(6).getStones());

        assertEquals(14 + 1 + 12, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(1, sowGame.getPit(8).getStones());
        assertEquals(0, sowGame.getPit(9).getStones());
        assertEquals(2, sowGame.getPit(10).getStones());
        assertEquals(5, sowGame.getPit(11).getStones());
        assertEquals(5, sowGame.getPit(12).getStones());
        assertEquals(0, sowGame.getPit(13).getStones());

        assertEquals(10, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerTwo());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player2"));
    }

    @Test
    void testSowValidMoveLastStoneLandsInCurrentPlayer2EmptyPit() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(false);
        setPlayerOnePits(game, 14, Arrays.asList(5, 0, 5, 5, 3, 5)); // 14 + (5+0+5+5+3+5) = 37
        setPlayerTwoPits(game, 10, Arrays.asList(0, 12, 3, 5, 5, 0)); // 10 + (1+12+2+5+5+0) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 10); //position 10 have value 3

        //THEN
        assertEquals(0, sowGame.getPit(1).getStones());
        assertEquals(3, sowGame.getPit(2).getStones());
        assertEquals(5, sowGame.getPit(3).getStones());
        assertEquals(5, sowGame.getPit(4).getStones());
        assertEquals(0, sowGame.getPit(5).getStones());
        assertEquals(5, sowGame.getPit(6).getStones());

        assertEquals(14, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(0, sowGame.getPit(8).getStones());
        assertEquals(12, sowGame.getPit(9).getStones());
        assertEquals(0, sowGame.getPit(10).getStones());
        assertEquals(6, sowGame.getPit(11).getStones());
        assertEquals(6, sowGame.getPit(12).getStones());
        assertEquals(0, sowGame.getPit(13).getStones());

        //opponent pit is 5 + 1 in hand
        assertEquals(10 + 5 + 1, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerOne());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player1"));
    }

    @Test
    void testSowValidMoveLastStoneLandsInCurrentPlayer1EmptyPitButOpponentPitIsEmpty() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);
        setPlayerOnePits(game, 14, Arrays.asList(5, 0, 5, 5, 3, 5)); // 14 + (5+0+5+5+3+5) = 37
        setPlayerTwoPits(game, 22, Arrays.asList(1, 0, 2, 5, 5, 0)); // 22 + (1+0+2+5+5+0) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 2); //position 2 have value 3

        //THEN
        assertEquals(5, sowGame.getPit(1).getStones());
        assertEquals(0, sowGame.getPit(2).getStones());
        assertEquals(6, sowGame.getPit(3).getStones());
        assertEquals(6, sowGame.getPit(4).getStones());
        assertEquals(1, sowGame.getPit(5).getStones());
        assertEquals(5, sowGame.getPit(6).getStones());

        assertEquals(14, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(1, sowGame.getPit(8).getStones());
        assertEquals(0, sowGame.getPit(9).getStones());
        assertEquals(2, sowGame.getPit(10).getStones());
        assertEquals(5, sowGame.getPit(11).getStones());
        assertEquals(5, sowGame.getPit(12).getStones());
        assertEquals(0, sowGame.getPit(13).getStones());

        assertEquals(22, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerTwo());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player2"));
    }

    @Test
    void testSowValidMoveLastStoneLandsInOpponentPlayerEmptyPit() {
        //GIVEN
        //7 6  5  4  3  2  1
        //  8  9 10 11 12 13 14
        var game = createGame(true);
        setPlayerOnePits(game, 14, Arrays.asList(5, 0, 5, 5, 3, 5)); // 14 + (5+0+5+5+3+5) = 37
        setPlayerTwoPits(game, 22, Arrays.asList(1, 0, 2, 5, 5, 0)); // 22 + (1+0+2+5+5+0) = 35

        //WHEN
        var sowGame = mancalaSowService.sow(game, 4); //position 4 have value 5

        //THEN
        assertEquals(5, sowGame.getPit(1).getStones());
        assertEquals(3, sowGame.getPit(2).getStones());
        assertEquals(5, sowGame.getPit(3).getStones());
        assertEquals(0, sowGame.getPit(4).getStones());
        assertEquals(1, sowGame.getPit(5).getStones());
        assertEquals(6, sowGame.getPit(6).getStones());

        assertEquals(15, sowGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());

        assertEquals(2, sowGame.getPit(8).getStones());
        assertEquals(1, sowGame.getPit(9).getStones());
        assertEquals(2, sowGame.getPit(10).getStones());
        assertEquals(5, sowGame.getPit(11).getStones());
        assertEquals(5, sowGame.getPit(12).getStones());
        assertEquals(0, sowGame.getPit(13).getStones());

        assertEquals(22, sowGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());

        //last stone in non main pit , so player will change
        Assertions.assertTrue(game.isPlayerTwo());
        assertTrue(logAppender.logs().contains("Game 100 - Next turn is of Player2"));
    }

    private void setPlayerOnePits(Game game, int mainPitStones, List<Integer> stones) {
        game.getPit(PLAYER_ONE_MAIN_PIT_ID).setStones(mainPitStones);
        int id = 6;
        for (int s : stones) {
            game.getPit(id).setStones(s);
            id--;
        }
    }

    private void setPlayerTwoPits(Game game, int mainPitStones, List<Integer> stones) {
        game.getPit(PLAYER_TWO_MAIN_PIT_ID).setStones(mainPitStones);
        int id = 8;
        for (int s : stones) {
            game.getPit(id).setStones(s);
            id++;
        }
    }

    private Game createGame(boolean isPlayerOneTurn) {
        var pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 6)).collect(Collectors.toList());
        pits.get(6).setStones(0);
        pits.get(13).setStones(0);

        return Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer(isPlayerOneTurn ? "Player1" : "Player2")
                .status(Status.IN_PROGRESS)
                .pits(pits)
                .build();
    }
}