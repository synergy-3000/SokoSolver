package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import setup.Graph;
import setup.Maze;
import setup.SokoMaze;

//Make a DeadPositionFinder3 that uses a HashSet and a normalized player position : done
/**
 * A second try at writing a DeadPositionFinder. This implementation doesn't need a 
 * Graph object. It creates its own graph.
 * 
 * @author zhipinghe
 *
 */
public class DeadPositionFinder2 implements DeadPositionFinder {
	
	boolean debug = false;
	
	final int LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3;
	int[] opp = { RIGHT, DOWN, LEFT, UP };   // (dir+2) % 4;
	
	int[] dx = {-1, 0,1,0}; 
	int[] dy = { 0,-1,0,1}; 
	int[][] hashDir = { {   0,   UP,    0},
			            {LEFT,   0,   RIGHT},
			            {   0,  DOWN,   0} };
	
	int[][] children = new int[SokoMaze.MAX_SPACES*4][];
	int[][] equal = new int[SokoMaze.MAX_SPACES*4][];
	int[] parent = new int[SokoMaze.MAX_SPACES*4];
	boolean[] visited = new boolean[SokoMaze.MAX_SPACES*4];
	
	int[][] index;
	int[] row;   // row for the index[r][c]
	int[] col;
	int oppTo[] = new int[2];
	int to[] = new int[2];
	ArrayList<Integer> childHash = new ArrayList<Integer>();
	ArrayList<Integer> equivHash = new ArrayList<Integer>();
	int[][] distances;
	boolean graphInit = false;

