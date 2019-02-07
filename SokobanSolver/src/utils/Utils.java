package utils;

import java.util.ArrayList;
import java.util.Arrays;

import setup.GraphCreator;
import setup.Maze;
import setup.MazePosition;
import setup.Reader;
import solver.BoxPush;
import solver.MyMazePosition;
import solver.SokoBoxPush;
import solver.SokoBoxPush.Direction;

public class Utils {
	//static 
	// Debug
	public static void printDistances(Maze maze, int distances[][]) {
		String outStr = "Distances array";
		System.out.println(outStr);
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		
		for (int i=0; i<numRows; i++) {
			String strRow = "";
			for (int j=0; j<numCols; j++) {
				
				String toAdd = "";
				if (maze.isBox(i,j)) {
					toAdd = "B";
				}
				else if(maze.isOutsideMaze(i,j)) {
					toAdd = " ";
				}
				else if(maze.isWall(i, j)) {
					toAdd = "#";
				}
				else {
					toAdd = String.valueOf(distances[i][j]);
				}
				if (toAdd.length() == 1) toAdd = "+" + toAdd;
				strRow = strRow + toAdd;
			}
			System.out.println(strRow);
		}
	}
	public static void printNodes(int[] nodeIds) {
		GraphCreator gc = GraphCreator.getGraphCreator();
		for(int i=0; i<nodeIds.length; i++) {
			System.out.println(gc.nodeIdToString(nodeIds[i]));
		}
	}
	public static void printArray(int[] array, String arrName, int count) {
		String spaces = "                              ";
		String outStr = arrName + "[]" + spaces;
		outStr = outStr.substring(0, 15);
		outStr += " = [";
		int i;
		for (i=0; i<count-1; i++) {
			if ((array[i] < 10) && (array[i] >= 0)) outStr += "0"; 
			outStr +=  array[i] + ", ";
		}
		if ((array[i] < 10) && (array[i] >= 0)) outStr += "0";
		outStr += array[i] + "]";
		System.out.println(outStr);
	}
	public static void printArray(boolean[] array, String arrName, int count) {
		System.out.println(arrName + "[]: ");
		String sIdxs = "Indexes: [ ";
		String sVals = "Values:  [ ";
		
		for (int i=0; i<count; i++) {
			
			sVals += (array[i] == true) ? (" 1") : (" 0");
			
			if (i<10) {
				sIdxs += " " + i;
			}
			else sIdxs += i;
			if (i == (count-1)) {
				sIdxs += "]";
				sVals += "]";
			}
			else {
				sIdxs += ",";
				sVals += ",";
			}
		}
		System.out.println(sIdxs);
		System.out.println(sVals);
	}
	public static void printArray(int[][] array, String arrName) {
		StringBuffer aLine = new StringBuffer();
		
		System.out.println(arrName);
		for(int r=0; r< array.length; r++) {
			aLine.setLength(0);
			for(int c=0; c<array[0].length; c++) {
				if (array[r][c] >=0 && array[r][c] < 10) aLine.append("0");
				aLine.append(array[r][c]);
			}
			System.out.println(aLine);
		}
	}
	public static void printMaze() {
		
		Reader reader = Reader.getReader();
		Maze maze = reader.getMaze();
		int nEmpty = GraphCreator.getGraphCreator().getNodes().length;
		
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		int idx = 0;
		
		for (int i=0; i<numRows; i++) {
			String strRow = "";
			for (int j=0; j<numCols; j++) {
				String toAdd = "";
				if(maze.isWall(i,j)) {
					toAdd = "#";
				}
				else if(maze.isOutsideMaze(i,j)) {
					toAdd = " ";
				}
				else {
					toAdd = String.valueOf(idx);
					idx += 1;
				}
				if (toAdd.length() == 1) toAdd = "+" + toAdd;
				strRow = strRow + toAdd;
			}
			System.out.println(strRow);
		}
	}
	// TODO This method has been replaced by SokoDeadPositionFinder.getPathToGoal()
	//
	public static ArrayList<Direction> getPath(MazePosition from, MazePosition to, int[][] distances) {
		
		ArrayList<Direction> path = new ArrayList<Direction>();
		MazePosition curPos = new MyMazePosition(to);
		Direction pushDirn;
		
		printArray(distances, "Utils.getPath() distances[][]: ");
		
		// Start from 'to' and work backwards to 'from'
		int curDist = distances[to.getRow()][to.getCol()];
		
		System.out.println("curDist: " + curDist);
		String outStr = String.format("Distance value at %s is %d ", from, distances[from.getRow()][from.getCol()]);
		System.out.println(outStr);
		
		while (!curPos.equals(from)) {
			pushDirn = null;
			
			// UP but DOWN when going in opposite direction
			if (isNext(curDist, Direction.UP , SokoBoxPush.getNewPosition(curPos, Direction.DOWN), distances)) {
				pushDirn = Direction.UP;
				curPos.moveDown();
			}
			// DOWN
			else if(isNext(curDist, Direction.DOWN , SokoBoxPush.getNewPosition(curPos, Direction.UP), distances)) {
				pushDirn = Direction.DOWN;
				curPos.moveUp();
			}
			// LEFT
			else if(isNext(curDist, Direction.LEFT , SokoBoxPush.getNewPosition(curPos, Direction.RIGHT), distances)) {
				pushDirn = Direction.LEFT;
				curPos.moveRight();
			}
			// RIGHT
			else if(isNext(curDist, Direction.RIGHT , SokoBoxPush.getNewPosition(curPos, Direction.LEFT), distances)) {
				pushDirn = Direction.RIGHT;
				curPos.moveLeft();
			}
			else {
				System.err.println("Can't find next position from " + curPos);
				printArray(distances, "distances[][] in Utils.getPath()");;
			}
			if (pushDirn != null) {
				path.add(0, pushDirn);
				curDist--;
			}
		}
		return path;
	}
	private static boolean isNext(int curDist, Direction pushDirn, MazePosition mpBox, int[][]distances) {
		boolean retVal = false;
		int row = mpBox.getRow();
		int col = mpBox.getCol();
		if (row>=0 && row<distances.length) {
			if (col>=0 && col<distances[0].length) {
				if (distances[row][col] == (curDist-1)) {
					retVal = true;
				}
			}
		}
		// Also check if we can perform a push in the given direction
		if (retVal) {
			Maze maze = Reader.getReader().getMaze();
			MazePosition[] mcOldBoxLocs = maze.getBoxLocations();
			MazePosition[] newBoxLocs = new MazePosition[1];
			newBoxLocs[0] = mpBox;
			maze.setBoxLocations(newBoxLocs);
			if (SokoBoxPush.canPerform(mpBox, pushDirn, maze, distances) == null) {
				retVal = false;
			}
			maze.setBoxLocations(mcOldBoxLocs);
		}
		return retVal;
	}
}
