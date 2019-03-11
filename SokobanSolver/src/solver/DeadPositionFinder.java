package solver;

import setup.Graph;
import setup.Maze;

public interface DeadPositionFinder {
	public int[] getDeadPositions(Graph graph, Maze maze);
}
