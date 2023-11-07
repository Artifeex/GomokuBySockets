package com.example.lab1;

public class Game {

    private static final int FIELD_SIZE = 15;

    GameStatus gameStatus;

    private Cell[][] field = new Cell[FIELD_SIZE][FIELD_SIZE];

    public Game() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                field[i][j] = Cell.Empty;
            }
        }
    }

    private enum Cell {
        Empty, Cross, Zero
    }

    boolean turn(Coordinates coordinates, PlayerRole playerRole) {
        if (field[coordinates.getRowIndex()][coordinates.getColumnIndex()] != Cell.Empty) {
            //fault
            return false;
        } else {
            field[coordinates.getRowIndex()][coordinates.getColumnIndex()] = mapPlayerRoleToCell(playerRole);
            updateGameStatus(coordinates, mapPlayerRoleToCell(playerRole));
        }
        return true;
    }

    private Cell mapPlayerRoleToCell(PlayerRole playerRole) {
        return playerRole == PlayerRole.Cross ? Cell.Cross : Cell.Zero;
    }

    private boolean checkRow(Coordinates coordinates, Cell playerTurnCell) {
        int counter = 0;
        for (int column = 0; column < FIELD_SIZE; column++) {
            if (field[coordinates.getRowIndex()][column] == playerTurnCell) {
                counter++;
                if (counter == 5) {
                    return true;
                }
            } else {
                counter = 0;
            }
        }
        return false;
    }

    private boolean checkColumn(Coordinates coordinates, Cell playerTurnCell) {
        int counter = 0;
        for (int row = 0; row < FIELD_SIZE; row++) {
            if (field[row][coordinates.getColumnIndex()] == playerTurnCell) {
                counter++;
                if (counter == 5) {
                    return true;
                }
            } else {
                counter = 0;
            }
        }
        return false;
    }

    private boolean checkDiagonals(Coordinates coordinates, Cell playerTurnCell) {
        return checkDiagonalFromTopToLeft(coordinates, playerTurnCell) || checkDiagonalFromTopToRight(coordinates, playerTurnCell);
    }

    private boolean checkDiagonalFromTopToRight(Coordinates coordinates, Cell playerTurnCell) {
        int x = coordinates.getRowIndex();
        int y = coordinates.getColumnIndex();
        while (x != 0 && y != 0) {
            x--;
            y--;
        }
        int counter = 0;
        while (x < FIELD_SIZE && y < FIELD_SIZE) {
            if (field[x][y] == playerTurnCell) {
                counter++;
                if (counter == 5) {
                    return true;
                }
            } else {
                counter = 0;
            }
            x++;
            y++;
        }
        return false;
    }

    private boolean checkDiagonalFromTopToLeft(Coordinates coordinates, Cell playerTurnCell) {
        int x = coordinates.getRowIndex();
        int y = coordinates.getColumnIndex();
        while (x < FIELD_SIZE - 1 && y < FIELD_SIZE - 1) {
            x++;
            y++;
        }
        int counter = 0;
        while (x >= 0 && y < FIELD_SIZE) {
            if (field[x][y] == playerTurnCell) {
                counter++;
                if (counter == 5) {
                    return true;
                }
            } else {
                counter = 0;
            }
            x--;
            y++;
        }
        return false;
    }

    private boolean isDraw() {
        for (int i = 0; i < FIELD_SIZE; i++) {
            for (int j = 0; j < FIELD_SIZE; j++) {
                if (field[i][j] == Cell.Empty) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateGameStatus(Coordinates coordinates, Cell playerTurnCell) {

        if (checkRow(coordinates, playerTurnCell) || checkColumn(coordinates, playerTurnCell) || checkDiagonals(coordinates, playerTurnCell)) {
            gameStatus = playerTurnCell == Cell.Cross ? GameStatus.WinCross : GameStatus.WinZero;
        } else if (isDraw()) {
            gameStatus = GameStatus.Draw;
        } else {
            gameStatus = GameStatus.GameStarted;
        }
    }

}
