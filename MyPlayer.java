import java.util.Random;
import java.util.ArrayList;

/**
 * University of San Diego
 * COMP 285: Spring 2016
 * Instructor: Gautam Wilkins
 *
 * Implement your Connect Four player class in this file.
 */

public class MyPlayer extends Player
{
    double start;
    double CUTOFF = 6;
    double weightedMatrix[][] = {{.04, .06, .07, .10, .07, .06, .04}, {.06, .08, .11, .14, .11, .08, .06}, {.07, .11, .15, .18, .15, .11, .07}, {.10, .14, .18, .22, .18, .14, .10}, {.07, .11, .15, .18, .15, .11, .07},
            {.06, .08, .11, .14, .11, .08, .06}, {.04, .06, .07, .10, .07, .06, .04}}
        ;

    // the weighted matrix displays how many possible winning combinations of any given slot possess
    // Example. Corners can have only three different winning combos (vertical, horizontal, and diagonal)

    /* try this. 72 total ways to win. So tiles in other matrix below divided by 72
    to find the percentage of wins that tile is in
    believe this is a better representation each tile as it gives percentage of win
    {.04, .06, .07, .10, .07, .06, .04}
    {.06, .08, .11, .14, .11, .08, .06}
    {.07, .11, .15, .18, .15, .11, .07}
    {.10, .14, .18, .22, .18, .14, .10}
    {.07, .11, .15, .18, .15, .11, .07}
    {.06, .08, .11, .14, .11, .08, .06}
    {.04, .06, .07, .10, .07, .06, .04}
     */

    /* new
    works better b/c we actually found values to correspond with each position instead of guessing
    {.03, .04, .05, .07, .05, .04, .03}
    {.04, .06, .08, .10, .08, .06, .04}
    {.05, .08, .11, .13, .11, .08, .05}
    {.07, .10, .13, .16, .13, .10, .07}
    {.05, .08, .11, .13, .11, .08, .05}
    {.04, .06, .08, .10, .08, .06, .04}
    {.03, .04, .05, .07, .05, .04, .03}
     */

    /* old
    {.03, .04, .05, .06, .05, .04, .03}
    {.04, .05, .07, .08, .07, .05, .04}
    {.05, .06, .07, .08, .07, .06, .05}
    {.06, .07, .08, .09, .08, .07, .06}
    {.05, .06, .07, .08, .07, .06, .05}
    {.04, .05, .07, .08, .07, .05, .04}
    {.03, .04, .05, .06, .05, .04, .03}
     */

    public void setPlayerNumber(int number) {
        this.playerNumber = number;
    }

    public int chooseMove(Board gameBoard)
    {
        start = System.nanoTime();
        Move optimalMove = new Move(0, 0.0);
        int depth;
        depth = 1;
        while (true)
        {
            try
            {
                optimalMove = alphaBeta(gameBoard, depth, this.playerNumber, -1.0, 1.0, 0);
            } catch (Exception e)
            {
                break;
            }
            depth++;
        }
        double difference;
        double elapsed;
        difference = System.nanoTime()-start;
        elapsed = (double)difference/1e9;
        System.out.println("Elapsed Time: " + elapsed + " second(s)");
        System.out.println("Optimal Value: " + optimalMove.value);
        return optimalMove.column;
    }

    public Move alphaBeta(Board board, int depth, int playerNum, double a, double b, int lastMoveColumn) throws Exception
            // in this method you can find the minimax, iterative deepening, and also pruning
    {
        double elapsed = (System.nanoTime()-start) / 1e9;
        if (elapsed > CUTOFF)
        {
            throw new Exception();
        }
        int otherPlayer;
        otherPlayer = (playerNum == 1 ? 2 : 1);
        int gameState;
        if(board.isEmpty())
        {
            return new Move(3, 0);
        }
        if ((gameState = board.checkIfGameOver(lastMoveColumn)) != -1)
        {
            double result;
            result = (gameState == 2 ? -1 : gameState);
            return new Move(lastMoveColumn, result);
        }
        if (depth == 0)
        {
            return new Move(0, Heuristic(board, playerNum, getLastMoveRow(board, lastMoveColumn), lastMoveColumn ));
        }
        if (playerNum == 1)
        {
            Move bestMove = null;
            for (int i = 0; i < 7; i++) {
                if (board.isColumnOpen(i)) {
                    board.move(playerNum, i);
                    Move result = alphaBeta(board, depth - 1, otherPlayer, a, b, i);
                    result.column = i;
                    if (bestMove == null || result.value > bestMove.value) {
                        bestMove = result;
                    }
                    if (result.value > a) {
                        a = result.value;
                    }
                    board.undoMove(i);
                    if (b <= a) {
                        break;
                    }
                }
            }
            return bestMove;
        }
        else
        {
            Move bestMove = null;
            for (int i = 0; i < 7; i++)
            {
                if (board.isColumnOpen(i))
                {
                    board.move(playerNum, i);
                    Move result = alphaBeta(board, depth - 1, otherPlayer, a, b, i);
                    result.column = i;
                    if (bestMove == null || result.value < bestMove.value)
                    {
                        bestMove = result;
                    }
                    if(result.value < b)
                    {
                        b = result.value;
                    }
                    board.undoMove(i);
                    if (b<=a)
                    {
                        break;
                    }
                }
            }
            return bestMove;
        }
    }

    public double Heuristic(Board gameBoard, int playerNumber, int row, int column)
    {
        double score;
        score = 0;
        if (playerNumber==1)
        {
            if (gameBoard.checkIfGameOver(column)==1)
            {
                score = 1;
                return score;
            }
            if (gameBoard.checkIfGameOver(column)==2)
            {
                score = -1;
                return score;
            }
            if (gameBoard.checkIfGameOver(column)==0)
            {
                score = 0;
                return score;
            }
            score = score + (weighting(gameBoard, playerNumber));
        }
        else
        {
            if (gameBoard.checkIfGameOver(column)==1)
            {
                score = -1;
                return score;
            }
            if (gameBoard.checkIfGameOver(column)==2)
            {
                score = 1;
                return score;
            }
            if (gameBoard.checkIfGameOver(column)==0)
            {
                score = 0;
                return score;
            }
            score = score + -1 * weighting(gameBoard, playerNumber);
        }
        return score;
    }

    private double weighting(Board board, int playerNumber)
    {
        double player1score;
        double player2score;
        player1score = 0.0D;
        player2score = 0.0D;

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (board.getBoard()[i][j] == playerNumber) {
                    player1score = player1score + weightedMatrix[i][j];
                } else if (board.getBoard()[i][j] != 0) {
                    player2score = player2score + weightedMatrix[i][j];
                }
            }
        }
        return (player1score - player2score)/(player1score + player2score);
    }

    private int getLastMoveRow(Board gameBoard, int lastMoveColumn) {
        if (lastMoveColumn < 0 || lastMoveColumn >= 7) {
            return -1;
        }

        int lastMoveRow = -1;
        for (int i=7-1; i>=0; i--)
        {
            if (gameBoard.getBoard()[i][lastMoveColumn] != 0)
            {
                lastMoveRow = i;
                break;
            }
        }
        return lastMoveRow;
    }
}

class Move extends MyPlayer
{
    public double value;
    public int column;
    public Move(int newColumn, double newValue)
    {
        column = newColumn;
        value = newValue;
    }
}
