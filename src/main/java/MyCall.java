import javafx.scene.control.Button;
import java.util.ArrayList;
import java.util.concurrent.*;

public class MyCall implements Callable<ArrayList<Node>> {
    Button[][] board;
    int[] currState = new int[16];
    String whichHeuristic;

    MyCall(Button[][]game, String heuristic){
        board = game;
        GameBoardToArray(board);
        whichHeuristic = heuristic;
    }

    private void GameBoardToArray(Button[][] game) {
        int counter = 0;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                currState[counter] = Integer.parseInt(game[i][j].getText());
                counter++;
            }
        }
    }

    @Override
    public ArrayList<Node> call() throws Exception {
        Node startState = new Node(currState);
        A_IDS_A_15solver ids = new A_IDS_A_15solver(startState);
        return ids.A_Star(startState, whichHeuristic);
    }

}
