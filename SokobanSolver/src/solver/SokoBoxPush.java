
package solver;

import setup.Maze;
import setup.MazePosition;

public class SokoBoxPush implements BoxPush {
	
	static final int ROW = 0;
	static final int COL = 1;
	
	
	private MazePosition mpBox;
	private Direction dirn;
	
	public static enum Direction { UP(-1,0), DOWN(1,0), RIGHT(0,1), LEFT(0,-1);
		private final int deltaRow;
		private final int deltaCol;
		static final Direction directions[][] = { { null, Direction.UP, null},
									       { Direction.LEFT, null, Direction.RIGHT },
											    { null, Direction.DOWN, null } };
		Direction(int dRow, int dCol) {
			deltaRow = dRow;
			deltaCol = dCol;
		}
		public void getToPosition(int fromPos[], int toPos[]) {
			toPos[ROW] = fromPos[ROW] + deltaRow;
			toPos[COL] = fromPos[COL] + deltaCol;
		}
		public static Direction getDirection(int[] fromPos, int[] toPos) {
			int deltaR = Integer.signum(toPos[ROW] - fromPos[ROW]);
			int deltaC = Integer.signum(toPos[COL]	- fromPos[COL]);
			int row = deltaR + 1;
			int col = deltaC + 1;
			
			return directions[row][col]; 
		}
		public Direction opposite() {
			int row = -1*deltaRow + 1;
			int col = -1*deltaCol + 1;
			
			return directions[row][col];
		}
		
	}
	
	public SokoBoxPush(MazePosition mpBox, Direction dirn) {
		this.mpBox = mpBox;
		this.dirn = dirn;
	}

	@Override
	public void doPush(Maze maze) {
		
		// Move box [0]
		MazePosition mpNew = getNewPosition(mpBox, dirn);
		MazePosition[] mpBoxLocs = new MazePosition[1];
		mpBoxLocs[0] = mpNew;
		maze.setBoxLocations(mpBoxLocs);
		
		// Move player. Player will be in the position the box was in before the push
		maze.setPlayerLocation(mpBox);
		
	}
	@Override
	public MazePosition getOldBoxPosition() {
		return mpBox;
	}
	@Override
	public Direction getDirection() {
		return dirn;
	}
	@Override
	public MazePosition getNewBoxPosition() {
		return getNewPosition(mpBox, dirn);
	}
	
	public String toString() {
		String outStr = " Push box at ";
		
		switch (dirn) {
			case UP:
				outStr += mpBox + " UP"; 
				break;
			case DOWN:
				outStr += mpBox + " DOWN";
				break;
			case RIGHT:
				outStr += mpBox + " RIGHT";
				break;
			case LEFT:
				outStr += mpBox + " LEFT";
				break;
			default:
				outStr += mpBox + " UNKNOWN";
		}
		return outStr;
	}
	public static MazePosition getNewPosition(MazePosition mpOld, Direction dirn) {
		
		MazePosition mpNew = new MyMazePosition(mpOld);
		
		switch (dirn) {
			case UP:
				mpNew.moveUp();
				break;
			case DOWN:
				mpNew.moveDown();
				break;
			case RIGHT:
				mpNew.moveRight();
				break;
			case LEFT:
				mpNew.moveLeft();
				break;
			default:
				;
		}
		return mpNew;
	}
	// TODO Method replaced by Direction.getDirection(int[] from, int[] to)
	public static Direction getDirection(MazePosition from, MazePosition to) {
		int rowFrom = from.getRow(); int rowTo = to.getRow();
		int colFrom = from.getCol(); int colTo = to.getRow();
		Direction dirn = null;
		
		// RIGHT
		if ((rowFrom == rowTo) && (colFrom + 1 == colTo)) {
			dirn = Direction.RIGHT;
		}
		// LEFT
		else if ((rowFrom == rowTo) && (colFrom - 1 == colTo)) {
			dirn = Direction.LEFT;
		}
		// DOWN
		else if ((rowFrom + 1 == rowTo) && (colFrom == colTo)) {
			dirn = Direction.DOWN;
		}
		// UP
		else if ((rowFrom - 1 == rowTo) && (colFrom == colTo)) {
			dirn = Direction.UP;
		}
		return dirn;
	}
	public static BoxPush canPerform(MazePosition mpBox, Direction dirn, 
			              Maze maze, int[][] distances) {
		
		BoxPush retVal = null;
		MazePosition mpTo, mpPlayer;
		mpTo = getNewPosition(mpBox, dirn);
		
		// Calc player position
		switch (dirn) {
		case UP:
			mpPlayer = getNewPosition(mpBox, Direction.DOWN);
			break;
		case DOWN:
			mpPlayer = getNewPosition(mpBox, Direction.UP);
			break;
		case LEFT:
			mpPlayer = getNewPosition(mpBox, Direction.RIGHT);
			break;
		case RIGHT:
			mpPlayer = getNewPosition(mpBox, Direction.LEFT);
			break;
			default:
				mpPlayer = null;
				System.err.println("Unrecognised Direction in SokoBoxPush.canPerform");
		}
		
		// Check indexes
		if ( checkIndexes(mpPlayer, maze) && 
				checkIndexes(mpBox, maze) &&
				checkIndexes(mpTo, maze) ) {
			// The 'to' and player square have to be empty
			if ( (maze.isEmpty(mpTo.getRow(), mpTo.getCol())) &&
					maze.isEmpty(mpPlayer.getRow(), mpPlayer.getCol()) ) {
				
				// Also the player square has to be reachable
				if ( distances[mpPlayer.getRow()][mpPlayer.getCol()] >= 0) {
					retVal = new SokoBoxPush(mpBox, dirn);
				}
			}
		}
		return retVal;
	}
	private static boolean checkIndexes(MazePosition mp, Maze maze) {
		boolean retVal = false;
		int row = mp.getRow(); int col = mp.getCol();
		if ( (row >= 0) && (row < maze.numRows()) ) {
			if ( (col >=0) && col < maze.numCols()) {
				if (!maze.isOutsideMaze(row, col)) {
					retVal = true;
				};
			}
		}
		return retVal;
	}

	

	
}
