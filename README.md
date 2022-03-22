SCRABBLE

Classes

- Trie Data Structure:
  - Read in lexicon and build trie (print in-order to test)
  
- Board:
  - Squares with:
    - Cross-check "bit vector", change over time
    - Score multipliers, null after first use
    - Read from input and build 2D array of board squares
  
- Tiles
  - Letter value
  - Bit vector index?
  - Point value
  - Make however many of each, put in stack, shuffle and pop

- Player
  - Ability to play word
  - Check valid word, return tiles to rack if invalid
  - Keep track of overall score and current word score
  - Automatically refill used tiles in rack

- Solver
  - LeftPart(), ExtendRight(), LegalMove()
  - Recursive backtracking
  - Magic

- Main Game
  - Alternate turns between players
  - End game state


TO DO:

- Player class
  - Rework some methods (cross-checks)
  - Allow player to try entire word before shutting them down

- Game
  - Link player and solver, alternating turns
  - Add end game state
  - Make GUI

- GUI
  - Add play area: gridpane, text boxes, buttons, colored squares
  - Buttons: squares, tiles, down/across, reset, shuffle tiles
  - Text: Scores, tiles in bag, tile letters, square multipliers (DW, DL, TL, TW, *)
  - Scenes: main game, win screen
  - Ideas: different tile colors for different players? 
           win screen displays played words & points for each player?

-Future changes
  - Apply following run command: java -jar scrabble.jar sowpods.txt < input.txt > output.txt