	private int[] topLeft = new int[2];
	
	
	private void initGraph(Maze maze) {
		Arrays.fill(visited, false);
		Arrays.fill(parent, -1);
		// Remove boxes. We will place a box at every empty square and compute the player reachable area
		int[][] boxLocs = maze.getBoxLocations();
		for (int[] coord : boxLocs) {
			maze.setEmptyAt(coord[0], coord[1]);
		}
		// Set up the index of each empty square inside the maze
		int nRows = maze.numRows(), nCols = maze.numCols();
		int idx = 0;
		index = new int[nRows][nCols];
		distances = new int[nRows][nCols];
		row = new int[nRows*nCols];
		col = new int[nRows*nCols];
		int hash;
		int pRow, pCol;
		
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (!maze.isWall(r, c) && !maze.isOutsideMaze(r, c)) {
					index[r][c] = idx;
					row[idx] = r;
					col[idx] = c;
					idx += 1;
				}
				else {
					index[r][c] = -1;
				}
			}
		}
		// Set up the children and hash codes
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (index[r][c] > -1) {
					maze.setBoxAt(r, c);
					// Set the player at possibly 4 empty spaces around the box
					for (int i=0; i<4; i++) {
						pRow = r+dy[i];
						pCol = c+dx[i];
						if (index[pRow][pCol] > -1) {
							maze.getDistances(pRow, pCol, distances, topLeft );
							hash = getHashCode(index[r][c], r, c, r+dy[i], c+dx[i]);
							equal[hash] = getEquivalent(pRow, pCol, r, c, distances);
							children[hash] = getPushes(maze, r, c, distances);
							/*for (int child : children[hash]) {
								parent[child] = hash;
							}*/
						}
					}
					maze.setEmptyAt(r, c);
				}
			}
		}
		// Debug
		if (debug) {
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (index[r][c] > -1) {
					hash = index[r][c]*4;
					
					for(int j=0; j<4; j++) {
						if (index[r+dy[j]][c+dx[j]] > -1) {
							String outStr = String.format("[%d,%d] #%d",r,c,hash+j);
							System.out.println(outStr);
							System.out.print("children: ");
							for (int child : children[hash+j]) {
								System.out.printf(child + " ");
							}
							System.out.printf("\nequivalent: ");
							for (int equiv : equal[hash+j]) {
								System.out.printf(equiv + " ");
							}
							System.out.printf("\n");
							System.out.printf("Parent: #" + parent[hash+j]);
							System.out.printf("\n");
						}
					}
				}
			}
		}
		}
		// End Debug
		// Put boxes back
		for (int[] coord : boxLocs) {
			maze.setBoxAt(coord[0], coord[1]);
		}
		graphInit = true;		
	}
	/**
	 * Returns the children as their hash codes.
	 * 
	 * @param maze
	 * @param boxR
	 * @param boxC
	 * @return
	 */
	private int[] getPushes(Maze maze, int boxR, int boxC, int[][] distances) {
		
		childHash.clear();
		for (int i=0; i<2; i++) {
			
			to[0] = boxR+dy[i];
			to[1] = boxC+dx[i];
			oppTo[0] = boxR-dy[i];
			oppTo[1] = boxC-dx[i];
			
			if (maze.isEmpty(to[0], to[1]) && maze.isEmpty(oppTo[0], oppTo[1])) {
				if (distances[oppTo[0]][oppTo[1]] >= 0) {
					childHash.add(getHashCode(index[to[0]][to[1]], to[0], to[1], boxR, boxC));
				}
				if (distances[to[0]][to[1]] >= 0) {
					childHash.add(getHashCode(index[oppTo[0]][oppTo[1]], oppTo[0], oppTo[1], boxR, boxC));
				}
			}
		}
		int[] ret = new int[childHash.size()];
		int j=0;
		for (int child : childHash) {
			ret[j] = child;
			j += 1;
		}
		return ret;
	}
	/**
	 * Get equivalent nodes as hash codes
	 */
	private int[] getEquivalent(int pRow, int pCol, int boxR, int boxC, int[][] distances) {
		
		equivHash.clear();
		int dR = pRow - boxR;
		int dC = pCol - boxC;
		int dir = hashDir[1+dR][1+dC];
		int pR, pC;
		//int boxIdx = index[boxR][boxC];
		int hash;
		/*if (boxR == 8 && boxC == 7) {
			System.out.println("distances[][] for [8,7]: ");
			for (int i=0; i<distances.length; i++) {
				for (int j=0; j<distances[i].length; j++) {
					int d = distances[i][j];
					if (d >= 0 && d < 10) System.out.print("0");
					System.out.print(d);
				}
				System.out.print("\n");
			}
		}*/
		for (int i=0; i<4; i++) {
			pR = boxR + dy[i];
			pC = boxC + dx[i];
			if ((i != dir) && (distances[pR][pC] > -1)) {
				hash = index[boxR][boxC] * 4 + i;
				equivHash.add(hash);
				//equivHash.add(getHashCode(boxIdx, boxR, boxC, pR, pC));
			}
		}
		int[] ret = new int[equivHash.size()];
		int j=0;
		for (int equiv : equivHash) {
			ret[j] = equiv;
			j += 1;
		}
		return ret;
	}
	@Override
	public int[] getDeadPositions(Graph graph, Maze maze) {
		
		initGraph(maze);
		
		// Clear boxes
		int[][] boxLocs = maze.getBoxLocations();
		for (int[] coord : boxLocs) {
			maze.setEmptyAt(coord[0], coord[1]);
		}
		
		// For each empty square inside maze check that if a box placed in it could be pushed to a goal square
		ArrayList<Integer> dead = new ArrayList<Integer>();
		boolean found = false;
		int nRows = maze.numRows();
		int nCols = maze.numCols();
		int pRow = 0, pCol = 0;
		boolean[] goals = maze.getIsGoalNode();
		int[] pRC = new int[2];
		maze.getPlayerLocation(pRC);
		
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (index[r][c] > -1) {
					found = false;
					// Set a box at the square
					maze.setBoxAt(r, c);
					maze.getDistances(pRC[0], pRC[1], distances, topLeft);
					maze.setEmptyAt(r, c);
					
					// Find a square around the box that the player can reach.
					for (int j=0; j<4 && !found; j++) {
						pRow = r + dy[j];
						pCol = c + dx[j];
						if (distances[pRow][pCol] > -1) {
							found = true;
						}
					}
					if (!found || (getPathToGoal(maze, r, c, goals, pRow, pCol) == null)) dead.add(index[r][c]);
				}
			}
		}
		int[] deadIdxs = new int[dead.size()];
		int i=0;
		for (int idx : dead) {
			deadIdxs[i] = idx;
			i++;
		}
		// Put the boxes back
		for (int[] coord : boxLocs) {
			maze.setBoxAt(coord[0], coord[1]);
		}
		return deadIdxs;
	}
	/*private boolean debugTest(boolean found) {
		System.out.println("found = " + found);
		return false;
	}*/
	private int idFromHash(int hash) {
		return hash/4;
	}
	public int[] getPathToGoal(Maze maze, int boxR, int boxC, boolean[] isGoalNode, int pRow, int pCol) {
		
		if (!graphInit) initGraph(maze);
		
		Arrays.fill(visited, false);
		int starthash = getHashCode(index[boxR][boxC], boxR, boxC, pRow, pCol);
		int hash = starthash;
		int[] path = null;
		LinkedList<Integer> queue = new LinkedList<Integer>();
		parent[starthash] = -1;
		queue.add(starthash);
		boolean found = false;
		while (!queue.isEmpty() && !found) {
			hash = queue.poll();
			if (isGoalNode[hash/4]) found = true;
			visit(hash);
			int[] neighbours = children[hash];
			for (int neighbour : neighbours) {
				if (!visited[neighbour]) {
					parent[neighbour] = hash;
					queue.add(neighbour);
				}
			}
		}
		if(found) {
			LinkedList<Integer> stack = new LinkedList<Integer>();
			while (hash != starthash) {
				//System.out.printf("[%d,%d] ", row[hash/4], col[hash/4]);
				stack.push(hash);
				hash = parent[hash];
			}
			path = new int[stack.size()];
			int i=0;
			while (!stack.isEmpty()) {
				path[i] = stack.pop();
				i++;
			}
			if (debug) {
			System.out.printf("[%d,%d] ", row[hash/4], col[hash/4]);
			System.out.print("\n");
			}
		}
		return path;
	}
	private void visit(int hash) {
		visited[hash] = true;
		for (int node : equal[hash]) {
			visited[node] = true;
		}
	}
	private int getHashCode(int stoneIdx, int stoneRow, int stoneCol, int playerRow, int playerCol) {
		int dRow, dCol;
		dRow = playerRow - stoneRow;
		dCol = playerCol - stoneCol;
		return (stoneIdx * 4 + hashDir[1+dRow][1+dCol]);
	}
}
