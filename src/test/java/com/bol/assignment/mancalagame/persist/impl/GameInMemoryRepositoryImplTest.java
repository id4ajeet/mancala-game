package com.bol.assignment.mancalagame.persist.impl;

import com.bol.assignment.mancalagame.log.TestLogAppender;
import com.bol.assignment.mancalagame.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GameInMemoryRepositoryImplTest {

    private GameInMemoryRepositoryImpl repository;
    private TestLogAppender logAppender = new TestLogAppender();
    private Logger logs = (Logger) LoggerFactory.getLogger(GameInMemoryRepositoryImpl.class);

    @BeforeEach
    void setUp() {
        logAppender.clear();
        logs.addAppender(logAppender);
        repository = new GameInMemoryRepositoryImpl();
    }

    @Test
    void testInit() {
        //WHEN
        repository.init();

        //THEN
        assertTrue(logAppender.logs().contains("Game Repository initialized"));
    }

    @Test
    void testSave() {
        //GIVEN
        Game game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .build();
        repository.init();

        //WHEN
        Game save = repository.save(game);

        //THEN
        assertTrue(logAppender.logs().contains("Saving Game 100"));
        assertEquals("100", save.getGameId());
    }

    @Test
    void testFindSuccess() {
        //GIVEN
        Game game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .build();
        repository.init();
        repository.save(game);

        //WHEN
        Optional<Game> fnd = repository.find("100");

        //THEN
        assertTrue(fnd.isPresent());
        assertEquals("100", fnd.get().getGameId());
    }

    @Test
    void testFindNotExists() {
        //GIVEN
        repository.init();

        //WHEN
        Optional<Game> fnd = repository.find("200");

        //THEN
        assertFalse(fnd.isPresent());
    }

    @Test
    void testRemoveSuccessful() {
        //GIVEN
        Game game = Game.builder()
                .gameId("100")
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .build();
        repository.init();
        repository.save(game);

        //WHEN
        Game remove = repository.remove("100");

        //THEN
        assertEquals("100", remove.getGameId());
        assertTrue(logAppender.logs().contains("Removing Game 100"));
    }

    @Test
    void testRemoveNonExisting() {
        //GIVEN
        repository.init();

        //WHEN
        Game remove = repository.remove("100");

        //THEN
        assertNull(remove);
        assertTrue(logAppender.logs().contains("Removing Game 100"));
    }
}