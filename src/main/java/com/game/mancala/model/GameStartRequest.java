package com.game.mancala.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameStartRequest implements Serializable {
    private String playerOneName;
    private String playerTwoName;
    private String gameId;
}
