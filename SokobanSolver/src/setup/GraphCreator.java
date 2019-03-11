package setup;

import java.util.Arrays;

import gui.Controller;
import gui.Direction;
/**
 * The GraphCreator class is only used by SokoDeadPositionFinder for finding the Dead Positions in the maze.
 * It was a first attempt and in hindsight it is not needed. All the functionality can be put in SokoDeadPositionFinder
 * TODO Modify SokoDeadPositionFinder to not need a Graph or GraphCreator and delete GraphCreator
 * 
 * @author zhipinghe
 *
 */
public class GraphCreator implements Graph {
	
	final int ROW = 0;
	final int COL = 1;
	
	int PermanentEdgesTo[][];   // The player can reach all sides of the box at this position
	int PermanentEdgesFrom[][];
	int SoftEdgesTo[][];		// These edges of the graph change depending on the player position
	int SoftEdgesFrom[][];  	// because the player cannot reach at least one side of the box. 
	int nFrom[], nTo[];
	Node nodes[];
	int ids[][], nNodes, nodeRows[], nodeColumns[];
	boolean hasSoftEdges[];      // box pushes from this node are soft?
	int boxes[], nBoxes;
	int goalSquares[], nGoals;
	int player;
	int[][] reachable;
	
	Maze maze;
	
	int[] fr, to, playerPos;
	
	private static GraphCreator graphCreator;
	
