package solver;

import setup.MazePosition;

public class MyMazePosition implements MazePosition {
	
	int r = 0, c = 0;
	
	public MyMazePosition(int row, int col) {
		r = row;
		c = col;
	}
	public MyMazePosition(MazePosition mpBox) {
		r = mpBox.getRow();
		c = mpBox.getCol();
	}
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MyMazePosition) {
			return this.equals((MyMazePosition)obj);
		}
		return super.equals(obj);
	}
	@Override
	public int hashCode() {
		return r * 50 + c;
	}
	@Override
	public int getRow() {
		return r;
	}

	@Override
	public int getCol() {
		return c;
	}

	@Override
	public void setRow(int row) {
		r = row;
	}

	@Override
	public void setCol(int col) {
		c = col;
	}
	public boolean equals(MazePosition mp) {
		boolean retVal = false;
		if(r == mp.getRow()) {
			if (c == mp.getCol()) {
				retVal = true;
			}
		}
		return retVal;
	}
	@Override
	public String toString() {
		return String.format("(%d,%d)", r,c);
	}
	@Override
	public void moveLeft() {
		c -= 1;
	}
	@Override
	public void moveRight() {
		c += 1;
	}
	@Override
	public void moveUp() {
		r -= 1;
	}
	@Override
	public void moveDown() {
		r +=1;
	}
	@Override
	public void moveTo(MazePosition toPos) {
		r = toPos.getRow();
		c = toPos.getCol();
	}
}
