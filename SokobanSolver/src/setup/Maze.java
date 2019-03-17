package setup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import gui.Direction;

public interface Maze {
	public void getDistances(int startRow, int startCol, int[][] distances, int[] topLeft); 
	public boolean isOutsideMaze(int row, int col);
	public boolean isWall(int row, int col);
	public boolean isBox(int row, int col);
	public boolean isGoalSquare( int row, int col);
	public boolean isEmpty(int row, int col);
	public void getPlayerLocation(int[] coord);
	public void setPlayerLocation(int[] pos);
	public int[][] getBoxLocations();
	public void setBoxLocations(int[][] coords);
	public int numRows();
	public int numCols();
	public void setBoxAt(int row, int col);
	public void moveBox(int row, int col, Direction dirn);
	public void movePlayer(Direction dirn);
	public void setEmptyAt(int row, int col);
	public boolean[] getIsGoalNode();    // true for nodeId = a goal node
	public boolean allStonesOnGoals();
	public void setNewMaze(MazeState ms);
	
	/*
	 * This method should be in either the Reader class or SokoMaze class 
	 * Note: mazeChars[][] is not necessarily rectangular
	 */
	public static MazeState parseMazeChars(char[][] mazeChars) {
		 int[] player = new int[2]; // Row and Column of player position
		 int[][] stoneLocs;         // r,c of stones
		 int numStones;
		 int numRows = mazeChars.length;
		 int numCols = 0;
		 int[][] goalLocs;
		 int numSpaces = 0;
		 int[] dx = {-1,  0, 0, 1};
		 int[] dy = { 0, -1, 1, 0};
		 player[0]=-1;
		 player[1]=-1;
		 boolean playerFound = false;
		 ArrayList<int[]> stones = new ArrayList<int[]>();
		 ArrayList<int[]> goals = new ArrayList<int[]>();
		 boolean[][] visited = new boolean[numRows][];
		 
		 // find player 
		 for(int r=0; r<mazeChars.length; r++) {
			 visited[r] = new boolean[mazeChars[r].length];
			 Arrays.fill(visited[r], false);
			 numCols = (mazeChars[r].length > numCols) ? mazeChars[r].length : numCols;
			 if (!playerFound) {
				 player[1] = indexOf(mazeChars[r], SokoMaze.PLAYER);
				 player[0] = r;
				 playerFound = (player[1] >= 0);
			 }
		 }
	    /* 
	     * Fill interior with '0' to denote a square inside the maze. Algorithm expects this.
	     * Use Breadth First Search to iterate over all squares inside the maze.
	     */
		Deque<int[]> queue = new LinkedList<int[]>();
		mazeChars[player[0]][player[1]] = SokoMaze.SPACE_INSIDE_MAZE;
		numSpaces = 1;
		visited[player[0]][player[1]] = true;
		queue.add(player);
		int[] child;
		int r,c;
		char ch;
		while (!queue.isEmpty()) {
			int[] square = queue.poll();
			for (int i=0; i< dx.length; i++) {
				// Don't need to check if outside array bounds because we are checking squares
				// inside the maze with a wall as a boundary
				r = square[0] + dy[i];
				c= square[1] + dx[i];
				ch = mazeChars[r][c];
				if (!visited[r][c] && (ch != SokoMaze.WALL)) {
					
					child = addChild(r, c, queue, visited);
					numSpaces += 1;
					
					switch(ch) {
					case SokoMaze.BOX_ON_GOAL:
						goals.add(child);
					case SokoMaze.BOX:
						stones.add(child);
						break;
					case SokoMaze.GOAL_SQUARE:
						goals.add(child);
						break;
					case ' ':
						mazeChars[r][c] = SokoMaze.SPACE_INSIDE_MAZE;
						break;
					default: 
						;
					}
				}
			}
		}
		numStones = stones.size();
		stoneLocs = stones.toArray(new int[numStones][]);
		goalLocs = goals.toArray(new int[goals.size()][]);
		
		return new MazeState(player, stoneLocs, numStones, numRows, numCols, goalLocs, mazeChars, nCr(numSpaces,numStones), numSpaces);
	}
	private static int[] addChild(int row, int col, Deque<int[]> queue, boolean[][] visited) {
		visited[row][col] = true;
		int[] child = new int[2];
		child[0] = row;
		child[1] = col;
		queue.add(child);
		return child;
	}
	private static int indexOf(char[] array, char ch) {
		int index = -1;
		for (int i=0; i<array.length && (index<0); i++) {
			index = (array[i] == ch) ? i : -1;
		}  
		return index;
	}
	private static long nCr(int n, int r) {
		long prod = 1;
	    for (int i=1; i<=r; i++) {
	    	prod *= n+1-i;
	    	prod /= i;
	    }
	   return prod;
	}
	
}
