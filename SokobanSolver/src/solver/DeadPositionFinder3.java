package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import setup.Graph;
import setup.Maze;
import setup.SokoMaze;
/**
 * Implementation of DeadPositionFinder that uses a HashSet and a normalised player position
 * equal to the topmost left coordinate of the player reachable area. Could spped this up by
 * having an Node[][3], 1st index = boxId , 2nd index = player location around box. It was implemented
 * this way because the future solver might be implemented similarly but with multiple box locations
 * in each Node.
 * 
 * @author zhipinghe
 *
 */
public class DeadPositionFinder3 implements DeadPositionFinder {
	
	HashSet<Node> visited;
	HashMap<Node, Node> parent;
	
	int[][] distances;		// Player reachable area
	int[][] index;			// Empty square index at row,col
	int[] row,col;			// row and col for empty square index
	int[] to, oppTo;        // Coordinate variables used in calculations
	int[] coord;			// working out variables
	boolean[] canReach;		// ditto
	int[] coord2;
	int[] boxRC, pRC;
	
	int[] dx = {-1, 0,1,0}; 
	int[] dy = { 0,-1,0,1}; 
	final int LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3;
	int[] opp = { RIGHT, DOWN, LEFT, UP };  
	boolean[] isGoal;
	int[] dead;
	
