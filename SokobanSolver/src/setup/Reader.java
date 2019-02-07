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

import solver.MyMazePosition;
import utils.Utils;

public class Reader implements MazeReader {
	
	boolean debug = true;
	
	private static Reader myReader;
	String SOKOBAN_HEADER = "Sokoban";
	final char SPACE_OUTSIDE_MAZE = ' ';
	final char SPACE_INSIDE_MAZE = '0';
	final char WALL = '#';
	final char PLAYER = 'P';
	final char BOX = 'B';
	final char GOAL_SQUARE = 'X';
	final char BOX_ON_GOAL = 'T';    // Box on a goal square
	
	int numRows, numCols;
	String errMsg;
	MazeCell mazeCells[][];
	Maze maze = null;
	ArrayList<MazePosition> goalSquares;
	
	boolean[] isGoalNode; // true if node id = goal node

	
	char[][] mazeChars;
	
	public  static Reader getReader() {
		if (myReader == null) {
			myReader = new Reader();
		}
		return myReader;
	}
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
			for (int r=0; r<numRows; r++) {
				for (int c=0; c<numCols; c++) {
					if ( (mazeChars[r][c] == GOAL_SQUARE) || (mazeChars[r][c] == BOX_ON_GOAL)) {
						goalSquares.add(new MyMazePosition(r,c));
					}
				}
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
	class MyMaze implements Maze {
		
		MazePosition playerPosition = null;
		ArrayList<MazePosition> boxes = null;
		
		@Override
		public int[][] getDistances(int startRow, int startCol) {
			if(!inArrayBounds(startRow, startCol)) {
				errMsg = String.format("Invalid start position (%d,%d)", startRow,startCol);
				throw new ArrayIndexOutOfBoundsException(errMsg);
			}
			assumingThat(debug, new Executable() {
				public void execute() throws Throwable {
					if (isOutsideMaze(startRow, startCol)) {
						errMsg = String.format("(row %d, col %d) is outside the maze", startRow,startCol);
						throw new Throwable(errMsg);
						//System.err.println(errMsg);
					}
					if (isWall(startRow, startCol)) {
						errMsg = String.format("A wall is located at position (row %d, col %d)!!!", startRow,startCol);
						throw new Throwable(errMsg);
						//System.err.println(errMsg);
					}
				};
			});
			// Do a BFS (Breadth First Search) on Maze Array
			LinkedList<MazeCell> queue = new LinkedList<MazeCell>();
			queue.add(getMazeCell(startRow,startCol));
			int[][] distances = new int[numRows][numCols];
			for (int i=0; i<numRows; i++) {
				Arrays.fill(distances[i], -1);
			}
			distances[startRow][startCol] = 0;
			
			while (!queue.isEmpty()) {
				MazeCell mc = queue.poll();
				MazeCell[] neighbours = mc.getNeighbours();
				
				for (MazeCell neighbour : neighbours) {
					if ( distances[neighbour.getRow()][neighbour.getCol()] == -1) {
						distances[neighbour.getRow()][neighbour.getCol()] = distances[mc.getRow()][mc.getCol()] + 1;
						queue.add(neighbour);
					}
				}
			}
			// Print distances array
			
			assumingThat(debug, new Executable() {
				public void execute() throws Throwable {
					String outStr = String.format("Distances from (%d,%d)",startRow,startCol);
					System.out.println(outStr);
					
					for (int i=0; i<numRows; i++) {
						String strRow = "";
						for (int j=0; j<numCols; j++) {
							String toAdd = "";
							if(isWall(i,j) || isOutsideMaze(i,j) || isBox(i,j)) {
								toAdd = String.valueOf(mazeChars[i][j]);
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
			});
 			return distances;
		}

		@Override
		public int numRows() {
			return numRows;
		}

		@Override
		public int numCols() {
			return numCols;
		}
		
		public boolean isOutsideMaze(int row, int col) {
			return (mazeChars[row][col] == SPACE_OUTSIDE_MAZE);
		}
		
		@Override
		public boolean isWall(int row, int col) {
			return (mazeChars[row][col] == WALL);
		}

		@Override
		public boolean isBox(int row, int col) {
			
			return ((mazeChars[row][col] == BOX) || (mazeChars[row][col] == BOX_ON_GOAL));
		}

		@Override
		public boolean isGoalSquare(int row, int col) {
			// We do this differently because a Box and goal can be on the same square
			boolean retVal = false;
			for (MazePosition mp : goalSquares) {
				if (mp.getRow() == row && mp.getCol() == col) {
					retVal = true;
					break;
				}
			}
			return retVal;
		}

		@Override
		public MazePosition getPlayerLocation() {
			if (playerPosition == null) {
				for (int i=0; i<numRows; i++) {
					for (int j=0; j<numCols; j++) {
						if (mazeChars[i][j] == PLAYER) {
							playerPosition = new MyMazePosition(i,j);
							break;
						}
					}
				}
			}
			return playerPosition;
		}

		@Override
		public MazePosition[] getBoxLocations() {
			if (boxes == null) {
				boxes = new ArrayList<MazePosition>();
				for (int r=0; r<numRows; r++) {
					for (int c=0; c<numCols; c++) {
						if (isBox(r,c)) {
							boxes.add( new MyMazePosition(r,c));
						}
					}
				}
			}
			
			// Debug
			System.out.println("getBoxLocations()");
			for (MazePosition mp : boxes) {
				System.out.println("Box locations: " + mp);
			}
			return boxes.toArray(new MazePosition[0]);
		}

		@Override
		public void setBoxLocations(MazePosition[] mpBoxlocs) {
			
			int row,col;
			MazePosition[] oldLocs = getBoxLocations();
			
			for (MazePosition mp : oldLocs) {
				row = mp.getRow();
				col = mp.getCol();
				if (isGoalSquare(row, col)) {
					mazeChars[row][col] = GOAL_SQUARE;  // Don't really need to do this but helpful when printing out maze
				}
				else {
					mazeChars[mp.getRow()][mp.getCol()]  = SPACE_INSIDE_MAZE;
				}
			}
			boxes.clear();
			for (MazePosition mc : mpBoxlocs) {
				boxes.add(mc);
				mazeChars[mc.getRow()][mc.getCol()] = BOX;
			}
		}

		@Override
		public boolean isEmpty(int row, int col) {
			return ( !isWall(row, col) && !isBox(row, col) );
		}

		@Override
		public void setPlayerLocation(MazePosition mpPlayer) {
			playerPosition.setRow(mpPlayer.getRow());
			playerPosition.setCol(mpPlayer.getCol());
		}

		@Override
		public void setBoxAt(int row, int col) {
			mazeChars[row][col] = BOX;
		}

		@Override
		public void setEmptyAt(int row, int col) {
			if (isGoalSquare(row, col)) {
				mazeChars[row][col] = GOAL_SQUARE;
			}
			else {
				mazeChars[row][col] = SPACE_INSIDE_MAZE;
			}
		}

		@Override
		/* Return array true if node id = goal node
		 * */
		public boolean[] getIsGoalNode() {
			if (isGoalNode == null) {
				ArrayList<Integer> goals = new ArrayList<Integer>();
				
				int nEmpty = 0; // No. of empty squares in Maze
				int nRows=numRows(),nCols=numCols();
				for (int r=0; r<nRows; r++) {
					for (int c=0; c<nCols; c++) {
						if (isGoalSquare(r, c)) {
							goals.add(nEmpty);
							nEmpty += 1;
						}
						else if(!isWall(r, c) && !isOutsideMaze(r, c)) {
							nEmpty += 1;
						}
					}
				}
				isGoalNode = new boolean[nEmpty];
				Arrays.fill(isGoalNode, false);
				for (int id : goals) {
					isGoalNode[id] = true;
				}
			}
			// Debug print isGoalNode
			Utils.printArray(isGoalNode, "isGoalNode", isGoalNode.length);
			
			return isGoalNode;
		}
	}
	@Override
	public Maze getMaze() {
		return maze;
	}
}
