import java.io.*;
import java.net.*;

/**
 * This class starts a TicTacToe server object.
 * It waits for 2 TicTacToeClient connections before starting the game.
 * */
public class TicTacToeServer implements Runnable{
    private static TicTacToeServer s;
    private boolean running;
    private BufferedReader inPlayer1;
    private PrintWriter outPlayer1;
    private BufferedReader inPlayer2;
    private PrintWriter outPlayer2;
    private TicTacToeBoard game;

    /**
     * Main method to start the server.
     * */
    public static void main(String[] args) {
        s = new TicTacToeServer();
        s.start();
    }

    /**
     * This method is called when a thread is started.
     * It handles the communication between the server and the client.
     * */
    public void start(){
        game = new TicTacToeBoard();
        try {
            System.out.println("Server started, now waiting for players to connect...");
            ServerSocket serverSocket = new ServerSocket(26901);

            // player 1
            Socket player11 = serverSocket.accept();
            System.out.println("Player 1 has joined the game from IP address: " + player11.getInetAddress()); // print IP address of player 1
            this.inPlayer1 = new BufferedReader(new InputStreamReader(player11.getInputStream()));
            this.outPlayer1 = new PrintWriter(player11.getOutputStream(), true);


            // player 2
            Socket player21 = serverSocket.accept();
            System.out.println("Player 2 has joined the game from IP address: " + player21.getInetAddress()); // print IP address of player 2
            this.inPlayer2 = new BufferedReader(new InputStreamReader(player21.getInputStream()));
            this.outPlayer2 = new PrintWriter(player21.getOutputStream(), true);


            // start the game
            outPlayer1.println("YOUR_TURN");
            outPlayer1.println("MESSAGE It's your turn!");
            outPlayer2.println("MESSAGE Please wait for your turn.");

            Thread player1 = new Thread(s);
            player1.setName("Player1");
            Thread player2 = new Thread(s);
            player2.setName("Player2");

            running = true;
            player1.start();
            player2.start();

            player1.join();
            player2.join();

            serverSocket.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String readMessage(boolean player1Turn) throws IOException {
        if (player1Turn) {
            return inPlayer1.readLine();
        } else {
            return inPlayer2.readLine();
        }
    }

    /**
     * This method processes the message sent by the client.
     */
    public void run() {
        boolean player1Turn = (Thread.currentThread().getName().equals("Player1"));
        while (running) {
            try {
                String message = readMessage(player1Turn);
                processMessage((player1Turn ? "PLAYER1 " : "PLAYER2 ") + message, player1Turn);

            } catch (IOException ex) {
                outPlayer1.println("DISCONNECTED");
                outPlayer2.println("DISCONNECTED");
                running = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void endgameConditions(int winner) {
        if (winner == 1) {
            outPlayer1.println("WINNER");
            outPlayer2.println("LOSER");
            running = false;
        } else if (winner == 2) {
            outPlayer1.println("LOSER");
            outPlayer2.println("WINNER");
            running = false;
        } else if (winner == 3) {
            outPlayer1.println("TIE");
            outPlayer2.println("TIE");
            running = false;
        } else if (winner != 0) {
            throw new IllegalArgumentException("Unexpected winner value");
        }

    }

    private void processMessage(String m, boolean player1Turn) throws IOException {
        System.out.println("Received message from client: " + m);
        // m is a string of the form "PLAYERn MOVE x" where n = 1,2 and x is a position on the board
        // representing the coordinates of the move
        String[] parts = m.split(" ");
        String player = parts[0];
        String move = parts[2];

        if (game.setBoard(Integer.parseInt(move), player1Turn)){
            System.out.println("set board to: " + game.getBoard() + " By " + player.substring(6));

            outPlayer1.println("BOARD " + game.getBoard());
            outPlayer2.println("BOARD " + game.getBoard());
            int winner = game.gameOver();
            if (winner == 0) {
                if (player1Turn) {
                    outPlayer2.println("MESSAGE Your opponent has moved, now it's your turn.");
                    outPlayer1.println("MESSAGE Valid move, wait for you opponent.");
                } else {
                    outPlayer1.println("MESSAGE Your opponent has moved, now it's your turn.");
                    outPlayer2.println("MESSAGE Valid move, wait for you opponent.");
                }
            }
            // check for endgame conditions
            endgameConditions(game.gameOver());
        }
    }
}


