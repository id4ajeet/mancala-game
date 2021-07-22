package com.game.mancala.model;

import lombok.Data;

import java.io.Serializable;
@Data
public class GameExitRequest implements Serializable {
    private String gameId;
}
