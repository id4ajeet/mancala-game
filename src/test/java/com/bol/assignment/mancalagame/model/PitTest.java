package com.bol.assignment.mancalagame.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PitTest {

    @Test
    void testIncreament() {
        //GIVEN
        var pit = new Pit();
        pit.setId(1);
        pit.setStones(10);

        //WHEN
        pit.increaseStone();

        //THEN
        assertEquals(11, pit.getStones());
    }

    @Test
    void testAddStones() {
        //GIVEN
        var pit = new Pit(1, 10);

        //WHEN
        pit.addStones(5);

        //THEN
        assertEquals(15, pit.getStones());
        assertEquals(1, pit.getId());
    }

    @Test
    void testEmptyStones() {
        //GIVEN & when
        var pit = new Pit(1, 0);

        //THEN
        assertTrue(pit.isEmpty());
    }
}