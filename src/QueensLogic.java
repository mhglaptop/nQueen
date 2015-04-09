/**
 * This class implements the logic behind the BDD for the n-queens problem
 * You should implement all the missing methods
 * 
 * @author Stavros Amanatidis
 *
 */

import net.sf.javabdd.*;

public class QueensLogic {
    private int x = 0;
    private int y = 0;
    private int[][] board;


    private BDDFactory factory;
    private BDD True;
    private BDD False;
    private BDD bdd;
    private int N;


    public QueensLogic() {
       //constructor
    }

    public void initializeGame(int size) {
        this.x = size;
        this.y = size;
        this.N = size;
        this.board = new int[x][y];
        System.out.println("This is a " + N + "*" +N+" board");

        //Initialize the factory with a cache
        this.factory = JFactory.init(2000000, 200000);

        //Create N*N variables.
        this.factory.setVarNum(this.N*this.N);

        // Create true and false nodes.
        this.False = this.factory.zero();
        this.True = this.factory.one();

        //Initalize our bdd to true
        this.bdd = True;

        for (int i = 0; i< N;i++){
            for(int j=0; j< N; j++) {
                boardRules(i,j);
                System.out.println("Rules added for " +i + ";" +j);
                System.out.println("Is variable: " + getVariable(i,j));
            }
        }

        // There must be one queen pr row.
        for (int i = 0; i< N;i++){
            BDD onePrRow = False;
            for(int j=0; j< N; j++) {
               onePrRow = onePrRow.or(this.factory.ithVar(getVariable(i,j)));
            }
            this.bdd.andWith(onePrRow);
        }
    }

    // Rules taken from http://javabdd.sourceforge.net/xref/NQueens.html
    // x, y correspond to the specific cell we are looking at.
    private void boardRules(int x, int y){
        BDD restFalse = True;
        BDD cellBDD = False;

        // No queen must be placed in the same row
        for (int i = 0; i < N; i++){
            if (i != y) {
                // All other cells must be false, ie no queen is placed in the cell
                restFalse = restFalse.and(this.factory.nithVar(getVariable(x,i)));
            }
        }

        // No queen must be placed in the same column
        for (int i = 0; i < N; i++){
            if (i != x) {
                // All other cells must be false, ie no queen is placed in the cell
                restFalse = restFalse.and(this.factory.nithVar(getVariable(i,y)));
            }
        }

        int current = getVariable(x,y);
        // Up left check, this is done my shifting variable with a N+1 interval X times
        int uCount = 1;
        while (uCount <= x) {
            current-=N+1;
            if (current >= 0) {
                restFalse = restFalse.and(this.factory.nithVar(current));
            }
            uCount++;
        }


        // Down left check, this is done my shifting variable with a N-1 interval X times.
        int dCount = 1;
        current = getVariable(x,y);
        while (dCount <= x) {
            current+=N-1;
            if (current < N*N) {
                restFalse = restFalse.and(this.factory.nithVar(current));
            }
            dCount++;
        }

        // If a queen is placed in this cell then this most be true
        cellBDD = cellBDD.or(restFalse);
        // Otherwise it must be true that no queen is placed at this location
        cellBDD = cellBDD.or(this.factory.nithVar(getVariable(x,y)));

        // This cell must be true for the BBD to be true
        this.bdd = this.bdd.and(cellBDD);
    }

    private int getVariable(int x, int y){
        return N*y+x;
    }

    public int[][] getGameBoard() {
        return board;
    }

    public boolean insertQueen(int column, int row) {

        if (board[column][row] == -1 || board[column][row] == 1) {
            return true;
        }

        System.out.println("Queen placed in " + column + ":" + row);

        board[column][row] = 1;

        // Place the queen in the our BDD
        this.bdd = this.bdd.restrict(this.factory.ithVar(getVariable(column, row)));
        System.out.println("Placeing a queen in bdd variable: " + getVariable(column,row));

        System.out.println("Updating rules");
        // put some logic here..
        // Update board with invalid placements
        for (int i = 0; i< N;i++){
            for(int j=0; j< N; j++) {
                BDD test = this.bdd.restrict(this.factory.ithVar(getVariable(i,j)));
                if (test.isZero()) {
                    System.out.println("A queen cannot be placed in field " + i + ":" +j);
                    System.out.println("Is variable: " + getVariable(i,j));
                    board[i][j] = -1;
                }
            }
        }

        return true;
    }
}
