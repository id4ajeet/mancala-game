package com.game.mancala.controllers;

import com.game.mancala.model.*;
import com.game.mancala.service.MancalaGameService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
public class GameController {

    private static final String INDEX_PAGE = "index";
    private static final String PLAY_GAME_PAGE = "play-game";
    private static final String GAME_ATTR = "game";

    @NonNull
    private final MancalaGameService mancalaGameService;

    @GetMapping("/")
    public String index() {
        return INDEX_PAGE;
    }

    @PostMapping(value = "/start")
    public ModelAndView startGame(@ModelAttribute GameStartRequest request) {
        return getModelAndView(mancalaGameService.create(request));
    }

    @PostMapping(value = "/play")
    public ModelAndView playGame(@ModelAttribute GameUpdateRequest request) {
        return getModelAndView(mancalaGameService.update(request));
    }

    @PostMapping(value = "/reset")
    public ModelAndView resetGame(@ModelAttribute GameResetRequest request) {
        return getModelAndView(mancalaGameService.reset(request));
    }

    @PostMapping(value = "/end")
    public String endGame(@ModelAttribute GameExitRequest request) {
        mancalaGameService.remove(request);
        return INDEX_PAGE;
    }

    private ModelAndView getModelAndView(Game game) {
        var modelAndView = new ModelAndView();
        modelAndView.setViewName(PLAY_GAME_PAGE);
        modelAndView.addObject(GAME_ATTR, game);
        return modelAndView;
    }
}

