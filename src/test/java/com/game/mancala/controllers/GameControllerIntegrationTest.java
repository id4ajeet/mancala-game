package com.game.mancala.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.mancala.model.*;
import com.game.mancala.persist.GameRepository;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerIntegrationTest {

    private static final String GAME_ID_IN_RESPONSE = "<input type=\"hidden\" value=\"123-456-7890\" name=\"gameId\"/>";
    private static final String CURRENT_PLAYER_TWO = "<span>Current Turn: </span><span>Player2</span>";
    private static final String CURRENT_PLAYER_ONE = "<span>Current Turn: </span><span>Player1</span>";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        gameRepository.init();
    }

    @Test
    void testIndex_success200() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Files.readString(Path.of("./src/test/resources/response/index-response.html"))));
    }

    @Test
    void testCreate_success200() throws Exception {
        //GIVEN
        gameRepository.save(createGame("123-456-7890"));

        //WHEN & THEN
        var request = new GameStartRequest();
        request.setGameId("123-456-7890");

        mvc.perform(post("/start")
                .flashAttr("gameStartRequest", request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Matchers.containsString(CURRENT_PLAYER_ONE)))
                .andExpect(content().string(Matchers.containsString(GAME_ID_IN_RESPONSE)));
    }

    @Test
    void testPlay_success200() throws Exception {
        //GIVEN
        gameRepository.save(createGame("123-456-7890"));

        //WHEN & THEN
        var request = new GameUpdateRequest();
        request.setGameId("123-456-7890");
        request.setPitId(5);

        mvc.perform(post("/play")
                .flashAttr("gameUpdateRequest", request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Matchers.containsString(CURRENT_PLAYER_TWO)))
                .andExpect(content().string(Matchers.containsString(GAME_ID_IN_RESPONSE)));
    }

    @Test
    void testPlayWin_success200() throws Exception {
        //GIVEN
        var game = createGame("123-456-7890");
        makePlayer1WinInNextTurnWithPidId6(game);

        gameRepository.save(game);

        //WHEN & THEN
        var request = new GameUpdateRequest();
        request.setGameId("123-456-7890");
        request.setPitId(6); //pitId 6 to win the game as 6 have 1 stone, movement will make all player one pit's empty

        mvc.perform(post("/play")
                .flashAttr("gameUpdateRequest", request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Matchers.containsString("Game won by - <span>Player1</span>")))
                .andExpect(content().string(Matchers.containsString(GAME_ID_IN_RESPONSE)));
    }

    @Test
    void testReset_success200() throws Exception {
        //GIVEN
        var game = createGame("123-456-7890");
        game.setCurrentPlayer("Player2");

        gameRepository.save(game);

        //WHEN & THEN
        var request = new GameResetRequest();
        request.setGameId("123-456-7890");

        mvc.perform(post("/reset")
                .flashAttr("gameResetRequest", request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Matchers.containsString(CURRENT_PLAYER_ONE))) //match to player one
                .andExpect(content().string(Matchers.containsString(GAME_ID_IN_RESPONSE)));
    }

    @Test
    void testEndGame_success200() throws Exception {
        //GIVEN
        var game = createGame("123-456-7890");
        game.setCurrentPlayer("Player2");

        gameRepository.save(game);

        //WHEN & THEN
        var request = new GameExitRequest();
        request.setGameId("123-456-7890");

        mvc.perform(post("/end")
                .flashAttr("gameExitRequest", request))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(content().string(Matchers.containsString("<label>Existing GameId</label>"))) //match content from index.html
                .andExpect(mvcResult -> assertEquals("index", Objects.requireNonNull(mvcResult.getModelAndView()).getViewName()));
    }

    @Test
    void testCreateWithGet_fail405() throws Exception {
        mvc.perform(get("/start"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testPlayWithGet_fail405() throws Exception {
        mvc.perform(get("/play"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testResetWithGet_fail405() throws Exception {
        mvc.perform(get("/reset"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    void testEndWithGet_fail405() throws Exception {
        mvc.perform(get("/end"))
                .andExpect(status().isMethodNotAllowed());
    }

    private void makePlayer1WinInNextTurnWithPidId6(Game game) {
        game.getPits().forEach(p -> p.setStones(0));

        //game will finish with player1 when pitId selected is 6
        game.getPit(6).setStones(1);
        game.getPit(10).setStones(10);

        //update main pit
        game.getPit(7).setStones(41);
        game.getPit(14).setStones(20);

    }

    private Game createGame(String gameId) {
        var pits = IntStream.range(1, 15).mapToObj(id -> new Pit(id, 6)).collect(Collectors.toList());
        pits.get(6).setStones(0);
        pits.get(13).setStones(0);

        return Game.builder()
                .gameId(gameId)
                .playerOneName("Player1")
                .playerTwoName("Player2")
                .currentPlayer("Player1")
                .status(Status.IN_PROGRESS)
                .pits(pits)
                .build();
    }
}