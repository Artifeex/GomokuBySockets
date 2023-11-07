package com.example.lab1;

import java.io.Serializable;

public enum GameStatus implements Serializable {
    WaitingPlayers, GameStarted, Draw, WinCross, WinZero
}