	public static GraphCreator getGraphCreator() {
		if (graphCreator == null) {
			graphCreator = new GraphCreator();
		}
		return graphCreator;
	}
	private GraphCreator() {
		fr = new int[2];
		to = new int[2];
		playerPos = new int[2];
		
	}
	public Graph createPushGraph(Maze maze) {
		this.maze = maze;
		int nMazeRows = maze.numRows();
		int nMazeCols = maze.numCols();
		nNodes = 0;
		nBoxes = 0;
		
		// Assign an index for each empty square
		ids = new int[nMazeRows][nMazeCols];
		
		// Player reachable squares
		reachable = new int[nMazeRows][nMazeCols];
		
		for (int r=0; r < nMazeRows; r++) {
			for (int c=0; c < nMazeCols; c++) {
				if ( !maze.isWall(r, c) && !maze.isOutsideMaze(r, c) ) {
					if (maze.isBox(r, c)) nBoxes++;
					ids[r][c] = nNodes++;
				}
				else {
					ids[r][c] = -1;
				}
			}
		}
		int[] mpPlayer = new int[2];
		maze.getPlayerLocation(mpPlayer);
		player = ids[mpPlayer[0]][mpPlayer[1]];
		
		boxes = new int[nBoxes];
		nodeRows = new int[nNodes];
		nodeColumns = new int[nNodes];
		nFrom = new int[nNodes];
		nTo = new int[nNodes];
		nodes = new Node[nNodes];
		hasSoftEdges = new boolean[nNodes];
		SoftEdgesFrom = new int[nNodes][4];
		SoftEdgesTo = new int[nNodes][4];
		PermanentEdgesFrom = new int[nNodes][4];
		PermanentEdgesTo = new int[nNodes][4];
		
		int id;
		int boxCount = 0;
		for (int r=0; r < nMazeRows; r++) {
			for (int c=0; c < nMazeCols; c++) {
				id = ids[r][c];
				if (id >= 0) {
					nodeRows[id] = r;
					nodeColumns[id] = c;
					
					hasSoftEdges[id] = false;
					
					for (int i=0; i<4; i++) {
						PermanentEdgesFrom[id][i] = -1;
						PermanentEdgesTo[id][i] = -1;
						SoftEdgesFrom[id][i] = -1;
						SoftEdgesTo[id][i] = -1;
					}
				}
				if (maze.isBox(r, c)) boxes[boxCount++] = id;
			}
		}
		addEdges();
		
		// Debug: Print graph
		System.out.println("Pushes for each square");
		int row, col; 
		int boxPos[], toPos[];
		boxPos = new int[2];
		toPos = new int[2];
		for (id=0; id<nNodes; id++) {
			row = nodeRows[id];
			col = nodeColumns[id];
			boxPos[ROW] = row;
			boxPos[COL] = col;
			System.out.println(String.format("nFrom[%d] = %d", id, nFrom[id]));
			if (hasSoftEdges[id]) {
				System.out.println(String.format("(%d,%d) has soft edges ",row,col));
				for (int j=0; j<nFrom[id]; j++) {
					toPos[ROW] = nodeRows[SoftEdgesFrom[id][j]];
					toPos[COL] = nodeColumns[SoftEdgesFrom[id][j]];
					String outStr = String.format("[%s]", Direction.getDirection(boxPos, toPos));
					System.out.print(outStr);
				}
				System.out.println();
			}
			else {
				System.out.println(String.format("(%d,%d) ",row,col));
				for (int j=0; j<nFrom[id]; j++) {
					toPos[ROW] = nodeRows[PermanentEdgesFrom[id][j]];
					toPos[COL] = nodeColumns[PermanentEdgesFrom[id][j]];
					String outStr = String.format("[%s]", Direction.getDirection(boxPos, toPos));
					System.out.print(outStr);
				}
				System.out.println();
			}
		}
		return this;
	}
	// Add the edges
	// Clear all boxes, a new box will be placed at each square in the maze
	// and the possible pushes calculated
	void addEdges() {
		int id;
		int row,col;
		int toNodeIds[];
		int nodeId;
		
		toNodeIds = new int[4];
		
		// Clear all boxes
		for (int i=0; i<nBoxes; i++) {
			id = boxes[i];
			maze.setEmptyAt(nodeRows[id], nodeColumns[id]);
		}
		// For each square get where the box can be pushed to
		for (id = 0; id < nNodes; id++) {
			row = nodeRows[id]; col = nodeColumns[id];
			maze.setBoxAt(row, col);
			
			// Get player reachable array. This is dependent on the box location and player location
			maze.getDistances(nodeRows[player], nodeColumns[player], reachable);
			
			nFrom[id] = 0;
			for (Direction pushDirn : Direction.values()) {
				 nodeId = getNode(id, pushDirn, reachable);
				 if (nodeId >= 0) {
					toNodeIds[nFrom[id]] = nodeId;
					nFrom[id] += 1;
				 }
			}
			if (hasSoftEdges[id] == false) {
				for (int i=0; i<nFrom[id]; i++) {
					PermanentEdgesFrom[id][i] = toNodeIds[i];
					PermanentEdgesTo[toNodeIds[i]][i] = id;
					nTo[toNodeIds[i]] += 1;
				}
				nodes[id] = new PushBoxNode(id, Arrays.copyOf(PermanentEdgesFrom[id], nFrom[id]));
			}
			else { // Soft edge node
				for (int i=0; i<nFrom[id]; i++) {
					SoftEdgesFrom[id][i] = toNodeIds[i];
					SoftEdgesTo[toNodeIds[i]][i] = id;
				}
				nodes[id] = createSoftNode(toNodeIds, nFrom[id], id);
			}
			// Clear old box position
			maze.setEmptyAt(row, col);
		}
	}
	/* Returns node id or -1 if push can't be performed e.g A wall, 
	 * a wall blocking the push, or unreachable square
	 * 
	 */
	private int getNode(int fromId, Direction pushDirn, int[][] distances) {
		int retVal = -1;
		int toPos[] = new int[2];
		int playerPos[] = new int[2];
		int boxRow = nodeRows[fromId]; 
		int boxCol = nodeColumns[fromId];
		
		getPositions(boxRow, boxCol, pushDirn, playerPos, toPos);
		
		// Player and 'to' square have to be empty
		if (maze.isEmpty(toPos[ROW], toPos[COL]) && maze.isEmpty(playerPos[ROW], playerPos[COL])) {
			
			// Player square has to be reachable
			if ( distances[playerPos[ROW]][playerPos[COL]] >= 0 ) {
				// There is an edge from box node to 'to' node
				retVal = ids[toPos[ROW]][toPos[COL]];
			}
			else { // A Soft edge. This edge might exist if the player is in a different position
				hasSoftEdges[fromId] = true;
			}
		}

		return retVal;
	}
	/*
	 * Calculates which directions are pushable, boolean canPush[], for the box located at <code>boxRow,boxCol</code>
	 * in the same order as Direction.values().
	 */
	public int getPushableDirections(int boxRow, int boxCol, boolean[] canPush) {
		int i=0, nPushes = 0;
		int[][] reachable = Controller.getInstance().getReachable();
		System.out.println("getPushableDirections:for box at (row,col): (" + boxRow + "," + boxCol + ")");
		printReachable(reachable);
		for (Direction dirn : Direction.values()) {
			canPush[i] = false;
			getPositions(boxRow, boxCol, dirn, playerPos, to);
			
			// Player and 'to' square have to be empty
			if (maze.isEmpty(to[ROW], to[COL]) && maze.isEmpty(playerPos[ROW], playerPos[COL])) {

				// Player square has to be reachable
				if ( reachable[playerPos[ROW]][playerPos[COL]] >= 0 ) {
					canPush[i] = true;
					nPushes++;
				}
			}
			i += 1;
		}
		String s = String.format("Pushes for stone at (%d,%d): ", boxRow,boxCol);
		int j = 0;
		for (Direction dirn : Direction.values()) {
			s += canPush[j] ?  dirn : "";
			s += " ";
			++j;
		}
		System.out.println(s);
		return nPushes;
	}
	// Debug
	private void printReachable(int[][] array) {
		String row;
		for (int r=0; r<maze.numRows(); r++) {
			row = "";
			for (int c=0; c<maze.numCols(); c++) {
				if (array[r][c] >= 0 && array[r][c] < 10) row += " "; 
				row += array[r][c] + ","; 
			}
			System.out.println(row);
		}
	}
	/* Returns the node for the player if push can be performed else -1
	 *   
	 */
	public int getPlayerNodeForPush(int fromId, Direction pushDirn) {
		int retVal = -1;
		int toPos[] = new int[2];
		int playerPos[] = new int[2];
		int boxR = nodeRows[fromId];
		int boxC = nodeColumns[fromId];
		
		getPositions(boxR, boxC, pushDirn, playerPos, toPos);
		
		if (maze.isEmpty(toPos[ROW], toPos[COL]) && maze.isEmpty(playerPos[ROW], playerPos[COL])) {
			retVal = ids[playerPos[ROW]][playerPos[COL]];
		}
		return retVal;
	}
	
