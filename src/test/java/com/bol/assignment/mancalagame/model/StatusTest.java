package com.bol.assignment.mancalagame.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusTest {

    @Test
    void testEnumCorrectValue() {
        assertEquals(Status.IN_PROGRESS, Status.fromValue("IN_PROGRESS"));
        assertEquals(Status.DRAW, Status.fromValue("DRAW"));
        assertEquals(Status.WON, Status.fromValue("WON"));
    }

    @Test
    void testEnumIncorrectValue() {
        assertThrows(IllegalArgumentException.class, () -> Status.fromValue("WRONG_VALUE"));
    }
}