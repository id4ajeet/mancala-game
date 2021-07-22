package com.bol.assignment.mancalagame.config;

import com.bol.assignment.mancalagame.persist.GameRepository;
import com.bol.assignment.mancalagame.service.GameInitializerService;
import com.bol.assignment.mancalagame.service.GameResultService;
import com.bol.assignment.mancalagame.service.MancalaGameService;
import com.bol.assignment.mancalagame.service.MancalaSowService;
import com.bol.assignment.mancalagame.service.impl.GameInitializerServiceImpl;
import com.bol.assignment.mancalagame.service.impl.GameResultServiceImpl;
import com.bol.assignment.mancalagame.service.impl.MancalaGameServiceImpl;
import com.bol.assignment.mancalagame.service.impl.MancalaSowServiceImpl;
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
