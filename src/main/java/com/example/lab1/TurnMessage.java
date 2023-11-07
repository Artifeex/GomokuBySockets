package com.example.lab1;

import java.io.Serializable;

public class TurnMessage implements Serializable {
    GameStatus gameStatus;

    PlayerRole activeRole;

    Coordinates coordinates;

    PlayerRole madeMove;

    public TurnMessage(GameStatus gameStatus, PlayerRole activeRole, Coordinates coordinates, PlayerRole madeMove) {
        this.gameStatus = gameStatus;
        this.activeRole = activeRole;
        this.coordinates = coordinates;
        this.madeMove = madeMove;
    }
}
