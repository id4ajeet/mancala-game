package com.bol.assignment.mancalagame.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class GameResetRequest implements Serializable {
    private String gameId;
}