	private Node createSoftNode(int toNodeIds[], int numFrom, int nodeId) {
		
		int playerPos[][] = new int[4][2];
		int nPlayerPos = 0; 
		
		int[][] pushToIds = new int[4][4];  // For each player position in normPlayers have an array of allowed
		                                    // pushes. 4 player squares and 4 possible pushes for each square.
		int[] nPushes = new int[4];			// Number of pushes for each player square
		int row, col;
		int regionNum[] = new int[4];
		int nRegions = 0;
		Arrays.fill(regionNum, -1);
		int[] toPos = new int[2];
		
		int boxRow = nodeRows[nodeId];
		int boxCol = nodeColumns[nodeId];
		
		
		
		boolean canPush[] = new boolean[4];
		Arrays.fill(canPush, false);
		
		int[][] regionPushToIds;
		int[] nRegionPushToIds;
		
		// Debug
		System.out.println("GraphCreator.createSoftNode()");
		
		// How many empty squares around nodeId?
		
		for (Direction dirn : Direction.values()) {
			
			getPositions(boxRow, boxCol, dirn, playerPos[nPlayerPos], toPos);
			
			if (maze.isEmpty(playerPos[nPlayerPos][ROW], playerPos[nPlayerPos][COL])) {
				
				if (maze.isEmpty(toPos[ROW], toPos[COL])) {
					canPush[nPlayerPos] = true;
					pushToIds[nPlayerPos][0] = getNodeAt(toPos[ROW], toPos[COL]);
					nPushes[nPlayerPos] = 1;
				}
				nPlayerPos++;
			}
		}
		// Find which player positions are in the same region
		for (int i = 0; i<nPlayerPos; i++) {
			if (regionNum[i] == -1) {
				regionNum[i] = nRegions;
				nRegions += 1;
			}
			maze.getDistances(playerPos[i][ROW], playerPos[i][COL], reachable);
			for (int j = (i+1); j<nPlayerPos; j++) {
				row = playerPos[j][ROW];
				col = playerPos[j][COL];
				
				if (reachable[row][col] >= 0) {
					regionNum[j] = regionNum[i];
				}
			}
		}
		regionPushToIds = new int[nRegions][4];
		nRegionPushToIds = new int[nRegions];
		
		// Create pushToIds array
		for (int i=0; i<nRegions; i++) {
			nRegionPushToIds[i] = 0;
			for (int j=0; j<nPlayerPos; j++) {
				if ( (regionNum[j] == i) && (canPush[j])) {
					regionPushToIds[i][nRegionPushToIds[i]] = pushToIds[j][0];
					nRegionPushToIds[i] += 1;
				}
			}
		}
		// Debug
		for (int i=0; i<nPlayerPos; i++) {
			String outStr = String.format("Player Pos (%d,%d) regionNum = %d", playerPos[i][ROW], playerPos[i][COL], regionNum[i]);
			System.out.println(outStr);
			System.out.println("Pushes to (node Id) : ");
			outStr = "";
			
			for (int k=0; k<nRegionPushToIds[regionNum[i]]; k++) {
				outStr += String.format("[%d] ", regionPushToIds[regionNum[i]][k]);
			}
			System.out.println(outStr);
		}
		return new PushBoxSoftNode(nodeId, playerPos, nPlayerPos, regionNum, regionPushToIds, nRegionPushToIds );
		
		// End Debug
		
	}
	
