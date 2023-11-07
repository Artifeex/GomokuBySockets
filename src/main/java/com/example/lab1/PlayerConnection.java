package com.example.lab1;

import javax.crypto.spec.PSource;
import java.io.*;
import java.net.Socket;

public class PlayerConnection extends Thread {
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    PlayerRole playerRol;
    Server server;

    public PlayerConnection(Server server, Socket playerSocket, PlayerRole playerRol) {
        try {
            clientSocket = playerSocket;
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            in = new ObjectInputStream(clientSocket.getInputStream());
            this.playerRol = playerRol;
            this.server = server;
            StartGameMessage message = new StartGameMessage(GameStatus.WaitingPlayers, playerRol);
            out.writeObject(message);
        } catch (IOException e) {

        }
        this.start();
    }

    //из-за того, что мы в другом потоке, то выполняя блокирующий read на сервере мы не блокируем весь сервер
    @Override
    public void run() {
        try {
            while (true) {
                //ожидание хода клиента
                Coordinates turn = (Coordinates) in.readObject();
                boolean turnSuccess = server.turn(turn, playerRol);
                if (turnSuccess) {
                    out.writeBoolean(true); //сообщаю об успешном ходе
                    server.shareTurn(turn, playerRol); //так как успешный ход, то делюсь информацией о нем с клиентами
                } else {
                    out.writeBoolean(false); //ошибочный ход
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("ClassNotFoundException");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }
}
