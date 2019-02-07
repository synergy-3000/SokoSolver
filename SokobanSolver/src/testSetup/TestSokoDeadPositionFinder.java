package testSetup;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.Node;
import setup.Reader;
import solver.SokoBoxPush;
import solver.SokoBoxPusher;
import solver.SokoDeadPositionFinder;

// TODO Investigate connecting Eclipse to GitHub
// TODO How to loop over an enumeration e.g SokoBoxPush.Direction
// TODO Remove obsolete TODOs 
//
class TestSokoDeadPositionFinder {
	File file;
	Reader aReader;
	SokoDeadPositionFinder finder;
	
	@BeforeEach
	void setUp() throws Exception {
		aReader = Reader.getReader();
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testGetDeadPositions() {
		//fail("Not yet implemented");
	}
	@Test
	void testGetAllDeadPositions() {
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		
		SokoBoxPusher sbp = new SokoBoxPusher(maze, 0, 0); // Not needed
		
		// For each empty square inside maze check if a box placed in it can be pushed to a goal square
		int numRows = maze.numRows();
		int numCols = maze.numCols();
		int idx = 0;
		for (Direction dirn : SokoBoxPush.Direction) {
			
		}
		for (int i=0; i<numRows; i++) {
			for (int j=0; j<numCols; j++) {
				if( (!maze.isOutsideMaze(i,j)) && (!maze.isWall(i, j))) {
					finder.getPathToGoal(graph, sbp, 24, 37, 25);
				}
				else {
					toAdd = String.valueOf(idx);
					idx += 1;
				}
				if (toAdd.length() == 1) toAdd = "+" + toAdd;
				strRow = strRow + toAdd;
			}
			System.out.println(strRow);
		}
	}
	@Test
	void testGetPathToGoal() {
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze2.txt");
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze3.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		Node[] nodes = graph.getNodes();
		
		int startRow = graph.getNodeRow(15);
		int startCol = graph.getNodeCol(15);
		
		finder = SokoDeadPositionFinder.getInstance();
		// for maze 3
		boolean[] goals = maze.getIsGoalNode();

		int[] path = finder.getPathToGoal(graph, new SokoBoxPusher(maze, startRow, startCol), 24, goals, 25);
		
		System.out.println("numRows(): " + maze.numRows() + " numCols(): " + maze.numCols());
		
		
		
		// for maze 2
		//finder.getPathToGoal(graph, new SokoBoxPusher(maze, startRow, startCol), 4, 0, 3);
		
		/*int boxNode = graph.getNodeAt(8, 10);
		int row, col;
		maze.setBoxAt(8, 10);
		int[] neighbours = nodes[boxNode].visit(9, 10);
		System.out.println("Neighbours of (8,10), player at (9,10)");
		String outStr = "";
		for (int neighbour : neighbours) {
			row = graph.getNodeRow(neighbour);
			col = graph.getNodeCol(neighbour);
			outStr += String.format(" (%d,%d) ", row, col);
		}
		System.out.println(outStr);
		Assertions.assertTrue(nodes[boxNode].visited(8, 11));
		Assertions.assertFalse(nodes[boxNode].visited(8,9));
		Assertions.assertFalse(nodes[boxNode].visited(7,10));
		
		nodes[boxNode].visit(8,9);
		
		Assertions.assertFalse(nodes[boxNode].visited(7, 10));
		
		int testNode = graph.getNodeAt(8, 13);
		neighbours = nodes[testNode].visit(9, 10);
		System.out.println("Neighbours of (8,13): ");
		System.out.println(Arrays.toString(neighbours));*/
		
	}

}
