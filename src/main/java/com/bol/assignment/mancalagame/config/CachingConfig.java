package com.bol.assignment.mancalagame.config;

import com.bol.assignment.mancalagame.persist.GameRepository;
import com.bol.assignment.mancalagame.persist.impl.GameInMemoryRepositoryImpl;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("games-data");
    }

    @Bean(initMethod = "init")
    public GameRepository gameRepository() {
        return new GameInMemoryRepositoryImpl();
    }
}
