package testSetup;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.Reader;
import solver.SokoDeadPositionFinder;

// Investigate connecting Eclipse to GitHub - Ans: Use EGit which is automatically installed with Eclipse Photon 
// then right click on project node in package explorer choose 'Team' from context menu
// How to loop over an enumeration e.g SokoBoxPush.Direction Ans: use Direction.values()
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
		finder = SokoDeadPositionFinder.getInstance();
		
		int[] deadPosns = finder.getDeadPositions(graph, maze);
		
		// Debug: Print out dead positions
		String s = "Dead Positions: [";
		for (int pos : deadPosns) {
			s += pos + " ";
		}
		s += "]";
		System.out.println(s);
	}
	@Test
	void testGetPathToGoal() {
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze2.txt");
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		
		graph.clearVisits();
		
		finder = SokoDeadPositionFinder.getInstance();

		boolean[] goals = maze.getIsGoalNode();

		int[] path = finder.getPathToGoal(graph, 17, goals, 18);
		
		if (path == null) System.out.println("IS dead position");
		else System.out.println("NOT dead position");
		
		System.out.println("numRows(): " + maze.numRows() + " numCols(): " + maze.numCols());
	}
}
