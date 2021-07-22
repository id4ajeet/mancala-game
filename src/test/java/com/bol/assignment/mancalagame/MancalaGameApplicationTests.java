package com.bol.assignment.mancalagame;

import ch.qos.logback.classic.Logger;
import com.bol.assignment.mancalagame.log.TestLogAppender;
import com.bol.assignment.mancalagame.model.Game;
import com.bol.assignment.mancalagame.model.GameStartRequest;
import com.bol.assignment.mancalagame.service.MancalaGameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class MancalaGameApplicationTests {

    @Autowired
    private MancalaGameService mancalaGameService;

    private TestLogAppender logAppender = new TestLogAppender();
    private Logger logs = (Logger) LoggerFactory.getLogger("org.springframework");

    @BeforeEach
    void setUp() {
        logAppender.clear();
        logs.addAppender(logAppender);
    }

    @Test
    void contextLoads() {
        var request = new GameStartRequest();
        request.setPlayerOneName("Player1");
        request.setPlayerTwoName("Player2");

        Game game = mancalaGameService.create(request);
        assertNotNull(game);
        assertNotNull(game.getGameId());
    }

    @Test
    void startupTest() {
        try {
            MancalaGameApplication.main(new String[]{});
            assertTrue(logAppender.logs().contains("[INFO] Tomcat started on port(s): 8888 (http) with context path ''"));
        } catch(Exception e) {
            assertTrue(logAppender.logs().contains("Port 8888 was already in use."));
        }
    }
}
