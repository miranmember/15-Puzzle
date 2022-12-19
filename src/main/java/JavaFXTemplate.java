import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;


public class JavaFXTemplate extends Application {
	int currPuzzle = -1, xPos = 3, yPos = 3, index = 1;
	HashMap<String, Scene> SceneMap = new HashMap<>();
	Button Exit,playAgain;
	Button[][] gameBoard = new Button[4][4];
	Text Title = new Text("15 Puzzle"),Win;
	BorderPane startScreenPane;
	GridPane buttonGrid;
	MenuBar menuBar;
	Menu solveWithAI, options;
	PauseTransition pause = new PauseTransition(Duration.seconds(10));
	Random randPuzzle = new Random();
	EventHandler<ActionEvent> switchButtons;
	ExecutorService ex;
	ArrayList<Node> solution;
	PauseTransition[] pauses = new PauseTransition[10];
	MenuItem seeSolution, solveWithAI1, solveWithAI2, newPuzzle;
	int[][] Puzzles = { {1, 0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15},
						{4, 2, 10, 6, 8, 1, 11, 3, 5, 0, 14, 13, 9, 12, 15, 7},
						{1, 6, 4, 3, 8, 2, 5, 7, 13, 11, 0, 15, 9, 12, 14, 10},
						{8, 6, 3, 14, 9, 10, 2, 13, 15, 1, 5, 7, 11, 12, 4, 0},
						{5, 14, 6, 7, 9, 11, 3, 15, 13, 0, 8, 10, 2, 12, 4, 1},
						{15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0},
						{10, 15, 8, 13, 9, 5, 2, 7, 6, 14, 3, 0, 4, 11, 1, 12},
						{4, 2, 6, 0, 8, 1, 10, 3, 12, 5, 14, 7, 13, 9, 15, 11},
						{12, 5, 4, 2, 7, 14, 8, 3, 9, 6, 10, 0, 13, 15, 1, 11},
						{15, 11, 0, 4, 13, 8, 10, 5, 1, 3, 7, 9, 6, 2, 14, 12} };

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}

	//feel free to remove the starter code from this method
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		pause.play();
		SceneMap.put("StartScreen",startScreen());
		switchButtons = actionEvent -> {
			Button B = (Button) actionEvent.getSource();
			switchButton(B);
			if (isWinning()) {
				primaryStage.setScene(SceneMap.get("endScreen"));
				primaryStage.setTitle("Won");
			}
		};
		SceneMap.put("gameScreen",gameScreen());
		SceneMap.put("endScreen",endScreen());
		primaryStage.setTitle("Welcome to JavaFX");
		primaryStage.setScene(SceneMap.get("StartScreen"));
		primaryStage.show();

		pause.setOnFinished(e-> {
			primaryStage.setScene(SceneMap.get("gameScreen"));
			primaryStage.setTitle("15 Puzzle");
		});

		seeSolution.setOnAction(a->{
			solveWithAI1.setDisable(true);
			solveWithAI2.setDisable(true);
			seeSolution.setDisable(true);
			buttonGrid.setDisable(true);
			buttonGrid.setOpacity(1);
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					gameBoard[i][j].setOpacity(1);
				}
			}
			menuBar.setDisable(true);

			for (int i = 0; i < 10; i++) {
				pauses[i] = new PauseTransition(Duration.seconds(1));
			}

			pauses[0].play();

			for (int i = 0; i < 10; i++) {
				int finalI = i;
				pauses[i].setOnFinished(e-> {
					changeGameBoard(solution.get(index++).getKey());
					if (isWinning()) {
						setDefault();
						Win.setText("AI Solved The Puzzle");
						primaryStage.setScene(SceneMap.get("endScreen"));
						return;
					}
					if (finalI < 9) {
						pauses[finalI + 1].play();
					} else {
						setDefault();
					}
				});
			}

		});

		newPuzzle.setOnAction(e-> {
			reset();
		});

		playAgain.setOnAction(e-> {
			reset();
			primaryStage.setScene(SceneMap.get("gameScreen"));
			primaryStage.setTitle("15 Puzzle");
		});
	}

	public Scene startScreen() {
		Title.setText("Welcome to 15 Puzzle Game\n\n\n" +
				"INSTRUCTIONS\n" +
				"Move tiles in grid to order them from 1 to 15. The empty spot should end up at TOP-LEFT corner\n" +
				"To move a tile you can click on it\n" +
				"Numbered tile can only be swapped with empty tile\n" +
				"You can also view the solution to puzzle using Solution Menu\n" +
				"Clicking on \"Solve with AI H1\" or \"Solve with AI H2\" runs the heuristic AI that solves the puzzle\n" +
				"After that clicking on \"See Solution\" solves the puzzle for you 10 moves at a time");
		Title.setStyle("-fx-font-size: 18; -fx-font-family: 'Comic Sans MS';");
		Title.setFill(Color.WHITE);
		Title.setTextAlignment(TextAlignment.CENTER);
		VBox startScreenVBox = new VBox(10,Title);
		startScreenVBox.setAlignment(Pos.CENTER);
		startScreenPane = new BorderPane(startScreenVBox);
		startScreenPane.setStyle("-fx-background-color: #003333");
		return new Scene(startScreenPane, 900, 600);
	}

	public Scene gameScreen() {
		makeGameBoard();
		makeMenu();
		BorderPane pane = new BorderPane(buttonGrid, menuBar, null, null, null );
		pane.setStyle("-fx-background-color: #003333");
		return new Scene(pane, 500, 500);
	}

	private void makeMenu() {
		menuBar = new MenuBar();
		menuBar.setStyle("-fx-background-color: pink");
		solveWithAI = new Menu("Solution");
		options = new Menu("Options");


		solveWithAI1 = new MenuItem("Solve with AI H1");
		solveWithAI2 = new MenuItem("Solve with AI H2");
		seeSolution = new MenuItem("See Solution");
		seeSolution.setDisable(true);

		solveWithAI1.setOnAction(e->{
			solveAI("heuristicOne");
			index = 1;
			seeSolution.setDisable(false);
		});

		solveWithAI2.setOnAction(e->{
			solveAI("heuristicTwo");
			index = 1;
			seeSolution.setDisable(false);
		});

		solveWithAI.getItems().addAll(solveWithAI1, solveWithAI2, seeSolution);

		newPuzzle = new MenuItem("New Game");
		MenuItem exit = new MenuItem("Exit");

		exit.setOnAction(actionEvent -> {Platform.exit(); System.exit(0);});

		options.getItems().addAll(newPuzzle, exit);

		menuBar.getMenus().addAll(solveWithAI, options);
	}

	public void makeGameBoard() {
		buttonGrid = new GridPane();
		buttonGrid.setStyle("-fx-background-color: #062222;");
		int randInt = randPuzzle.nextInt(10);
		while (currPuzzle == randInt) {
			randInt = randPuzzle.nextInt(10);
		}
		currPuzzle = randInt;
		String str;
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (Puzzles[currPuzzle][counter] == 0) {
					str = "0";
					xPos = i;
					yPos = j;
				} else {
					str = String.valueOf(Puzzles[currPuzzle][counter]);
				}
				Button B = new Button(str);
				B.setMinSize(75,75);
				if (str.equals("0")) {
					B.setStyle("-fx-text-fill: transparent; -fx-background-color: #006666;");
				} else {
					B.setStyle("-fx-background-color: pink;  -fx-font-size: 20; -fx-font-family: 'Comic Sans MS';");
				}
				B.setFocusTraversable(false);
				B.setOnAction(switchButtons);
				gameBoard[i][j] = B;
				buttonGrid.add(gameBoard[i][j], j, i);
				counter++;
			}
		}
		buttonGrid.setVgap(5);
		buttonGrid.setHgap(5);
		buttonGrid.setAlignment(Pos.CENTER);
	}

	public Scene endScreen() {
		Win = new Text("You Win");
		Win.setTextAlignment(TextAlignment.CENTER);
		Win.setStyle("-fx-font-size: 18; -fx-font-family: 'Comic Sans MS';");
		Win.setFill(Color.WHITE);
		Exit = new Button("Exit");
		Exit.setOnAction(actionEvent -> {Platform.exit(); System.exit(0);});
		Exit.setStyle("-fx-font-size: 15; -fx-font-family: 'Comic Sans MS';");
		Exit.setMinWidth(100);
		playAgain = new Button("Play Again");
		playAgain.setStyle("-fx-font-size: 15; -fx-font-family: 'Comic Sans MS';");
		playAgain.setMinWidth(100);
		VBox endScreenVBox = new VBox(20, Win, playAgain, Exit);
		endScreenVBox.setStyle("-fx-background-color: #062222");
		endScreenVBox.setAlignment(Pos.CENTER);
		return new Scene(endScreenVBox, 500,500);
	}

	public void switchButton(Button B) {
		int currX = -999, currY = -999;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (gameBoard[i][j] == B) {
					currX = i;
					currY = j;
				}
			}
		}

		if ((currX == xPos+1 && currY == yPos) || (currX == xPos-1 && currY == yPos) || (currX == xPos && currY == yPos+1) || (currX == xPos && currY == yPos-1)) {
			gameBoard[xPos][yPos].setStyle("-fx-text-fill: BLACK; -fx-background-color: pink;  -fx-font-size: 20; -fx-font-family: 'Comic Sans MS';");
			gameBoard[xPos][yPos].setText(B.getText());
			gameBoard[currX][currY].setText("0");
			gameBoard[currX][currY].setStyle("-fx-text-fill: transparent; -fx-background-color: #006666;");
			xPos = currX;
			yPos = currY;
			seeSolution.setDisable(true);
		}
	}

	public boolean isWinning() {
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (Integer.parseInt(gameBoard[i][j].getText()) == counter){
					counter++;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private void solveAI(String heuristic) {
		buttonGrid.setDisable(true);
		buttonGrid.setOpacity(1);
		menuBar.setDisable(true);
		ex = Executors.newFixedThreadPool(5);
		ex.submit(()->{
			ExecutorService ex1 = Executors.newFixedThreadPool(5);
			Future<ArrayList<Node>> future = ex1.submit(new MyCall(gameBoard, heuristic));
			while (true) {
//				 try {
//					 solution = future.get();
//				 } catch (Exception e) {
//					 e.printStackTrace();
//				 }
				if (future.isDone()) {
					try {
						solution = future.get();
						buttonGrid.setDisable(false);
						menuBar.setDisable(false);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					break;
				}
			}
		});
		ex.shutdown();
	}

	public void setDefault() {
		solveWithAI1.setDisable(false);
		solveWithAI2.setDisable(false);
		seeSolution.setDisable(false);
		buttonGrid.setDisable(false);
		menuBar.setDisable(false);
	}
	public void changeGameBoard(int[] solutionKey) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				String str = String.valueOf(solutionKey[(i*4)+j]);
				gameBoard[i][j].setText(str);
				if(str.equals("0")) {
					gameBoard[i][j].setStyle("-fx-text-fill:transparent; -fx-background-color: #006666;");
					xPos = i;
					yPos = j;
					seeSolution.setDisable(true);
				} else {
					gameBoard[i][j].setStyle("-fx-text-fill:black; -fx-background-color: pink;  -fx-font-size: 20; -fx-font-family: 'Comic Sans MS';");
				}
			}
		}
	}

	public void reset() {
		int randInt = randPuzzle.nextInt(10);
		while (currPuzzle == randInt) {
			randInt = randPuzzle.nextInt(10);
		}
		currPuzzle = randInt;
		String str;
		int counter = 0;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (Puzzles[currPuzzle][counter] == 0) {
					str = "0";
					xPos = i;
					yPos = j;
					gameBoard[i][j].setStyle("-fx-text-fill: transparent; -fx-background-color: #006666;");
				} else {
					str = String.valueOf(Puzzles[currPuzzle][counter]);
					gameBoard[i][j].setStyle("-fx-text-fill: BLACK; -fx-background-color: pink;  -fx-font-size: 20; -fx-font-family: 'Comic Sans MS';");
				}
				gameBoard[i][j].setText(str);
				counter++;
			}
		}
		solveWithAI1.setDisable(false);
		solveWithAI2.setDisable(false);
		seeSolution.setDisable(false);
		buttonGrid.setDisable(false);
		seeSolution.setDisable(true);
		Win.setText("You win");
	}
}
