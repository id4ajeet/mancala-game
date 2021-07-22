package com.bol.assignment.mancalagame.service.impl;

import com.bol.assignment.mancalagame.exception.GameNotFoundException;
import com.bol.assignment.mancalagame.model.*;
import com.bol.assignment.mancalagame.persist.GameRepository;
import com.bol.assignment.mancalagame.service.GameInitializerService;
import com.bol.assignment.mancalagame.service.GameResultService;
import com.bol.assignment.mancalagame.service.MancalaGameService;
import com.bol.assignment.mancalagame.service.MancalaSowService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@AllArgsConstructor
@Slf4j
public class MancalaGameServiceImpl implements MancalaGameService {

    @NonNull
    private final GameRepository gameRepository;

    @NonNull
    private final GameInitializerService gameInitializerService;

    @NonNull
    private final MancalaSowService mancalaSowService;

    @NonNull
    private final GameResultService gameResultService;

    @Override
    public Game create(GameStartRequest request) {
        if (request.getGameId() != null && !request.getGameId().trim().isEmpty()) {
            return load(request.getGameId());
        }

        var game = gameInitializerService.init(request.getPlayerOneName(), request.getPlayerTwoName());
        log.info("Game started with Id {} between {} and {}", game.getGameId(), request.getPlayerOneName(), request.getPlayerTwoName());
        save(game);
        return game;
    }

    @Override
    public Game update(GameUpdateRequest request) {
        var game = load(request.getGameId());
        log.info("Game {} loaded", request.getGameId());
        if (game.getStatus().equals(Status.IN_PROGRESS)) {
            mancalaSowService.sow(game, request.getPitId());
            gameResultService.decide(game);
            save(game);
            log.info("Game {} updated", request.getGameId());
        } else {
            log.info("Game concluded with {} by {}", game.getStatus().name(), game.getWinner());
        }
        return game;
    }

    @Override
    public Game reset(GameResetRequest request) {
        var game = load(request.getGameId());
        log.info("Game {} loaded", request.getGameId());
        game = gameInitializerService.reset(game);
        save(game);
        log.info("Game {} updated", request.getGameId());
        return game;
    }

    @Override
    public void remove(GameExitRequest request) {
        gameRepository.remove(request.getGameId());
        log.info("Game {} Removed", request.getGameId());
    }

    @Cacheable(value = "games-data", key = "#gameId", unless = "#result  == null")
    public Game load(String gameId) {
        return gameRepository.find(gameId).orElseThrow(() -> new GameNotFoundException(gameId));
    }

    @CachePut(value = "games-data", key = "#game.gameId")
    public Game save(Game game) {
        return gameRepository.save(game);
    }
}
