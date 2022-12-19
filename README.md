# 15-Puzzle
A 15 puzzle game implemented using JavaFX.

## Features
+ Choose from 10 different starting configurations or shuffle the current configuration to create a new puzzle.
+ Solve the puzzle using one of two different algorithms: breadth-first search or A* search with the Manhattan distance heuristic.
+ View the solution by stepping through each move or let the AI solve the puzzle automatically.
+ Exit the game or play again from the end screen.

## How to Run
1. Clone the repository to your local machine.
2. Compile and run using
```
mvn clean
mvn compile
mvn exec:java
```
3. From the start screen, click on the "Start" button to begin the game.
4. Click on any button adjacent to the blank space to move it.
5. Use the "Solve with AI" menu in the top menu bar to select an AI solving algorithm and view the solution.
6. Use the "Options" menu in the top menu bar to shuffle the current puzzle or choose a new starting configuration.
7. Click on the "Exit" button or use the "Exit" option in the "Options" menu to close the game.
8. Click on the "Play Again" button on the end screen to play a new game.

## Implementation Details
The game board is represented using a 2D array of Button objects. The blank space is represented by a button with an empty string label. The puzzle is solved when the buttons are in the correct order from left to right, top to bottom, with the blank space at the bottom right.

Two different algorithms are implemented for solving the puzzle: breadth-first search and A* search with the Manhattan distance heuristic. The breadth-first search algorithm is implemented using a queue, while the A* search algorithm is implemented using a priority queue. The solution for each algorithm is stored in an ArrayList of Node objects, where each Node represents a state of the puzzle and the list is used to step through the solution or solve the puzzle automatically. Thank you Dr.Mark Hallenbeck for providing the Algorithms for this project.

The game is implemented using JavaFX and the user interface is created using a combination of BorderPane, GridPane, MenuBar, and VBox layouts. Event handlers are used to handle user input and update the game state. A HashMap is used to store the different scenes and switch between them as needed.


# Example
![Example 1](https://user-images.githubusercontent.com/103700248/208361156-cdbf9aa3-ab7c-45f9-a053-7436499a6caa.png)
