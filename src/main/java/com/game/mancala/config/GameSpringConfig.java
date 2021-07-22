package com.game.mancala.config;

import com.game.mancala.persist.GameRepository;
import com.game.mancala.service.GameInitializerService;
import com.game.mancala.service.GameResultService;
import com.game.mancala.service.MancalaGameService;
import com.game.mancala.service.MancalaSowService;
import com.game.mancala.service.impl.GameInitializerServiceImpl;
import com.game.mancala.service.impl.GameResultServiceImpl;
import com.game.mancala.service.impl.MancalaGameServiceImpl;
import com.game.mancala.service.impl.MancalaSowServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameSpringConfig {

    @Bean
    public GameInitializerService getGameInitializerService(@Value("${mancala.pit.stones.initial.value:6}") final int initialPitStonesCount) {
        return new GameInitializerServiceImpl(initialPitStonesCount);
    }

    @Bean
    public MancalaGameService mancalaGameService(GameRepository gameRepository,
                                                 GameInitializerService gameInitializerService,
                                                 MancalaSowService mancalaSowService,
                                                 GameResultService gameResultService) {
        return new MancalaGameServiceImpl(gameRepository, gameInitializerService, mancalaSowService, gameResultService);
    }

    @Bean
    public MancalaSowService mancalaSowService() {
        return new MancalaSowServiceImpl();
    }

    @Bean
    public GameResultService gameResultService() {
        return new GameResultServiceImpl();
    }
}
