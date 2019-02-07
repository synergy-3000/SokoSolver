package solver;

import setup.Graph;
import setup.MazePosition;

public interface DeadPositionFinder {
	public MazePosition[] getDeadPositions(Graph graph);
	int[] getPathToGoal(Graph graph, BoxPusher boxPusher, int fromNode, boolean[] isGoalNode, int playerNode);
}
