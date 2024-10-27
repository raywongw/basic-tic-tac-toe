import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
 * This class creates an instance of TicTacToeClient and communicates with a TicTacToeServer
 * This class implements Runnable for multithreading and also sets up the GUI for the client.
 */
public class TicTacToeClient extends JFrame implements Runnable {

    private static final long serialVersionUID = 1L;

    private final JLabel[] labels = new JLabel[9];
    private PrintWriter writer;
    private final JLabel notificationLabel = new JLabel("Waiting for another player to join...");


    /**
     * This method is the main method that starts the client.
     */
    public void setupGUI(){
        JTextField tf = new JTextField(30);
        final JButton submit = new JButton("Submit");
        final JMenuItem exit = new JMenuItem("Exit");
        JMenuItem instruction = new JMenuItem("Instruction");
        JPanel notificationPanel = new JPanel();
        JPanel boardPanel = new JPanel();
        JPanel namePanel = new JPanel();

        this.setLayout(new BorderLayout());
        // action listeners
        exit.addActionListener(new ActionListener() {
            @Override
            /**
             * This method is for the exit menu item to exit the game.
             */
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        instruction.addActionListener(new ActionListener() {
            @Override
            /**
             * This method is for the instruction menu item to display the instruction of the game.
             */
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Some information about the game: \nCriteria for a valid move: \n-The move is not occupied by any mark. \n-The move is ade in the player's turn\n-The move is made within the 3 x 3 board.\nThe game would continue and switch among the opposite player until it reaches either one of the following conditions:\n- Player 1 wins\n- Player 2 wins\n- Draw");
            }
        });

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);
        JMenu controlMenu = new JMenu("Control");
        menuBar.add(controlMenu);
        JMenu helpMenu = new JMenu("Help");
        menuBar.add(helpMenu);
        controlMenu.add(exit);
        helpMenu.add(instruction);
        this.notificationLabel.setText("Enter your name and click submit...");

        submit.addActionListener(new ActionListener() {
            @Override
            /**
             * This method is for the submit button to submit the name of the player.
             */
            public void actionPerformed(ActionEvent e) {
                System.out.println("Submit button clicked"); // print statement to see if submit button is working correctly
                submit.setEnabled(false);
                tf.setEnabled(false);
                notificationLabel.setText("WELCOME " + tf.getText() + "! Waiting for another player to join...");
                setTitle("Tic Tac Toe-Player: " + tf.getText());
                Thread t = new Thread(TicTacToeClient.this);
                t.start();
            }
        });

        // set up board
        boardPanel.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++){
                labels[i * 3 + j] = new JLabel(" ", SwingConstants.CENTER);
                labels[i * 3 + j].setFont(new Font("Arial", Font.BOLD, 100));
                labels[i * 3 + j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                labels[i * 3 + j].setBackground(Color.WHITE);
                labels[i * 3 + j].setEnabled(false);
                int finalI = i;
                int finalJ = j;
                labels[i * 3 + j].addMouseListener(new MouseListener() {
                    @Override
                    /**
                     * This method is for the player to make a move and send the move to the server.
                     */
                    public void mouseClicked(MouseEvent e) {
                        writer.println("MOVE "+ (finalI * 3 + finalJ));
                        System.out.println("Sent message to server: MOVE " + (finalI * 3 + finalJ)); // print statement to see if message is being sent to server
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {

                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {

                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
                // add label to board
                boardPanel.add(labels[i * 3 + j]);
            }

        }


        this.setJMenuBar(menuBar);

        namePanel.add(tf);
        namePanel.add(submit);
        this.add(boardPanel, BorderLayout.CENTER);

        this.add(namePanel, BorderLayout.SOUTH);


        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(500, 600);
        this.setVisible(true);
        this.add(notificationPanel, BorderLayout.NORTH);
        notificationPanel.add(notificationLabel);
        this.setTitle("Tic Tac Toe");
    }

    /**
     * This method is for the thread to communicate with the server and update the GUI accordingly.
     */
    public void run() {
        // connect to server
        try {
            Socket socket = new Socket("127.0.0.1", 26901);
            System.out.println("Connected to server"); // print statement to see if client is connecting to server correctly
            writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            for (int i = 0; i < 9; i++) {
                labels[i].setEnabled(false); // disable the board until it's the player's turn
            }

            // receive command from server
            while (true) {
                String response = reader.readLine();
                System.out.println("Received message from server: " + response); // print statement to see if message is being received from server
                String[] tokens = response.split(" ");


                if (tokens[0].equals("WINNER")) {
                    JOptionPane.showMessageDialog(this, "Congratulations! You win!");
                    System.exit(0);
                }
                if (tokens[0].equals("LOSER")) {
                    JOptionPane.showMessageDialog(this, "You lose.");
                    System.exit(0);

                }
                if (tokens[0].equals("TIE")) {
                    JOptionPane.showMessageDialog(this, "Draw.");
                    System.exit(0);

                }
                if (tokens[0].equals("DISCONNECTED")) {
                    JOptionPane.showMessageDialog(this, "The other player has disconnected.");
                    System.exit(0);

                }

                if (tokens[0].equals("BOARD")) {
                    String board = tokens[1];
                    for (int i = 0; i < 9; i++) {
                        if (board.charAt(i) != '_') labels[i].setText(board.substring(i, i + 1));
                    }
                }
                if (tokens[0].equals("MESSAGE")) {
                    String message = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
                    notificationLabel.setText(message);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Main method to run the client.
     */
    public static void main(String[] args) {
        TicTacToeClient client = new TicTacToeClient();
        client.setupGUI();
    }
}