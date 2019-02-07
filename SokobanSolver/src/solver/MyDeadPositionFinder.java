package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import setup.Graph;
import setup.Maze;
import setup.MazeCell;
import setup.MazePosition;
import solver.SokoBoxPush.Direction;
import utils.Utils;

// TODO This class has been replaced by SokoDeadPositionFinder
public class MyDeadPositionFinder implements DeadPositionFinder {
	
	MazePosition[] savedLocs;
	MazePosition[] newLocs; // Array Always length 1
	
	// There are better ways to store the normalised player positions for each visited square in maze
	HashSet<MazePosition> normPlayerPosns[][];
	
	Maze maze;
	
	boolean debug = true;
	
	public MyDeadPositionFinder() {
		newLocs = new MazePosition[1];
	}
	
	@Override
	public MazePosition[] getDeadPositions(Graph graph) {
		
		normPlayerPosns = extracted(maze);
		this.maze = maze;

		ArrayList<MazePosition> arrDeadPosns = new ArrayList<MazePosition>();
		
		// Save Box Locations. We will be working with only one box which is at a possible Dead Position
		savedLocs = maze.getBoxLocations();
		
		// For each space in maze check if a dead position i.e a box at that location cannot be pushed to
		// a goal square
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		
		for (int r=0; r<numRows; r++) {
			for (int c=0; c<numCols; c++) {
				if (!maze.isWall(r, c) && !maze.isOutsideMaze(r, c)) {
					newLocs[0] = new MyMazePosition(r,c);
					maze.setBoxLocations(newLocs);
					System.out.println("Checking pushability of " + newLocs[0]);
					if (!isPushableToGoal(r,c)) {
						arrDeadPosns.add(new MyMazePosition(r,c));
					}
				}
			}
		}
		// Restore old box locations
		maze.setBoxLocations(savedLocs);
		
		return null;
	}

	private HashSet[][] extracted(Maze maze) {
		return new HashSet[maze.numRows()][maze.numCols()];
	}
	// Is pushable to at least one goal square?
	private boolean isPushableToGoal(int r, int c) {	
		boolean retVal = false;
		int boxRow, boxCol;
		int neighbourRow = -1; int neighbourCol = -1;
		int startRow = r; int startCol = c;
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		int[][] pushDistances = new int[numRows][numCols];

		for (int i=0; i<numRows; i++) {
			Arrays.fill(pushDistances[i], -1);
		}
		pushDistances[r][c] = 0;
		
		// Save normalized player position
		MazePosition player = maze.getPlayerLocation();
		int[][] distances = maze.getDistances(player.getRow(), player.getCol());
		MazePosition normPlayer = getNormPlayerPos(distances);
		
		normPlayerPosns[r][c] = new HashSet<MazePosition>();
		normPlayerPosns[r][c].add(normPlayer);
		
		if ( debug) {
			String outstr = String.format("Normalized player at " + normPlayer);
			System.out.println(outstr);
			Utils.printDistances(maze, distances);
		}
		
		BoxPush emptyPush = new EmptyBoxPush(startRow, startCol);
		
		// Do a BFS (Breadth First Search) on Box pushes
		LinkedList<BoxPush> queue = new LinkedList<BoxPush>();
		queue.add(emptyPush);
		
		while (!queue.isEmpty() && !maze.isGoalSquare(neighbourRow, neighbourCol)) {

			BoxPush push = queue.poll();
			push.doPush(maze);
			MazePosition mpBox = push.getNewBoxPosition();
			boxRow = mpBox.getRow(); boxCol = mpBox.getCol();
			
			// Update the player reachable areas for this new box position
			MazePosition mpPlayer = maze.getPlayerLocation();
			distances = maze.getDistances(mpPlayer.getRow(), mpPlayer.getCol());
			
			// Possible legal pushes
			BoxPush[] neighbours = getPossibleMoves(mpBox.getRow(), mpBox.getCol(), distances, maze);

			for (BoxPush neighbour : neighbours) {
				neighbour.doPush(maze);
				MazePosition mpNeighbour = neighbour.getNewBoxPosition();
				neighbourRow = mpNeighbour.getRow(); neighbourCol = mpNeighbour.getCol();
				
				// Update the player reachable areas for this new box position
				mpPlayer = maze.getPlayerLocation();
				distances = maze.getDistances(mpPlayer.getRow(), mpPlayer.getCol());
				
				normPlayer = getNormPlayerPos(distances);
				//System.out.println("normPlayer");
				if (pushDistances[neighbourRow][neighbourCol] == -1) {
					pushDistances[neighbourRow][neighbourCol] = pushDistances[boxRow][boxCol] + 1;
					queue.add(neighbour);
					normPlayerPosns[neighbourRow][neighbourCol] = new HashSet<MazePosition>();
					normPlayerPosns[neighbourRow][neighbourCol].add(normPlayer);
				}
				if (maze.isGoalSquare(neighbourRow, neighbourCol)) {
					break;
				}
				// Our box might have visited this square before but the player was in a different 
				// position (actually region). We use the normPlayerPosns to check this.
				/*else if (!normPlayerPosns[neighbourRow][neighbourCol].contains(normPlayer)) {
					pushDistances[neighbourRow][neighbourCol] = pushDistances[boxRow][boxCol] + 1;
					queue.add(neighbour);
					normPlayerPosns[neighbourRow][neighbourCol].add(normPlayer);
				}
				*/
				
			}
		}
		
		if (maze.isGoalSquare(neighbourRow, neighbourCol)) {
			retVal = true;
			String outStr = String.format("Found a push path from (%d,%d) to (%d,%d) ", r, c, neighbourRow, neighbourCol);
			System.out.println( outStr );
			
			// Debug: Print out path
			ArrayList<Direction> path = Utils.getPath(new MyMazePosition(r, c), 
					new MyMazePosition(neighbourRow, neighbourCol), pushDistances);
			System.out.println(path);
		}
		return retVal;
	}
	// Get topmost left position that the player can reach
	private MazePosition getNormPlayerPos(int[][] distances) {
		
		MazePosition retVal = null;
		
		for (int row=0; row<distances.length && retVal==null; row++) {
			for (int col=0; col<distances[row].length && retVal==null; col++) {
				if (distances[row][col] >= 0) {
					retVal = new MyMazePosition(row, col);
				}
			}
		}
		return retVal;
	}
	
