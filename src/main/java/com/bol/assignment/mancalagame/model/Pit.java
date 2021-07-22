package com.bol.assignment.mancalagame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

import static com.bol.assignment.mancalagame.service.impl.GameInitializerServiceImpl.NO_STONES;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class Pit implements Serializable {
    private int id;
    private int stones;

    public void increaseStone() {
        stones++;
    }

    public void addStones(int stones) {
        this.stones += stones;
    }

    public boolean isEmpty() {
        return stones == NO_STONES;
    }
}
