package gui;


public enum Direction { UP(-1,0), DOWN(1,0), RIGHT(0,1), LEFT(0,-1);
	static final int ROW = 0;
	static final int COL = 1;
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
   @Override
	public String toString() {
	   String str;
	   switch(this) {
	   case UP:
		   str = "UP";
		   break;
	   case DOWN:
		   str = "DOWN";
		   break;
	   case RIGHT:
		   str = "RIGHT";
		   break;
	   case LEFT:
		   default:
		   str = "LEFT";
		   break;
	   }
	   return str;
   }	
}
