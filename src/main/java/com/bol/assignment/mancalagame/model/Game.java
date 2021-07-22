package com.bol.assignment.mancalagame.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

import static com.bol.assignment.mancalagame.service.impl.GameInitializerServiceImpl.LEFT_MAIN_PIT_ID;
import static com.bol.assignment.mancalagame.service.impl.GameInitializerServiceImpl.RIGHT_MAIN_PIT_ID;

@Builder
@Data
@EqualsAndHashCode
public class Game implements Serializable {
    private String gameId;
    private String playerOneName;
    private String playerTwoName;
    private List<Pit> pits;
    private String currentPlayer;
    private String winner;
    private Status status;

    public boolean isPlayerOne() {
        return playerOneName.equals(currentPlayer);
    }

    public boolean isPlayerTwo() {
        return playerTwoName.equals(currentPlayer);
    }

    public Pit getPit(int pitId) {
        return pits.get(pitId - 1);
    }

    public int getCurrentPlayerMainPitIndex() {
        return isPlayerOne() ? RIGHT_MAIN_PIT_ID : LEFT_MAIN_PIT_ID;
    }

    public void switchTurn() {
        currentPlayer = isPlayerOne() ? playerTwoName : playerOneName;
    }
}
