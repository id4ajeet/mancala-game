package com.bol.assignment.mancalagame.service.impl;

import ch.qos.logback.classic.Logger;
import com.bol.assignment.mancalagame.exception.GameNotFoundException;
import com.bol.assignment.mancalagame.log.TestLogAppender;
import com.bol.assignment.mancalagame.model.*;
import com.bol.assignment.mancalagame.persist.impl.GameInMemoryRepositoryImpl;
import com.bol.assignment.mancalagame.service.MancalaGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class MancalaGameServiceImplTest {

    private static final int PLAYER_ONE_MAIN_PIT_ID = 7;
    private static final int PLAYER_TWO_MAIN_PIT_ID = 14;
    private static final int INITIAL_PIT_STONES_COUNT = 6;
    private MancalaGameService mancalaGameService;

    private TestLogAppender logAppender = new TestLogAppender();
    private GameInMemoryRepositoryImpl gameRepository;
    private GameInitializerServiceImpl gameInitializerService;

    @BeforeEach
    void setUp() {
        logAppender.clear();
        ((Logger) LoggerFactory.getLogger(MancalaGameServiceImpl.class)).addAppender(logAppender);

        gameRepository = new GameInMemoryRepositoryImpl();
        gameRepository.init();

        gameInitializerService = new GameInitializerServiceImpl(INITIAL_PIT_STONES_COUNT);

        mancalaGameService = new MancalaGameServiceImpl(
                gameRepository,
                gameInitializerService,
                new MancalaSowServiceImpl(),
                new GameResultServiceImpl());
    }

    @Test
    void testCreateGame() {
        //GIVEN
        var request = new GameStartRequest();
        request.setPlayerOneName("Player1");
        request.setPlayerTwoName("Player2");

        //WHEN
        var game = mancalaGameService.create(request);

        //THEN
        assertTrue(logAppender.logs().contains(String.format("Game started with Id %s between Player1 and Player2", game.getGameId())));
    }

    @Test
    void testLoadGameNotExits() {
        //GIVEN
        var request = new GameStartRequest();
        request.setGameId("100-100-100");

        //WHEN & THEN
        assertThrows(GameNotFoundException.class, () -> mancalaGameService.create(request));
    }

    @Test
    void testLoadExistingGame() {
        //GIVEN
        Game oldGame = storeGame("1234", null, Status.IN_PROGRESS, null);

        //WHEN
        var newRequest = new GameStartRequest();
        newRequest.setGameId("1234");

        Game loadedGame = mancalaGameService.create(newRequest);

        //THEN
        assertEquals(oldGame, loadedGame);
        assertEquals("Player1", loadedGame.getPlayerOneName());
        assertEquals("Player2", loadedGame.getPlayerTwoName());
        assertEquals("Player1", loadedGame.getCurrentPlayer());
        assertEquals("Player1", loadedGame.getCurrentPlayer());
        assertEquals(Status.IN_PROGRESS, loadedGame.getStatus());
        assertEquals(0, loadedGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());
        assertEquals(0, loadedGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());
    }

    @Test
    void testUpdateGame() {
        //GIVEN
        var game = storeGame("1234", null, Status.IN_PROGRESS, null);

        //WHEN
        var request = new GameUpdateRequest();
        request.setGameId(game.getGameId());
        request.setPitId(4);

        var updatedGame = mancalaGameService.update(request);

        //THEN
        assertEquals("1234", updatedGame.getGameId());
        assertEquals("Player1", updatedGame.getPlayerOneName());
        assertEquals("Player2", updatedGame.getPlayerTwoName());
        //player change on update
        assertEquals("Player2", updatedGame.getCurrentPlayer());
        assertEquals(Status.IN_PROGRESS, updatedGame.getStatus());
        assertEquals(1, updatedGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());
        assertEquals(0, updatedGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());
        assertTrue(logAppender.logs().contains("Game 1234 updated"));
    }

    @Test
    void testUpdateFinishedGame() {
        //GIVEN
        List<Pit> pits = IntStream.range(1, 15)
                .mapToObj(id -> new Pit(id, 0))
                .collect(Collectors.toList());
        pits.get(PLAYER_ONE_MAIN_PIT_ID - 1).setStones(36);
        pits.get(PLAYER_TWO_MAIN_PIT_ID - 1).setStones(36);

        storeGame("1234", pits, Status.DRAW, null);

        //WHEN
        var request = new GameUpdateRequest();
        request.setGameId("1234");
        request.setPitId(4);

        var updatedGame = mancalaGameService.update(request);

        //THEN
        assertEquals("1234", updatedGame.getGameId());
        assertEquals("Player1", updatedGame.getPlayerOneName());
        assertEquals("Player2", updatedGame.getPlayerTwoName());
        //player no change on update
        assertEquals("Player1", updatedGame.getCurrentPlayer());
        assertEquals(Status.DRAW, updatedGame.getStatus());
        assertEquals(36, updatedGame.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());
        assertEquals(36, updatedGame.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());
        assertFalse(logAppender.logs().contains("Game 1234 updated"));
        assertTrue(logAppender.logs().contains("Game concluded with DRAW"));
    }

    @Test
    void testResetGame() {
        //GIVEN
        List<Pit> pits = IntStream.range(1, 15)
                .mapToObj(id -> new Pit(id, 0))
                .collect(Collectors.toList());
        pits.get(PLAYER_ONE_MAIN_PIT_ID - 1).setStones(37);
        pits.get(PLAYER_TWO_MAIN_PIT_ID - 1).setStones(35);

        storeGame("1234", pits, Status.WON, "Player1");

        //WHEN
        var request = new GameResetRequest();
        request.setGameId("1234");

        var game = mancalaGameService.reset(request);

        //THEN
        assertEquals("1234", game.getGameId());
        assertEquals("Player1", game.getPlayerOneName());
        assertEquals("Player2", game.getPlayerTwoName());
        //player no change on update
        assertEquals("Player1", game.getCurrentPlayer());
        assertEquals(Status.IN_PROGRESS, game.getStatus());
        assertEquals(0, game.getPit(PLAYER_ONE_MAIN_PIT_ID).getStones());
        assertEquals(0, game.getPit(PLAYER_TWO_MAIN_PIT_ID).getStones());
        assertTrue(logAppender.logs().contains("Game 1234 updated"));
    }

    @Test
    void testRemoveGame() {
        //GIVEN
        storeGame("1234", null, null, null);

        //WHEN
        var request = new GameExitRequest();
        request.setGameId("1234");
        mancalaGameService.remove(request);

        //THEN
        assertTrue(logAppender.logs().contains("Game 1234 Removed"));
        assertTrue(gameRepository.find("1234").isEmpty());
    }

    @Test
    void testRemoveGameNull() {
        //GIVEN
        storeGame("1234", null, null, null);

        //WHEN & THEN
        var request = new GameExitRequest();
        assertDoesNotThrow(() -> mancalaGameService.remove(request));
    }

    private Game storeGame(String gameId, List<Pit> pits, Status status, String winner) {
        var game = gameInitializerService.init("Player1", "Player2");
        game.setGameId(gameId);
        if (pits != null) {
            game.setPits(pits);
        }
        if (status != null) {
            game.setStatus(status);
        }
        if (winner != null) {
            game.setWinner(winner);
        }
        gameRepository.save(game);
        return game;
    }

}