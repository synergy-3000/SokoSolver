package setup;


public interface Maze {
	public int[][] getDistances(int startRow, int startCol); 
	public boolean isOutsideMaze(int row, int col);
	public boolean isWall(int row, int col);
	public boolean isBox(int row, int col);
	public boolean isGoalSquare( int row, int col);
	public boolean isEmpty(int row, int col);
	public MazePosition getPlayerLocation();
	public void setPlayerLocation(MazePosition mpPlayer);
	public MazePosition[] getBoxLocations();
	public void setBoxLocations(MazePosition[] mcBoxlocs);
	public int numRows();
	public int numCols();
	public void setBoxAt(int row, int col);
	public void setEmptyAt(int row, int col);
	public boolean[] getIsGoalNode();  // true for nodeId = a goal node
	
}
