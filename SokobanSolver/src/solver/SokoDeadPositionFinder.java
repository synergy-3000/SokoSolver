package solver;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;

import setup.Graph;
import setup.MazePosition;
import setup.Node;
import utils.Utils;

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
	int nPushes;

	final int MAX_PUSHES = 50;

	@Override
	public MazePosition[] getDeadPositions(Graph graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	/*
	 * The player must be initially placed next to the box for this algorithm to
	 * work (non-Javadoc)
	 * 
	 * @see solver.DeadPositionFinder#getPathToGoal(setup.Graph, solver.BoxPusher,
	 * int, int, int)
	 */
	public int[] getPathToGoal(Graph graph, BoxPusher boxPusher, int fromNode, boolean[] isGoalNode, int playerNode) {

		nPushes = 0;
		if (nodeIds == null) {
			nodeIds = new int[MAX_PUSHES];
			pushes = new int[MAX_PUSHES];
		}

		nodes = graph.getNodes();
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

		boxPusher.setBoxPosition(graph.getNodeRow(fromNode), graph.getNodeCol(fromNode));

		while (!queue.isEmpty() && !found) {

			int nodeId = queue.poll();
			playerRow = pRowQueue.poll();
			playerCol = pColQueue.poll();
			int prevPush = nPushesQueue.poll();

			// TODO boxPusher not required because the algorithm does not need a box to be
			// placed in the maze
			boxPusher.setBoxPosition(graph.getNodeRow(nodeId), graph.getNodeCol(nodeId));
			boxRow = graph.getNodeRow(nodeId);
			boxCol = graph.getNodeCol(nodeId);

			int[] neighbours = nodes[nodeId].visit(playerRow, playerCol);

			// visits[nodeId] += 1;

			for (int neighbour : neighbours) {
				if (isGoalNode[neighbour])
					found = true;

				// The player will be in the position of the old box position
				if (!nodes[neighbour].visited(boxRow, boxCol)) {

					if (!(nPushes < nodeIds.length))
						reallocateArrays();
					nodeIds[nPushes] = nodes[neighbour].getId();
					pushes[nPushes] = pushes[prevPush] + 1;

					visits[neighbour] += 1;

					// TODO Remove pushDistances array. It is replaced by the arrays above -
					// nodeIds[], pushes[]

					// pushDistances[neighbour] = pushDistances[nodeId] + 1;
					// 1st Visit ?
					if (pushDistances[neighbour] == -1) {
						pushDistances[neighbour] = Math.max(pushDistances[nodeId], secondVisit[nodeId]) + 1;
					}
					// Must be 2nd visit
					else {
						secondVisit[neighbour] = Math.max(pushDistances[nodeId], secondVisit[nodeId]) + 1;
					}
					// TODO End Remove
					queue.add(neighbour);
					pRowQueue.add(boxRow);
					pColQueue.add(boxCol);
					nPushesQueue.add(nPushes);
					nPushes += 1;
				}
			}
		}
		// TODO Remove the following code
		Utils.printArray(nodeIds, "nodeIds", nPushes);
		Utils.printArray(pushes, "pushes", nPushes);
		// Debug: Print out pushDistances
		System.out.println("found = " + found);
		String outStr = "Node Ids =        [";
		int i;
		for (i = 0; i < pushDistances.length - 1; i++) {
			if ((i < 10) && (i >= 0))
				outStr += "0";
			outStr += Integer.toString(i) + ", ";
		}
		if ((i < 10) && (i > 0))
			outStr += "0";
		outStr += Integer.toString(i) + "]";
		System.out.println(outStr);

		Utils.printArray(pushDistances, "pushDistances", pushDistances.length);

		Utils.printArray(secondVisit, "secondVisit", secondVisit.length);

		System.out.println("visits = " + Arrays.toString(visits));

		// Sort pushDistances array
		Integer[] nodeId = new Integer[pushDistances.length * 2];
		for (i = 0; i < nodeId.length; i++) {
			nodeId[i] = Integer.valueOf(i);
		}
		// Arrays.sort(nodeId, new DistancesComparator(pushDistances));
		// Tack on secondVisit[]
		int[] pushStep = Arrays.copyOf(pushDistances, pushDistances.length * 2);
		for (i = pushDistances.length; i < pushStep.length; i++) {
			pushStep[i] = secondVisit[i - pushDistances.length];
		}
		Arrays.sort(nodeId, new DistancesComparator(pushStep));

		// Print out sorted array
		outStr = "nodeId =   [";
		int modNodeId;
		for (i = 0; i < nodeId.length - 1; i++) {
			modNodeId = nodeId[i] % nNodes;
			if ((modNodeId < 10) && (modNodeId >= 0))
				outStr += "0";
			outStr += Integer.toString(modNodeId) + ", ";
		}
		outStr += (nodeId[i] % nNodes) + "]";
		System.out.println(outStr);

		outStr = "sorted =   [";
		for (i = 0; i < nodeId.length - 1; i++) {
			if ((pushStep[nodeId[i]] < 10) && (pushStep[nodeId[i]] >= 0))
				outStr += "0";
			outStr += Integer.toString(pushStep[nodeId[i]]) + ", ";
		}
		outStr += pushStep[nodeId[i]] + "]";
		System.out.println(outStr);

		// TODO End of remove code
		if (found) {
			// Determine path (new algorithm)
			Deque<Integer> stack = new ArrayDeque<Integer>();
			int indexPos = nPushes - 1;
			// Node curNode = getNode(nodeId, indexPos);
			Node curNode = nodes[nodeIds[indexPos]];
			int step = pushes[indexPos];
			System.out.println("step = " + step);
			int[] path = new int[step + 1];
			path[step] = curNode.getId();
			stack.push(curNode.getId());
			indexPos--;

			while ((indexPos >= 0) && (step > 0)) {
				Node nextNode = nodes[nodeIds[indexPos]];
				if (isNextNode(pushes, nextNode, curNode, indexPos, step)) {
					curNode = nextNode;
					step -= 1;
					stack.push(curNode.getId());
					path[step] = curNode.getId();
				}
				indexPos--;
			}
			// Print path
			System.out.println("step = " + step);
			if (step == 0) {
				System.out.println("step = 0");
				outStr = "[ ";

				while (!stack.isEmpty()) {
					outStr += stack.pop();
					if (!stack.isEmpty())
						outStr += ", ";
					else
						outStr += "]";
				}
				System.out.println(outStr);
			}
			Utils.printArray(path, "path", path.length);
			Utils.printMaze();

			return path;
		} else {
			return null;
		}
	}

	private void reallocateArrays() {
		System.out.println("reallocateArrays()");
		nodeIds = Arrays.copyOf(nodeIds, nodeIds.length + MAX_PUSHES);
		pushes = Arrays.copyOf(pushes, pushes.length + MAX_PUSHES);
	}

	private boolean isNextNode(int[] pushes, Node nextNode, Node curNode, int indexPos, int step) {
		boolean retVal = false;
		if (pushes[indexPos] == (step - 1)) {
			if (nextNode.canPushTo(curNode.getId())) {
				retVal = true;
			}
		}
		return retVal;
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
