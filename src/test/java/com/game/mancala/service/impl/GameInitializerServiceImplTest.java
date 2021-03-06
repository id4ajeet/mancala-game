package com.game.mancala.service.impl;

import com.game.mancala.model.Game;
import com.game.mancala.service.GameInitializerService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameInitializerServiceImplTest {

    private static final int INITIAL_PIT_STONES_COUNT = 6;

    private GameInitializerService service;

    @Test
    void testInitializer() {
        //GIVEN
        service = new GameInitializerServiceImpl(INITIAL_PIT_STONES_COUNT);

        //WHEN
        Game game = service.init("Player1", "Player2");

        //THEN
        assertNotNull(game.getGameId());
        assertEquals("Player1", game.getPlayerOneName());
        assertSame(0, game.getPit(GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID).getStones());

        assertEquals("Player2", game.getPlayerTwoName());
        assertSame(0, game.getPit(GameInitializerServiceImpl.LEFT_MAIN_PIT_ID).getStones());

        assertTrue(game.getPits().stream().noneMatch(
                v -> v.getId() != GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID
                        && v.getId() != GameInitializerServiceImpl.LEFT_MAIN_PIT_ID
                        && v.getStones() != INITIAL_PIT_STONES_COUNT)
        );
    }

    @Test
    void testReset() {
        //GIVEN
        service = new GameInitializerServiceImpl(INITIAL_PIT_STONES_COUNT);
        Game game = service.init("Player1", "Player2");
        game.getPit(1).setStones(4);
        game.getPit(7).addStones(2);

        //WHEN
        Game resetGame = service.reset(game);

        //THEN
        assertEquals(game.getGameId(), resetGame.getGameId());
        assertEquals("Player1", resetGame.getPlayerOneName());
        assertEquals("Player2", resetGame.getPlayerTwoName());
        assertSame(0, resetGame.getPit(GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID).getStones());
        assertSame(0, resetGame.getPit(GameInitializerServiceImpl.LEFT_MAIN_PIT_ID).getStones());
        assertTrue(resetGame.getPits().stream().noneMatch(
                v -> v.getId() != GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID
                        && v.getId() != GameInitializerServiceImpl.LEFT_MAIN_PIT_ID
                        && v.getStones() != INITIAL_PIT_STONES_COUNT)
        );
    }
}