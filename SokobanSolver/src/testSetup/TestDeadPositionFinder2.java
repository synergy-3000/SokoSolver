package testSetup;
import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gui.Controller;
import setup.CollectionsReader;
import setup.Maze;
import setup.MazeState;
import solver.DeadPositionFinder2;
import solver.DeadPositionFinder3;

class TestDeadPositionFinder2 {
	
	DeadPositionFinder3 dpf;
	
	public TestDeadPositionFinder2() {
		dpf = new DeadPositionFinder3();
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
		//int[] dead = dpf.getDeadPositions(null, maze);
		
		/*int idx = 0;
		int nRows = maze.numRows();
		int nCols = maze.numCols();
		int[] rowIdx = new int[nRows*nCols];
		int[] colIdx = new int[nRows*nCols];
		
		for (int r=0; r<nRows; r++) {
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
		File file = new File("/Users/zhipinghe/Desktop/SokobanOriginalLevels.txt");
		List<MazeState> mstates = new CollectionsReader().readCollection(file);
		for (MazeState ms : mstates) {
			maze.setNewMaze(ms);
			dpf.getDeadPositions(null, maze);
		}
		int a = 1, b = 2, c = 3, d=4,e=5,f;
		String s2 = "";;
		String s = b < a ? " b < a" : b == a ? " b == a" : (s2 = " b > a");
		System.out.println("b=" + b + " a=" + a + " s=" + s + " s2=" +s2);
		a = b;
		s = b < a ? " b < a" : b == a ? " b == a" : " b > a";
		System.out.println("b=" + b + " a=" + a + s);
		a = b + 1;
		s = b < a ? " b < a" : b == a ? " b == a" : " b > a";
		System.out.println("b=" + b + " a=" + a + s);
		
	}
	@Test
	void testGetPathToGoal() {
		/*Maze maze = Controller.getInstance().getMaze();
		int[][] boxes = maze.getBoxLocations();
		int[] player = new int[2];
		maze.getPlayerLocation(player);
		boolean isGoal[] = maze.getIsGoalNode();
		int[] path = dpf.getPathToGoal(maze, boxes[0][0], boxes[0][1], isGoal, player[0], player[1]);
		*/
	}
}