	public String nodeIdToString(int id) {
		int row,col;
		row = nodeRows[id];
		col = nodeColumns[id];
		return String.format("(%d,%d)",row,col);
	}
	/* Return the player position and the destination position for the given
	 * box position and push direction. All in row, column coordinates
	 * 
	 */
	private void getPositions(int boxRow, int boxCol, Direction pushDirn, 
			int playerPos[], int toPosition[]) {
		
		int boxPosition[] = new int[2];
		boxPosition[ROW] = boxRow;
		boxPosition[COL] = boxCol;
		
		pushDirn.getToPosition(boxPosition, toPosition);
		pushDirn.opposite().getToPosition(boxPosition, playerPos);
	}
	
	public int[] getRowColPos(int nodeId) {
		int[] retVal = new int[2];
		retVal[ROW] = nodeRows[nodeId];
		retVal[COL] = nodeColumns[nodeId];
		
		return retVal;
	}
	/* Returns -1 if not an empty square
	 * (non-Javadoc)
	 * @see setup.Graph#getNodeAt(int, int)
	 */
	public int getNodeAt(int row, int col) {
		return ids[row][col];
	}
	@Override
	public int getNodeRow(int nodeId) {
		return nodeRows[nodeId];
	}
	@Override
	public int getNodeCol(int nodeId) {
		return nodeColumns[nodeId];
	}
	@Override
	public Node[] getNodes() {
		return nodes;
	}
	@Override
	public void clearVisits() {
		for (Node node : nodes) {
			node.clearVisits();
		}
	}
}
