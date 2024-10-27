# basic-tic-tac-toe

A Basic Tic-Tac-Toe game to learn about Java Swing GUI, Socket Programming and Multi-threading


## Prerequisite

The machine running this game should have port 26901 available for the program. If any other programs are running on this port, it may result in the malfunction of the game.


## Procedures

1. Start `TicTacToeServer.java`.
2. Start 2 instances of `TicTacToeClient.java`.
3. (Optional) Enter your name on the client GUI, the client which clicks submit first will be the first to start the game.
4. When clicking on the board, try to click on the center of the board to ensure the GUI reads your input.
5. After the game ends or the winning conditions are met, a message dialog will appear on the window stating which client wins/loses/draws. Closing the dialog will automatically close the game.
6. For instructions on how to play the game, navigate to "Help" -> "Instructions" in the menu bar of the client GUI.
7. If one of the client windows is closed (by clicking "Control" -> "Exit" or directly closing the window), the other client will receive a message dialog stating that the other client has disconnected.


## Known bugs

The game does not support 3 or more clients opening at the same time. Only the first 2 clients that clicked the "Submit" button will be connected by the server.
