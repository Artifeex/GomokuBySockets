package com.example.lab1;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class PlayerController {

    @FXML
    GridPane gridPane;

    @FXML
    Label labelMyRole;

    @FXML
    Label labelActiveRole;

    @FXML
    Label labelGameResult;

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private PlayerRole myRole;
    private PlayerRole activeRole;
    private GameStatus gameStatus;

    private Socket socket;


    @FXML
    void onButtonClick(ActionEvent event) {
        if (activeRole == myRole && gameStatus == GameStatus.GameStarted) {
            Button button = (Button) event.getSource();

            int rowIndex = GridPane.getRowIndex(button) == null ? 0 : GridPane.getRowIndex(button);
            int columnIndex = GridPane.getColumnIndex(button) == null ? 0 : GridPane.getColumnIndex(button);

            try {
                out.writeObject(new Coordinates(rowIndex, columnIndex));
            } catch (IOException e) {

            }

        }
    }

    private Button findButton(int rowIndex, int columnIndex) {
        Button result = null;
        for (Node node : gridPane.getChildren()) {
            int nodeRowIndex = GridPane.getRowIndex(node) == null ? 0 : GridPane.getRowIndex(node);
            int nodeColumnIndex = GridPane.getColumnIndex(node) == null ? 0 : GridPane.getColumnIndex(node);

            if (nodeRowIndex == rowIndex && nodeColumnIndex == columnIndex) {
                result = (Button) node;
                break;
            }
        }
        return result;
    }



    @FXML
    void onConnect(ActionEvent event) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int serverPort = 8080;
                String serverHost = "localhost";
                try {
                    socket = new Socket(serverHost, serverPort);
                    System.out.println("Connected to " + serverHost);

                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());

                    StartGameMessage startGameMessage = (StartGameMessage) in.readObject();
                    myRole = startGameMessage.playerRole;
                    gameStatus = startGameMessage.gameStatus;

                    if (gameStatus == GameStatus.WaitingPlayers) {
                        if (myRole == PlayerRole.Cross) {
                            Platform.runLater(() -> {
                                labelMyRole.setText("Вы играете: Х");
                                labelActiveRole.setText("Ожидание игроков");
                            });
                        } else {
                            Platform.runLater(() -> {
                                labelMyRole.setText("Вы играете: 0");
                                labelActiveRole.setText("Ожидание игроков");
                            });
                        }
                    }

                    startGameMessage = (StartGameMessage) in.readObject(); //жду, пока не придет команда с сервера о том, что началась игра

                    activeRole = startGameMessage.playerRole;
                    gameStatus = startGameMessage.gameStatus;

                    if (gameStatus == GameStatus.GameStarted) {
                        Platform.runLater(() -> {
                            labelActiveRole.setText("Сейчас ходят: " + getActivePlayerSign());
                        });
                    }

                    //основной цикл игры
                    while (gameStatus == GameStatus.GameStarted) {
                        if (activeRole == myRole) {
                            //при нажатии на кнопку отправляется информации о ходе на сервер, а потом возвращается информация об успешности или не успешности хода
                            boolean turnStatus = in.readBoolean();
                            while (turnStatus != true) {
                                turnStatus = in.readBoolean();
                            }
                        }
                        // информация о ходе с сервера
                        TurnMessage turnMessage = (TurnMessage) in.readObject();
                        gameStatus = turnMessage.gameStatus;
                        activeRole = turnMessage.activeRole;
                        updateLabels();
                        updateGridByTurn(turnMessage.coordinates, turnMessage.madeMove);
                    }

                    gameEnd();
                    socket.close();
                } catch (UnknownHostException e) {
                    System.out.println("UnknownHostException");
                } catch (EOFException e) {
                    System.out.println("EOFException");
                } catch (IOException e) {
                    System.out.println("IOException");
                } catch (ClassNotFoundException e) {
                    System.out.println("ClassNotFoundException");
                }
            }
        });
        thread.start();
        Button button = (Button) event.getSource();
        button.setDisable(true);
    }

    private void gameEnd() {

        Platform.runLater(() -> {
            if (gameStatus == GameStatus.WinZero) {
                labelGameResult.setText("Результат игры: победа ноликов");
            } else if (gameStatus == GameStatus.WinCross) {
                labelGameResult.setText("Результат игры: победа крестиков");
            } else if (gameStatus == GameStatus.Draw) {
                labelGameResult.setText("Результат игры: ничья");
            } else {
                throw new IllegalStateException("Undefined GameStatus");
            }
        });
    }

    private void updateLabels() {
        Platform.runLater(() -> {
            labelActiveRole.setText("Сейчас ходят: " + getActivePlayerSign());
        });
    }

    private void updateGridByTurn(Coordinates coordinates, PlayerRole playerRole) {
        Platform.runLater(() -> {
            Button button = findButton(coordinates.getRowIndex(), coordinates.getColumnIndex());
            button.setText(getPlayerSign(playerRole));
        });
    }

    private String getActivePlayerSign() {
        return activeRole == PlayerRole.Cross ? "X" : "0";
    }

    private String getPlayerSign(PlayerRole playerRole) {
        return playerRole == PlayerRole.Cross ? "X" : "0";
    }
}
