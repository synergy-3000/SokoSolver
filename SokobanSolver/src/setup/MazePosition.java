package setup;

public interface MazePosition {
	public int getRow();
	public int getCol();
	public void setRow(int row);
	public void setCol(int col);
	public void moveLeft();
	public void moveRight();
	public void moveUp();
	public void moveDown();
	public void moveTo(MazePosition toPos);
	public boolean equals(MazePosition mp);
}
