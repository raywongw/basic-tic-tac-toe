/**
 * This class is used to set up the board for tic-tac-toe game.
 */
public class TicTacToeBoard {
    // set up board for tic-tac-toe
    private boolean isPlayer1Turn;
    private int[] board;
    private int count;

    /**
     * Initializes a TicTacToe game session with a new board. Player 1 goes first.
     */
    public TicTacToeBoard() {
        board = new int[9];
        for (int i = 0; i < 9; i++) board[i] = 0;
        isPlayer1Turn = true;
        count = 0;
    }

    /**
     * Checks if the game has ended with a winner or a draw.
     * @return String of the board
     */
    public String getBoard() {
        String boardString = "";
        for (int i = 0; i < 9; i++) {
            if (board[i] == 0) boardString += "_";
            else if (board[i] == 1) boardString += "X";
            else if (board[i] == -1) boardString += "O";
        }
        return boardString;
    }

    /**
     * Set the board with the position and check if it is valid at the same time.
     * @param position the position of the board
     * @param player1 true if it is player 1's turn
     * @return true if the position is valid and it is the player's turn
     */
    public synchronized boolean setBoard(int position, boolean player1) {
        // check if the position is valid
        // check if it is the player's turn

        if (position < 0 || position > 8) return false;
        if (player1 && !isPlayer1Turn || !player1 && isPlayer1Turn) return false;
        // check if the position is empty
        int row = position / 3;
        int col = position % 3;
        if (board[position] != 0) return false;
        board[position] = player1 ? 1 : -1;
        count++;
        isPlayer1Turn = !isPlayer1Turn;
        return true;

    }


    /**
     * Check if the game is over.
     * @return 0 if the game is not over
     *         1 if player 1 wins
     *         2 if player 2 wins
     *         3 if draw
     */
    public int gameOver(){
        if (count < 5) return 0;

        // horizontal and vertical
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] + board[i * 3 + 1] + board[i * 3 + 2] == 3) return 1;
            if (board[i * 3] + board[i * 3 + 1] + board[i * 3 + 2] == -3) return 2;

            if (board[i] + board[i + 3] + board[i + 6] == 3) return 1;
            if (board[i] + board[i + 3] + board[i + 6] == -3) return 2;
        }

        // diagonal
        if (board[0] + board[4] + board[8] == 3) return 1;
        if (board[0] + board[4] + board[8] == -3) return 2;

        if (board[2] + board[4] + board[6] == 3) return 1;
        if (board[2] + board[4] + board[6] == -3) return 2;

        // draw
        if (count == 9) return 3;

        // game not over
        return 0;
    }
}
