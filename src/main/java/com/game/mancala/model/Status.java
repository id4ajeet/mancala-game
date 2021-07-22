package com.game.mancala.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Status {
    IN_PROGRESS, DRAW, WON;

    @JsonCreator
    public static Status fromValue(String name) {
        for (Status status : Status.values()) {
            if (status.name().equals(name)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value '" + name + "'");
    }
}
