package solver;

import java.util.Arrays;
import java.util.LinkedList;

import setup.Graph;
import setup.Node;

// TODO Remove this failed 1st attempt. 
public class PathToGoalFinder {
	
	Node[] nodes;
	int nNodes = 0;
	
	static PathToGoalFinder ptgf = null;
	
	public static PathToGoalFinder getInstance() {
		if (ptgf == null) {
			ptgf = new PathToGoalFinder();
		}
		return ptgf;
	}
	// Do a breadth first search to find a path to a goal square
	public String findPathToGoal(Graph graph, int startId) {
		nodes = graph.getNodes();
		nNodes = nodes.length;
		int[] neighbours;
		int playerRow, playerCol;
		
		int[] pushDistances = new int[nNodes];
		Arrays.fill(pushDistances, -1);
		pushDistances[startId] = 0;
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(startId);
		
		/*while (!queue.isEmpty()) {
			int node = queue.poll();
			playerRow = graph.getNodeRow(node);
			playerCol = graph.getNodeCol(node);
			//neighbours = nodes[node].getNeighbours(playerRow, playerCol);
			for (int neighbour :neighbours) {
				if (pushDistances[neighbour] == -1 || // We've visited but the node has soft edges and the player position was different) 
						{                             // Node.hasVisited
					pushDistances[neighbour] = pushDistances[node] + 1;
					queue.add(neighbour);
				}
				
			}
		}*/
		return " ";
	}
}
