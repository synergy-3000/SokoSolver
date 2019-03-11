package testSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gui.Controller;
import setup.Maze;
import solver.DeadPositionFinder2;

class TestDeadPositionFinder2 {
	
	DeadPositionFinder2 dpf;
	
	public TestDeadPositionFinder2() {
		dpf = new DeadPositionFinder2();
	}
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetDeadPositions() {
		Maze maze = Controller.getInstance().getMaze();
		int[] dead = dpf.getDeadPositions(null, maze);
		
		int idx = 0;
		int nRows = maze.numRows();
		int nCols = maze.numCols();
		int[] rowIdx = new int[nRows*nCols];
		int[] colIdx = new int[nRows*nCols];
		
		/*for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (!maze.isWall(r, c) && !maze.isOutsideMaze(r, c)) {
					rowIdx[idx] = r;
					colIdx[idx] = c;
					idx++;
				}
			}
		}
		for (int deadIdx : dead) {
			
		}*/
	}
	@Test
	void testGetPathToGoal() {
		Maze maze = Controller.getInstance().getMaze();
		int[][] boxes = maze.getBoxLocations();
		int[] player = new int[2];
		maze.getPlayerLocation(player);
		boolean isGoal[] = maze.getIsGoalNode();
		int[] path = dpf.getPathToGoal(maze, boxes[0][0], boxes[0][1], isGoal, player[0], player[1]);
		
	}
}
