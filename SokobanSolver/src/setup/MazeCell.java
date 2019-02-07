package setup;

public interface MazeCell {
	
	public MazeCell[] getNeighbours();
	public Maze getMaze(); 			// The maze that contains this MazeCell
	public int getRow();
	public int getCol();
}
