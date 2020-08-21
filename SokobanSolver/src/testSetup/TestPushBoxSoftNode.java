package testSetup;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.CollectionsReader;
import setup.Graph;
import setup.GraphCreator;
import setup.MazeState;
import setup.Node;
import setup.SokoMaze;

class TestPushBoxSoftNode {
	File file;
	private List<MazeState> mazeStates;
	private SokoMaze maze;
	
	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}
	@Test
	void test() {
	}
	@Test
	void testVisit() {
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze3.txt");
		mazeStates = CollectionsReader.getInstance().readCollection(file);
		maze = SokoMaze.getInstance(mazeStates.get(0));
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze2.txt");
		
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		Node[] nodes = graph.getNodes();
		int boxNode = graph.getNodeAt(8, 10);
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
		neighbours = nodes[testNode].visit(8, 12);
		System.out.println("Neighbours of (8,13): ");
		System.out.println(Arrays.toString(neighbours));
	   
	}
	void testVisited() {
		
	}
}
