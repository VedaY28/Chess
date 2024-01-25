# 2-Player Chess

Simulate chess utilizing the terminal that outputs the board every time after a corresponding move has been made. 

Implemented a game of Chess for two players. When program is launched, in the terminal a board is drawn in text, and prompts whomever's turn it is (white or black) for a move. Once the move is executed, the move will be be played and a new board is drawn, and the other player queried.

Start the Game by running the PlayChess.java file

## Commands

- **FileRank FileRank** EX: "e2 e4"
  - First file (column) and rank (row) are the coordinates of the piece to be moved, and the second file and rank are the coordinates of where it should end up
- **resign**
  - A player may resign by entering "resign"
- **draw?**
  - A player may offer a draw by appending "draw?" to the end of an otherwise regular move
- **quit**
  - Forcefully ends the game in the terminal and to restart run PlayChess.java
