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
					if ( (ch == BOX) || (ch == BOX_ON_GOAL)) {
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
	class MyMaze implements Maze {
		
		int[] playerRowCol = new int[2];
		boolean[] isGoalNode; // true if node id = goal node
		int[] from = new int[2];
		int[] to = new int[2];
		//change getDistances(..) so it doesn't keep allocating a new array each call : done
		@Override
		public void getDistances(int startRow, int startCol, int[][] distances) {
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
		/* Returns row and column location of player
		 * (non-Javadoc)
		 * @see setup.Maze#getPlayerLocation()
		 */
		public void getPlayerLocation(int[] coord) {
			coord[0] = playerRowCol[0];
			coord[1] = playerRowCol[1];
		}

		@Override
		public int[][] getBoxLocations() {
			return boxes;
		}

		@Override
		public boolean isEmpty(int row, int col) {
			return ( !isWall(row, col) && !isBox(row, col) );
		}

		@Override
		public void setPlayerLocation(int[] newPos) {
			playerRowCol[0] = newPos[0];
			playerRowCol[1] = newPos[1];
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

		@Override
		public void moveBox(int row, int col, Direction dirn) {
			setEmptyAt(row,col);
			from[0] = row;
			from[1] = col;
			dirn.getToPosition(from, to);
			setBoxAt(to[0],to[1]);

			boolean updated = false;
			for (int i=0; (i< boxes.length) && !updated; i++) {
				if ( (row == boxes[i][0]) && (col == boxes[i][1]) ) {
					boxes[i][0] = to[0];
					boxes[i][1] = to[1];
					updated = true;
				}
			}
		}

		@Override
		public void movePlayer(Direction dirn) {
			int pr = playerRowCol[0];
			int pc = playerRowCol[1];
			setEmptyAt(pr,pc);
			from[0] = pr; from[1] = pc;
			dirn.getToPosition(from, playerRowCol);
 		}
		// test "Start Again" bug : fixed 
		@Override
		public void setBoxLocations(int[][] coords) {
			for (int[] oldCoord : boxes) {
				setEmptyAt(oldCoord[0], oldCoord[1]);
			}
			for (int i=0; i<boxes.length; i++) {
				for (int j=0; j<2; j++) {
					setBoxAt(coords[i][0], coords[i][1]);
					boxes[i][j] = coords[i][j];
				}
			}
		}

		@Override
		public boolean allStonesOnGoals() {
			boolean all = true;
			for (int i=0; i<boxes.length && all; i++) {
				all = maze.isGoalSquare(boxes[i][0], boxes[i][1]);
			}
			return all;
		}
	}
	@Override
	public Maze getMaze() {
		return maze;
	}
}
