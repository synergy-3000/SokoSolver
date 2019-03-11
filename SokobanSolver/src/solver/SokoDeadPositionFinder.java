package solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

import gui.Direction;
import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.Node;
import setup.SokoMaze;
import utils.Utils;
//TODO Change so that new arrays aren't allocated each call to getPathToGoal and getDeadPositions
public class SokoDeadPositionFinder implements DeadPositionFinder {

	static SokoDeadPositionFinder instance;

	public static SokoDeadPositionFinder getInstance() {
		if (instance == null) {
			instance = new SokoDeadPositionFinder();
		}
		return instance;
	}

	Node[] nodes;
	int nNodes;

	int[] nodeIds, pushes;
	int[] parent;
	int nPushes;
	int[] toPos, fromPos;
	

	final int MAX_PUSHES = 50;
	
	private SokoDeadPositionFinder() {
		toPos = new int[2];
		fromPos = new int[2];
	}
	
	@Override
	public int[] getDeadPositions(Graph graph, Maze maze) {
		GraphCreator gc = GraphCreator.getGraphCreator();
		
		// For each empty square inside maze check that if a box placed in it could be pushed to a goal square
		int id, pNode = -1;
		boolean isDead;
		ArrayList<Integer> deadPosns = new ArrayList<Integer>();
		boolean[] goals = maze.getIsGoalNode();
		int[] path;
		Node[] nodes = graph.getNodes();
		
		for (Node node : nodes) {
			
			graph.clearVisits();
			isDead = true;

			// Have to find 2 opposite empty squares around square if box 
			// can be pushed at all.
			id = node.getId();
			
			// Did we start on a goal square ?
			if (!goals[id]) {
				if ( (pNode = gc.getPlayerNodeForPush(id, Direction.DOWN)) == -1 ) {
					pNode = gc.getPlayerNodeForPush(id, Direction.LEFT);
				}
				if (pNode != -1) {
					//System.out.println("getPathToGoal(...) id:%d");
					path = getPathToGoal(graph, id, goals, pNode);
					isDead = (path == null);
				}
				if (isDead) deadPosns.add(id);
			}
		}
		int[] retVal = new int[deadPosns.size()];
		int i=0;
		for (int dPosn : deadPosns) {
			retVal[i] = dPosn;
			i += 1;
		}
		return retVal;
	}

