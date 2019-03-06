package setup;

import static org.junit.jupiter.api.Assumptions.assumingThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.junit.jupiter.api.function.Executable;

import gui.Direction;
import solver.MyMazePosition;
import utils.Utils;

//TODO Remove this file. 
/*
 * This class was a first attempt to read a single maze. Now the class <>CollectionsReader reads
 * files containing multiple mazes.
 */
//Extract MyMaze into a separate file and rename to SokoMaze : done
/*public class Reader implements MazeReader {
	
	boolean debug = true;
	
	private static Reader myReader;
	String SOKOBAN_HEADER = "Sokoban";
	
	
	
	int numRows, numCols;
	String errMsg;
	MazeCell mazeCells[][];
	Maze maze = null;
	ArrayList<MazePosition> goalSquares;
	int[] player;    // Player's row, column position
	int[][] boxes;   // Box row and column positions

	
	char[][] mazeChars;
	
	public  static Reader getReader() {
		if (myReader == null) {
			myReader = new Reader();
		}
		return myReader;
	}
	private Reader() {
		player = new int[2];
	}
	//     This method has been replaced by Maze.parseChars(char[][] mazeChars) together 
	//     with CollectionsReader.readCollection(File file) 
	@Override
	public Maze readMaze(File file) {
		
		goalSquares = new ArrayList<MazePosition>();
		ArrayList<String> mazeRows = new ArrayList<String>();
		maze = null;
		numRows = 0; numCols = 0;
		
		boolean isSokobanFile = false;
		BufferedReader d;
		try {
			d = new BufferedReader(new FileReader(file));
			String aLine = d.readLine(); 
			
			// Find Sokoban header
			while (aLine != null && !isSokobanFile) {
				if (aLine.indexOf(SOKOBAN_HEADER) != -1) isSokobanFile = true;
				aLine = d.readLine();
			}
			// It is a Sokoban file. calculate numRows numCols
			while(aLine != null && isSokobanFile) {
				numCols = Math.max(numCols, aLine.length());
				numRows += 1;
				mazeRows.add(aLine);
				// Debug print line
				System.out.println(aLine);
				aLine = d.readLine();
			}
			System.out.printf("numRows: %d", numRows);
			System.out.printf(" numCols: %d\n", numCols);
			d.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		if (isSokobanFile) {
			mazeChars = new char[numRows][numCols];
			mazeCells = new MazeCell[numRows][numCols];
			int iRow = 0;
			
			for (String aLine : mazeRows) {
				Arrays.fill(mazeChars[iRow], SPACE_OUTSIDE_MAZE);
				System.arraycopy(aLine.toCharArray(), 0, mazeChars[iRow], 0, aLine.length());
				iRow++;
			}
			ArrayList<int[]> boxLocs = new ArrayList<int[]>();
			int[] coord;
			char ch;
			for (int r=0; r<numRows; r++) {
				for (int c=0; c<numCols; c++) { 
					ch = mazeChars[r][c];
					if (ch == PLAYER) {
						player[0] = r;
						player[1] = c;
					}
					if ((ch == BOX) || (ch == BOX_ON_GOAL)) {
						coord = new int[2];
						coord[0] = r;
						coord[1] = c;
						boxLocs.add(coord);
					}
					if ( (ch == GOAL_SQUARE) || (ch == BOX_ON_GOAL)) {
						goalSquares.add(new MyMazePosition(r,c));
					}
				}
			}
			boxes = new int[boxLocs.size()][];
			int j=0;
			for (int[] pos : boxLocs) {
				boxes[j] = pos;
				j += 1;
			}
			// Debug: print out Goal squares
			System.out.println("Goal Squares");
			for (MazePosition mp : goalSquares) {
				System.out.println(mp);
			}
			// Debug: check we have created mazeChars[][] correctly
			for(int i = 0; i < numRows; i++) {
				System.out.println(String.valueOf(mazeChars[i]));
			}
			maze = new MyMaze();
			maze.setPlayerLocation(player);
		}
		return maze;
	}
	
	
	boolean inArrayBounds( int row, int col) {
		boolean retVal = false;
		if (row >= 0 && row < numRows) {
			if (col >= 0 && col < numCols) {
				retVal = true;
			}
		}
		return retVal;
	}
	MazeCell getMazeCell(int row, int col) {
		if (mazeCells[row][col] == null) {
			mazeCells[row][col] = new MyMazeCell(row,col);
		}
		return mazeCells[row][col];
	}
	class MyMazeCell implements MazeCell {
		int row, col;
		MazeCell[] neighbours;
		ArrayList<MazeCell> neighboursList;
		
		MyMazeCell (int row, int col) {
			this.row = row;
			this.col = col;
			neighboursList = new ArrayList<MazeCell>();
		}
		public Maze getMaze() {
			return maze;
		}
		// Neighbours can change over time
		public MazeCell[] getNeighbours() {
			
			int tmpR, tmpC;
			
			ArrayList<MazeCell> neighboursList = new ArrayList<MazeCell>();
			// Up 
			tmpR = row - 1; tmpC = col;
			if (checkCell(tmpR, tmpC)) neighboursList.add(getMazeCell(tmpR, tmpC));
			// Right
			tmpR = row; tmpC = col+1;
			if (checkCell(tmpR, tmpC)) neighboursList.add(getMazeCell(tmpR, tmpC));
			// Down
			tmpR = row + 1; tmpC = col;
			if (checkCell(tmpR, tmpC)) neighboursList.add(getMazeCell(tmpR, tmpC));
			// Left
			tmpR = row; tmpC = col -1;
			if (checkCell(tmpR, tmpC)) neighboursList.add(getMazeCell(tmpR, tmpC));
			neighbours = neighboursList.toArray(new MazeCell[0]);

			return neighbours;
		}
		private boolean checkCell(int r, int c) {
			//System.out.printf("checkCell(%d,%d) isOutsideMaze()= %b\n", r,c,maze.isOutsideMaze(r, c));
			boolean retVal = false;
			if (r>=0 && r<numRows && c>=0 && c<numCols) {
				if (!maze.isWall(r,c) && !maze.isOutsideMaze(r,c) && !maze.isBox(r, c)) {
					retVal = true;
				}
			}
			return retVal;
		}
		@Override
		public int getRow() {
			return row;
		}
		@Override
		public int getCol() {
			return col;
		}
	}
	
	@Override
	public Maze getMaze() {
		return maze;
	}
}*/