	public DeadPositionFinder3() {
		visited = new HashSet<Node>();
		parent = new HashMap<Node, Node>();
		
		distances = new int[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
		index = new int[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
		row = new int[SokoMaze.MAX_SPACES];
		col = new int[SokoMaze.MAX_SPACES];
		to = new int[2];
		oppTo = new int[2];
		coord = new int[2];
		canReach = new boolean[4];
		coord2 = new int[2];
		dead = new int[SokoMaze.MAX_SPACES];
		boxRC = new int[2];
		pRC = new int[2];
	}
	/**
	 * Returns the index of the squares that are dead posiitons. i.e A box placed at that square cannot be
	 * pushed to a goal square.
	 */
	@Override
	public int[] getDeadPositions(Graph graph, Maze maze) {
		
		boolean found;
		int nDead = 0;
		
		// Remove boxes. We will place a box at every empty square and try and find a path to 
		// a goal square.
		int[][] boxLocs = maze.getBoxLocations();
		for (int[] coord : boxLocs) {
			maze.setEmptyAt(coord[0], coord[1]);
		}
		isGoal = maze.getIsGoalNode();
		
		// Set up the index of each empty square inside the maze
		int nRows = maze.numRows(), nCols = maze.numCols();
		int idx = 0;
		int pRow = 0, pCol = 0;
		
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
		// For each empty square check if a box placed on the square can be pushed to a goal square.
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (index[r][c] > -1) {
					found = false;
					// Set a box at the square
					//maze.setBoxAt(r, c);
					//maze.getDistances(pRC[0], pRC[1], distances);
					//maze.setEmptyAt(r, c);
					
					// Find a square around the box.
					//TODO Do we need to call getPathToGoal(...) for every empty square around the box
					for (int j=0; j<4 && !found; j++) {
						pRow = r + dy[j];
						pCol = c + dx[j];
						if (index[pRow][pCol] > -1) {
							found = true;
						}
					}
					if (!found || (getPathToGoal(maze, r, c, isGoal, pRow, pCol) == null)) {
						dead[nDead] = (index[r][c]);
						nDead += 1;
					}
				}
			}
		}
		// Put the boxes back
		for (int[] coord : boxLocs) {
			maze.setBoxAt(coord[0], coord[1]);
		}
		return Arrays.copyOf(dead, nDead); 
	}
	private int[] getPathToGoal(Maze maze, int boxR, int boxC, boolean[] isGoalNode, int pRow, int pCol) {
		
		boxRC[0] = boxR;
		boxRC[1] = boxC;
		pRC[0] = pRow;
		pRC[1] = pCol;
		
		visited.clear();
		parent.clear();
		Node node = null, nodes[];
		int[] path = null;
		
		LinkedList<Node> queue = new LinkedList<Node>();
		Node startNode = makeNode(maze, boxRC, pRC);
		queue.add(startNode);
		boolean found = false;
		while (!queue.isEmpty() && !found) {
			node = queue.poll();
			if (isGoalNode[node.boxId]) found = true;
			visited.add(node);
			nodes = getPushes(maze, node);
			for (Node child : nodes) {
				if (!visited.contains(child)) {
					parent.put(child, node);
					queue.add(child);
				}
			}
		}
		
		if(found) {
			LinkedList<Node> stack = new LinkedList<Node>();
			while (node != startNode) {
				//System.out.printf("[%d,%d] ", row[hash/4], col[hash/4]);
				stack.push(node);
				node = parent.get(node);
			}
			stack.push(node);
			path = new int[stack.size()];
			int i=0;
			while (!stack.isEmpty()) {
				path[i] = stack.pop().boxId;
				//System.out.printf("[%d,%d] ", row[path[i]], col[path[i]]);
				i++;
			}
			
			//System.out.print("\n");
		}
		return path;
	}
	/**
	 * Returns the neighbours of the given node. These are nodes that can be pushed to.
	 * distances is the player reachable area.
	 * 
	 * @param maze
	 * @param from
	 * @param distances
	 * @return
	 */
	private Node[] getPushes(Maze maze, Node from) {
		int boxR = row[from.boxId];
		int boxC = col[from.boxId];
		ArrayList<Node> pushes = new ArrayList<Node>();
		
		for (int i=0; i<2; i++) {
			
			to[0] = boxR+dy[i];
			to[1] = boxC+dx[i];
			oppTo[0] = boxR-dy[i];
			oppTo[1] = boxC-dx[i];
			
			if (maze.isEmpty(to[0], to[1]) && maze.isEmpty(oppTo[0], oppTo[1])) {
				// Push LEFT or push UP
				if (from.canReach[opp[i]]) {
					coord2[0] = boxR;
					coord2[1] = boxC;
					pushes.add(makeNode(maze, to, coord2));
				}
				// Push RIGHT or push DOWN
				if (from.canReach[i]) {
					coord2[0] = boxR;
					coord2[1] = boxC;
					pushes.add(makeNode(maze, oppTo, coord2));
				}
			}
		}
		return pushes.toArray(new Node[0]);
	}
	private Node makeNode(Maze maze, int[] box, int[] player) {
		maze.setBoxAt(box[0], box[1]);
		maze.getDistances(player[0], player[1], distances);
		maze.setEmptyAt(box[0], box[1]);
		getNorm(distances, coord);
		for (int j=0; j<4; j++) {
			if (distances[box[0]+dy[j]][box[1]+dx[j]] > -1) {
				canReach[j] = true;
			}
			else canReach[j] = false;
		}
		return new Node(canReach, coord, index[box[0]][box[1]]);
	}
	/**
	 * Get topmost, left coordinate of the player's reachable area.
	 *  
	 * @param distances
	 * @param norm
	 */
	private void getNorm(int[][] distances, int[] norm) {
		for (int r=0; r<distances.length; r++) {
			for (int c=0; c<distances[r].length; c++) {
				if (distances[r][c] > -1) {
					norm[0] = r;
					norm[1] = c;
					return;
				}
			}
		}
	}
	class Node {
		int boxId;
		int[] norm;
		boolean[] canReach; // The player reachable squares around the box. LEFT = 0, UP = 1, RIGHT = 2, DOWN = 3;
		
		public Node(boolean canReach[], int norm[], int boxId) {
			this.norm = new int[2];
			this.canReach = new boolean[4];
			System.arraycopy(norm, 0, this.norm, 0, 2);
			System.arraycopy(canReach, 0, this.canReach, 0, 4);
			this.boxId = boxId;
		}
		public void setNormPlayer(int[] pos) {
			norm[0] = pos[0];
			norm[1] = pos[1];
		}
		@Override
		public int hashCode() {
			return boxId;
		}
		public boolean equals(Node node) {
			return (boxId == node.boxId) && (norm[0] == node.norm[0]) && (norm[1] == node.norm[1]);
		}
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Node) {
				return equals((Node)obj);
			}
			else {
				return super.equals(obj);
			}
		}
		
	}
}