	/*
	 * The player must be initially placed next to the box for this algorithm to
	 * work (non-Javadoc)
	 * 
	 * @see solver.DeadPositionFinder#getPathToGoal(setup.Graph, solver.BoxPusher,
	 * int, int, int)
	 */
	public int[] getPathToGoal(Graph graph, int fromNode, boolean[] isGoalNode, int playerNode) {

		nPushes = 0;
		nodes = graph.getNodes();
		//if (nodeIds == null) {
			nodeIds = new int[MAX_PUSHES];
			pushes = new int[MAX_PUSHES];
			parent = new int[nodes.length*4];
		//}

		
		nNodes = nodes.length;
		int playerRow = graph.getNodeRow(playerNode);
		int playerCol = graph.getNodeCol(playerNode);
		boolean found = false;

		// Do a Breadth First Search to Goal Square
		LinkedList<Integer> queue = new LinkedList<Integer>(); // Node Ids
		LinkedList<Integer> pRowQueue = new LinkedList<Integer>(); // player row
		LinkedList<Integer> pColQueue = new LinkedList<Integer>(); // player col
		LinkedList<Integer> nPushesQueue = new LinkedList<Integer>();

		queue.add(fromNode);
		pRowQueue.add(playerRow);
		pColQueue.add(playerCol);
		nPushesQueue.add(nPushes);

		int[] pushDistances = new int[nodes.length];
		int[] secondVisit = new int[nodes.length];
		int[] visits = new int[nodes.length];
		Arrays.fill(visits, 0);
		Arrays.fill(pushDistances, -1);
		Arrays.fill(secondVisit, -1);

		pushDistances[fromNode] = 0;
		nodeIds[nPushes] = fromNode;
		pushes[nPushes++] = 0;
		int boxRow = graph.getNodeRow(fromNode);
		int boxCol = graph.getNodeCol(fromNode);
		parent[getHashCode(graph, fromNode, playerRow, playerCol)] = -1;
		
		int parentHash;

		while (!queue.isEmpty() && !found) {

			int nodeId = queue.poll();
			playerRow = pRowQueue.poll();
			playerCol = pColQueue.poll();
			int prevPush = nPushesQueue.poll();

			boxRow = graph.getNodeRow(nodeId);
			boxCol = graph.getNodeCol(nodeId);

			int[] neighbours = nodes[nodeId].visit(playerRow, playerCol);
			int neighbour;
			parentHash = getHashCode(graph, nodeId, playerRow, playerCol);
			
			for (int i=0; (i<neighbours.length) && !found; i++) {
				neighbour = neighbours[i];
				if (isGoalNode[neighbour])
					found = true;

				// The player will be in the position of the old box position
				if (!nodes[neighbour].visited(boxRow, boxCol)) {

					if (!(nPushes < nodeIds.length))
						reallocateArrays();
					nodeIds[nPushes] = nodes[neighbour].getId();
					
					parent[getHashCode(graph, nodeIds[nPushes], boxRow, boxCol)] = parentHash;
					pushes[nPushes] = pushes[prevPush] + 1;

					visits[neighbour] += 1;

					queue.add(neighbour);
					pRowQueue.add(boxRow);
					pColQueue.add(boxCol);
					nPushesQueue.add(nPushes);
					nPushes += 1;
				}
			}
		}
		
		if (found) {
			int nodeId = nodeIds[nPushes-1];
			int step = pushes[nPushes-1];
			int[] path = new int[step+1];
			path[step] = nodeId;
			step -= 1;
			parentHash = parent[getHashCode(graph, nodeId, boxRow, boxCol)];
			
			while (parentHash != -1) {
				path[step] = idFromHash(parentHash);
				step -= 1;
				parentHash = parent[parentHash];
			}
			
			Utils.printArray(path, "path", path.length);
			//Utils.printMaze(graph.g);

			return path;
		} else {
			return null;
		}
	}
	
	private int getHashCode(Graph graph, int nodeId, int playerRow, int playerCol) {
		
		// Direction from player to node
		fromPos[0] = graph.getNodeRow(nodeId);
		fromPos[1]= graph.getNodeCol(nodeId);
		toPos[0] = playerRow;
		toPos[1] = playerCol;
		
		Direction dirn = Direction.getDirection(fromPos, toPos);
		int dirnNum = 0;
		switch(dirn) {
		case UP:
			dirnNum = 0;
			break;
		case DOWN:
			dirnNum = 1;
			break;
		case LEFT:
			dirnNum = 2;
			break;
		case RIGHT:
			dirnNum = 3;
			break;
		}
		return (nodeId * 4) + dirnNum; 
	}
	private int idFromHash(int hash) {
		return hash/4;
	}
	private void reallocateArrays() {
		System.out.println("reallocateArrays()");
		nodeIds = Arrays.copyOf(nodeIds, nodeIds.length + MAX_PUSHES);
		pushes = Arrays.copyOf(pushes, pushes.length + MAX_PUSHES);
	}
	
	class DistancesComparator implements Comparator<Integer> {

		int[] pushDistances;

		public DistancesComparator(int[] pushDistances) {
			this.pushDistances = pushDistances;
		}

		public int compare(int index1, int index2) {

			return -Integer.compare(pushDistances[index1], pushDistances[index2]);
		}

		@Override
		public int compare(Integer o1, Integer o2) {
			return compare(o1.intValue(), o2.intValue());
		}

	}
}
