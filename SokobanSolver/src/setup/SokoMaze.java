package setup;
import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.function.Executable;

import gui.Direction;
import utils.Utils;

//When setting a new MazeState want the Maze to have its own copy of box locations etc. At the moment
// when the Maze is altered during play the MazeState is also altered : done
public class SokoMaze implements Maze {
	public static final int MAX_ROWS = 20;
	public static final int MAX_COLS = 20;
	
	public static final int MAX_SPACES = 255;   // The maximum number of empty squares inside the maze
	
	static final char SPACE_OUTSIDE_MAZE = ' ';
	static final char SPACE_INSIDE_MAZE = '0';
	static final char WALL = '#';
	static final char PLAYER = '@';
	static final char BOX = '$';
	static final char GOAL_SQUARE = '.';
	static final char BOX_ON_GOAL = '*';    // Box on a goal square
	
	private static SokoMaze instance;
	
	//Implement setNewMaze(MazeState ms) : done
	private int[][] stoneLocs;   	  // r,c of stones
	private int numStones;
	private int numRows;
	private int numCols;
	private int[][] goalLocs;
	private char[][] mazeChars;  	  // The maze with walls etc. Not necessarily rectangular.
	private long numStates;
	private int numSpaces;
	private int[] playerRowCol = new int[2];  // Row and Column of player position
	
	boolean debug = false;
	String errMsg;
	
	boolean[] isGoalNode;	// true if node id = goal node
	int[] from = new int[2];
	int[] to = new int[2];
	int[] dx = {-1, 0,0,1};
	int[] dy = { 0,-1,1,0};

	private Coord[][] coords; 
	
	public static SokoMaze getInstance(MazeState initialState) {
		if(instance == null) {
			instance = new SokoMaze(initialState);
		}
		return instance;
	}
	private SokoMaze(MazeState ms) {
		coords = new Coord[MAX_ROWS][MAX_COLS]; 
		mazeChars = new char[MAX_ROWS][MAX_COLS];
		isGoalNode = new boolean[MAX_SPACES];
		
		setNewMaze(ms);
	}
	//change getDistances(..) so it doesn't keep allocating a new array each call : done
	@Override
	public void getDistances(int startRow, int startCol, int[][] distances, int[] topLeft) {
		
		int r,c;
		topLeft[0] = startRow;
		topLeft[1] = startCol;
		
		if(!inArrayBounds(startRow, startCol, distances)) {
			errMsg = String.format("Invalid start position (%d,%d)", startRow,startCol);
			throw new ArrayIndexOutOfBoundsException(errMsg);
		}
		assumingThat(debug, new Executable() {
			public void execute() throws Throwable {
				if (isOutsideMaze(startRow, startCol)) {
					errMsg = String.format("(row %d, col %d) is outside the maze", startRow,startCol);
					throw new Throwable(errMsg);
					//System.err.println(errMsg);
				}
				if (isWall(startRow, startCol)) {
					errMsg = String.format("A wall is located at position (row %d, col %d)!!!", startRow,startCol);
					throw new Throwable(errMsg);
					//System.err.println(errMsg);
				}
			};
		});
		// Do a BFS (Breadth First Search) on Maze Array
		LinkedList<Coord> queue = new LinkedList<Coord>();
		queue.add(getCoord(startRow,startCol));
		for (int i=0; i<distances.length; i++) {
			Arrays.fill(distances[i], -1);
		}
		distances[startRow][startCol] = 0;
		
		while (!queue.isEmpty()) {
			Coord square = queue.poll();
			for (int i=0; i<dx.length; i++) {
				r = square.row + dy[i];
				c = square.col + dx[i];
				if (distances[r][c] == -1 && !isWall(r,c) && !isBox(r,c)) {
					distances[r][c] = distances[square.row][square.col] + 1;
					if ( (r < topLeft[0]) ||  ((r == topLeft[0]) && (c < topLeft[1]))) {
						topLeft[0] = r;
						topLeft[1] = c;
					}
					//topLeft[0] = r <
					//int a = (r < topLeft[0]) ? (topLeft[0] = r; topLeft[1] = c) :  (r == topLeft[1]) ? 5 : 3;
					
					queue.add(getCoord(r,c));
				}
			}
		}
		// Print distances array
		
		assumingThat(debug, new Executable() {
			public void execute() throws Throwable {
				String outStr = String.format("Distances from (%d,%d)",startRow,startCol);
				System.out.println(outStr);
				
				for (int i=0; i<numRows; i++) {
					String strRow = "";
					for (int j=0; j<numCols; j++) {
						String toAdd = "";
						if(isWall(i,j) || isOutsideMaze(i,j) || isBox(i,j)) {
							toAdd = String.valueOf(mazeChars[i][j]);
						}
						else {
							toAdd = String.valueOf(distances[i][j]);
						}
						if (toAdd.length() == 1) toAdd = "+" + toAdd;
						strRow = strRow + toAdd;
					}
					System.out.println(strRow);
				}
			}
		});
	}
	private boolean inArrayBounds(int startRow, int startCol, int[][] distances) {
		return (startRow < distances.length && startCol < distances[startRow].length);
	}
	@Override
	public int numRows() {
		return numRows;
	}

	@Override
	public int numCols() {
		return numCols;
	}
	
	public boolean isOutsideMaze(int row, int col) {
		return (mazeChars[row][col] == SPACE_OUTSIDE_MAZE);
	}
	