	// A box that doesn't move. Used to start off the Breadth First Search algorithm 
	class EmptyBoxPush implements BoxPush {
		MazePosition mpBox;

		public EmptyBoxPush(int row, int col) {
			mpBox = new MyMazePosition(row, col);
		}
		@Override
		public MazePosition getOldBoxPosition() {
			return mpBox;
		}

		@Override
		public Direction getDirection() {
			return Direction.UP;   // Should never be called. A random choice.
		}

		@Override
		public void doPush(Maze maze) {
			; // Do nothing
		}

		@Override
		public MazePosition getNewBoxPosition() {
			return mpBox;
		}
		
	}
	private static BoxPush[] getPossibleMoves(int row, int col, int[][]distances, Maze maze) {
		
		ArrayList<BoxPush> pushes = new ArrayList<BoxPush>();
		
		BoxPush boxPushUp = SokoBoxPush.canPerform(new MyMazePosition(row, col), Direction.UP, maze, distances);
		if (boxPushUp != null) pushes.add(boxPushUp);
		
		BoxPush boxPushDown = SokoBoxPush.canPerform(new MyMazePosition(row, col), Direction.DOWN, maze, distances);
		if (boxPushDown != null) pushes.add(boxPushDown);
		
		BoxPush boxPushRight = SokoBoxPush.canPerform(new MyMazePosition(row, col), Direction.RIGHT, maze, distances);
		if (boxPushRight != null) pushes.add(boxPushRight);
		
		BoxPush boxPushLeft = SokoBoxPush.canPerform(new MyMazePosition(row, col), Direction.LEFT, maze, distances);
		if (boxPushLeft != null) pushes.add(boxPushLeft);
		
		return pushes.toArray(new BoxPush[0]);
		
	}

	@Override
	public int[] getPathToGoal(Graph graph, BoxPusher boxPusher, int fromNode, boolean[] isGoalNode, int playerNode) {
		// TODO Auto-generated method stub
		return null;
	}
}













