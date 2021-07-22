package com.game.mancala.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameUpdateRequest implements Serializable {
    private String gameId;
    private int pitId;
}
