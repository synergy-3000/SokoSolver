package utils;

import java.util.Arrays;

import gui.Controller;
import setup.GraphCreator;
import setup.Maze;

public class Utils {
	
	public static int[][] copyArray(int[][] array) {
		int[][] copy = new int[array.length][];
		for (int i=0; i<array.length; i++) {
			copy[i] = Arrays.copyOf(array[i], array[i].length);
		}
		return copy;
	}
	public static void copyArray(char[][] toArray, char[][] frArray) {
		for(int i=0; i<frArray.length; i++) {
			System.arraycopy(frArray[i], 0, toArray[i], 0, frArray[i].length);
		}
	}
	public static void fill(char[][] array, char value) {
		for (int i=0; i<array.length; i++) {
			Arrays.fill(array[i], value);
		}
	}
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
		
		Maze maze = Controller.getInstance().getMaze();
		
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		int idx = 0;
		
		for (int i=0; i<numRows; i++) {
			String strRow = "";
			for (int j=0; j<numCols; j++) {
				String toAdd = "";
				if(maze.isWall(i,j)) {
					toAdd = "##";
				}
				else if(maze.isOutsideMaze(i,j)) {
					toAdd = "  ";
				}
				else {
					toAdd = String.valueOf(idx);
					idx += 1;
				}
				if (toAdd.length() == 1) toAdd = " " + toAdd;
				strRow = strRow + toAdd;
			}
			System.out.println(strRow);
		}
	}
}
