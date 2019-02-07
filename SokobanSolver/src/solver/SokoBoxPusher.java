package solver;

import setup.Maze;

public class SokoBoxPusher implements BoxPusher {

	private Maze maze;
	private int currentRow, currentCol;
	
	public SokoBoxPusher(Maze maze, int startRow, int startCol) {
		this.maze = maze;
		currentRow = startRow;
		currentCol = startCol;
	}

	@Override
	public void pushBox(int toRow, int toCol) throws IllegalArgumentException {
		
		// Error checking
		int deltaRow = Math.abs(toRow - currentRow);
		int deltaCol = Math.abs(toCol - currentCol);
		if ( (deltaRow > 1) || (deltaCol > 1) ) {
			throw new IllegalArgumentException(
					String.format("Distance from current position (%d,%d) to destination (%d,%d) is greater than one",
					currentRow, currentCol, toRow, toCol ));
		}
		
		maze.setEmptyAt(currentRow, currentCol);
		maze.setBoxAt(toRow, toCol);
		currentRow = toRow;
		currentCol = toCol;
	}

	@Override
	public void setBoxPosition(int row, int col) {
		maze.setEmptyAt(currentRow, currentCol);
		currentRow = row;
		currentCol = col;
		maze.setBoxAt(row, col);
	}

}
