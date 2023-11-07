package com.example.lab1;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {

    private static final int SERVER_PORT = 8080;

    private List<PlayerConnection> players = new ArrayList<PlayerConnection>();

    private PlayerRole activePlayerRole = PlayerRole.Cross;

    private Game game = new Game();

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    @Override
    public void run() {
        System.out.println("Server started");
        try {
            ServerSocket listenSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Server create server socket");
            while (players.size() < 2) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Client connected");
                players.add(new PlayerConnection(this, clientSocket, getPlayerRole()));
                if (players.size() == 2) {
                    startGame();
                }
            }
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    boolean turn(Coordinates coordinates, PlayerRole playerRole) {
        return game.turn(coordinates, playerRole);
    }

    private PlayerRole getPlayerRole() {
        PlayerRole playerRole;
        if (players.size() == 0) {
            playerRole = PlayerRole.Cross;
        } else if (players.size() == 1) {
            playerRole = PlayerRole.Zero;
        } else {
            throw new IllegalStateException("Count connected players is greater than two");
        }
        return playerRole;
    }

    private void startGame() {
        try {
            players.get(0).out.writeObject(new StartGameMessage(GameStatus.GameStarted, activePlayerRole));
            players.get(1).out.writeObject(new StartGameMessage(GameStatus.GameStarted, activePlayerRole));
        } catch (InvalidClassException e) {
            System.out.println("InvalidClassException in startGame");
        } catch (NotSerializableException e) {
            System.out.println("NotSerializableException in startGame");
        } catch (IOException e) {
            System.out.println("IOException in startGame");
        }
    }

    private PlayerRole getEnemyRole(PlayerRole playerRole) {
        return playerRole == PlayerRole.Cross ? PlayerRole.Zero : PlayerRole.Cross;
    }

    void shareTurn(Coordinates coordinates, PlayerRole playerRole) {
        try {
            activePlayerRole = getEnemyRole(playerRole);
            players.get(0).out.writeObject(new TurnMessage(game.gameStatus, activePlayerRole, coordinates, playerRole));
            players.get(1).out.writeObject(new TurnMessage(game.gameStatus, activePlayerRole, coordinates, playerRole));
        } catch (InvalidClassException e) {
            System.out.println("InvalidClassException in shareTurn");
        } catch (NotSerializableException e) {
            System.out.println("NotSerializableException in shareTurn");
        } catch (IOException e) {
            System.out.println("IOException in shareTurn");
        }
    }

}
