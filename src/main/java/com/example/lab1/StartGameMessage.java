package com.example.lab1;

import java.io.Serializable;

public class StartGameMessage implements Serializable {

    GameStatus gameStatus;

    PlayerRole playerRole;

    public StartGameMessage(GameStatus gameStatus, PlayerRole playerRole) {
        this.gameStatus = gameStatus;
        this.playerRole = playerRole;
    }
}
