package setup;

import gui.Direction;

public interface Maze {
	public int[][] getDistances(int startRow, int startCol); 
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
	public boolean[] getIsGoalNode();  // true for nodeId = a goal node
	
}
