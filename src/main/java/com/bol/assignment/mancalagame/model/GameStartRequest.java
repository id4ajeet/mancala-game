package com.bol.assignment.mancalagame.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameStartRequest implements Serializable {
    private String playerOneName;
    private String playerTwoName;
    private String gameId;
}
