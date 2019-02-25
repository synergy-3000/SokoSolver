package setup;

import java.util.Arrays;
import java.util.HashMap;

import solver.MyMazePosition;
import solver.Region;

public class PushBoxSoftNode extends PushBoxNode implements Node {
	
	final int ROW = 0;
	final int COL = 1;
	
	int[][] playerPos;
	int nPlayerPos;
	
	int[] regionNum;
	int   nRegions;
	int[][] regionPushToIds;
	int[]   nRegionPushToIds;
	
	boolean[] visited = new boolean[4];
	
	int[][] playerIds = new int[3][3];
	int minRow, minCol;
	
	int[][] regionPushes;
	
	
	/* The idea is that each empty square around the box has a list of nodeIds that the box can be pushed to.
	 * Not all are in the player reachable area for the player's current position. If all are reachable then 
	 * the node is just a PushBoxNode and not a PushBoxSoftNode. So each possible player position around the box
	 * is tested to see which other player positions are connected to it. This will be a 'region'.    
	 */
	public PushBoxSoftNode(int nodeId, int[][] playerPos, int nPlayerPos, int[] regionNum, int[][] regionPushToIds,
			int[] nRegionPushToIds) {
		
		super(nodeId, new int[0]);
		
		this.playerPos = playerPos;
		this.nPlayerPos = nPlayerPos;
		this.regionNum = regionNum;
		this.regionPushToIds = regionPushToIds;
		this.nRegionPushToIds = nRegionPushToIds;
		
		Arrays.fill(visited, false);
		minRow = playerPos[0][ROW];
		minCol = playerPos[0][COL];
		
		for (int i=1; i<nPlayerPos; i++) {
			minRow = Math.min(minRow, playerPos[i][ROW]);
			minCol = Math.min(minCol, playerPos[i][COL]);
		}
		for (int i=0; i<nPlayerPos; i++) {
			playerIds[playerPos[i][ROW] - minRow][playerPos[i][COL] - minCol] = i;
		}
		nRegions = nRegionPushToIds.length;
		regionPushes = new int[nRegions][];
		
		for (int i=0; i<nRegions; i++) {
			regionPushes[i] = Arrays.copyOf(regionPushToIds[i], nRegionPushToIds[i]);
		}
		
	}
	@Override
	public boolean canPushTo(int toNodeId) {
		boolean found = false;
		for (int i=0; i<nRegions && !found; i++) {
			for (int j=0; j<regionPushes[i].length && !found; j++) {
				if (regionPushes[i][j] == toNodeId) {
					found = true;
				}
			}
		}
		return found;
	}

	private int getPlayerId(int playerRow, int playerCol) {
		return playerIds[playerRow - minRow][playerCol - minCol];
	}
	
	@Override
	public boolean visited(int playerRow, int playerCol) {
		
		int i = getPlayerId(playerRow, playerCol);
		
		return visited[regionNum[i]];
	}

	@Override
    /* Get this nodes neighbours (pushes) for the given player position
     * (non-Javadoc)
     * @see setup.PushBoxNode#visit(int, int)
     */
	public int[] visit(int playerRow, int playerCol) {
		
		int i = getPlayerId(playerRow, playerCol);
		
		visited[regionNum[i]] = true;
		
		// Debug
		System.out.println("PushBoxSoftNode.visit: ");
		System.out.println("nodeId: " + this.id);
		String outStr = String.format( "(playerRow,playerCol) (%d,%d) ", playerRow, playerCol);
		System.out.println(outStr);
		System.out.println("Pushes " + Arrays.toString(regionPushes[regionNum[i]]));
		// End Debug
		
		return regionPushes[regionNum[i]];
		
		
	}
	@Override
	public void clearVisits() {
		Arrays.fill(visited, false);
	}
}