	@Override
	public boolean isWall(int row, int col) {
		return (mazeChars[row][col] == WALL);
	}

	@Override
	public boolean isBox(int row, int col) {
		
		return ((mazeChars[row][col] == BOX) || (mazeChars[row][col] == BOX_ON_GOAL));
	}

	@Override
	public boolean isGoalSquare(int row, int col) {
		boolean found = false;
		
		for (int i=0; i<goalLocs.length && !found; i++) {
			if (goalLocs[i][0] == row && goalLocs[i][1] == col) {
				found = true;
			}
		}
		return found;
	}

	@Override
	/* Returns row and column location of player
	 * (non-Javadoc)
	 * @see setup.Maze#getPlayerLocation()
	 */
	public void getPlayerLocation(int[] coord) {
		coord[0] = playerRowCol[0];
		coord[1] = playerRowCol[1];
	}

	@Override
	public int[][] getBoxLocations() {
		return stoneLocs;
	}

	@Override
	public boolean isEmpty(int row, int col) {
		return ( !isWall(row, col) && !isBox(row, col) );
	}

	@Override
	public void setPlayerLocation(int[] newPos) {
		playerRowCol[0] = newPos[0];
		playerRowCol[1] = newPos[1];
	}

	@Override
	public void setBoxAt(int row, int col) {
		mazeChars[row][col] = BOX;
	}

	@Override
	public void setEmptyAt(int row, int col) {
		if (isGoalSquare(row, col)) {
			mazeChars[row][col] = GOAL_SQUARE;
		}
		else {
			mazeChars[row][col] = SPACE_INSIDE_MAZE;
		}
	}
	/*
	 * Initialise boolean isGoalNode[]
	 */
	private void initIsGoalNode() {
			ArrayList<Integer> goals = new ArrayList<Integer>();
			
			int nEmpty = 0; // No. of empty squares in Maze
			int nRows=numRows(),nCols=numCols();
			for (int r=0; r<nRows; r++) {
				for (int c=0; c<nCols; c++) {
					if (isGoalSquare(r, c)) {
						goals.add(nEmpty);
						nEmpty += 1;
					}
					else if(!isWall(r, c) && !isOutsideMaze(r, c)) {
						nEmpty += 1;
					}
				}
			}
			Arrays.fill(isGoalNode, false);
			for (int id : goals) {
				isGoalNode[id] = true;
			}
		// Debug print isGoalNode
		Utils.printArray(isGoalNode, "isGoalNode", isGoalNode.length);
	}
	@Override
	/* Return array true if node id = goal node
	 * */
	public boolean[] getIsGoalNode() {
		return isGoalNode;
	}

	@Override
	public void moveBox(int row, int col, Direction dirn) {
		setEmptyAt(row,col);
		from[0] = row;
		from[1] = col;
		dirn.getToPosition(from, to);
		setBoxAt(to[0],to[1]);

		boolean updated = false;
		for (int i=0; (i< stoneLocs.length) && !updated; i++) {
			if ( (row == stoneLocs[i][0]) && (col == stoneLocs[i][1]) ) {
				stoneLocs[i][0] = to[0];
				stoneLocs[i][1] = to[1];
				updated = true;
			}
		}
	}

	@Override
	public void movePlayer(Direction dirn) {
		int pr = playerRowCol[0];
		int pc = playerRowCol[1];
		setEmptyAt(pr,pc);
		from[0] = pr; from[1] = pc;
		dirn.getToPosition(from, playerRowCol);
	}
	// test "Start Again" bug : fixed 
	@Override
	public void setBoxLocations(int[][] coords) {
		for (int[] oldCoord : stoneLocs) {
			setEmptyAt(oldCoord[0], oldCoord[1]);
		}
		for (int i=0; i<stoneLocs.length; i++) {
			for (int j=0; j<2; j++) {
				setBoxAt(coords[i][0], coords[i][1]);
				stoneLocs[i][j] = coords[i][j];
			}
		}
	}

	@Override
	public boolean allStonesOnGoals() {
		boolean all = true;
		for (int i=0; i<stoneLocs.length && all; i++) {
			all = isGoalSquare(stoneLocs[i][0], stoneLocs[i][1]);
		}
		return all;
	}
	private Coord getCoord(int r, int c) {
		if (coords[r][c] == null) {
			coords[r][c] = new Coord(r,c);
		}
		return coords[r][c];
	}
	class Coord {
		int row,col;
		
		Coord(int row, int col) {
			this.row = row;
			this.col = col;
		}
	}
	@Override
	public void setNewMaze(MazeState ms) {
		 stoneLocs = Utils.copyArray(ms.stoneLocs);   // r,c of stones
		 numStones = ms.numStones;
		 numRows = ms.numRows;
		 numCols = ms.numCols;
		 goalLocs = ms.goalLocs;
		 //copy mazeChars The maze with walls etc. ms.mazeChars[][] is not necessarily rectangular.
		 Utils.fill(mazeChars, SPACE_OUTSIDE_MAZE);
		 Utils.copyArray(mazeChars, ms.mazeChars);
		 numStates = ms.numStates;
		 numSpaces = ms.numSpaces;
		 playerRowCol = Arrays.copyOf(ms.player, 2);	// Row and Column of player position

		 initIsGoalNode();
	}
}
