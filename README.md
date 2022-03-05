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

- Computer/Solver
  - LeftPart(), ExtendRight(), LegalMove()
  - Recursive backtracking
  - Magic

- Main Game
  - Alternate turns between players
  - End game state


TO DO

- Player class
- 'play' words with tiles and verify
- add bland tile functionality


Future changes
- dictionary file path