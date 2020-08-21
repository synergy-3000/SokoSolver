package testSetup;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.CollectionsReader;
import setup.GraphCreator;
import setup.MazeState;
import setup.SokoMaze;

class GraphCreatorTest {
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
	void testGetGraphCreator() {
		GraphCreator gc = GraphCreator.getGraphCreator();
		assert(gc != null);
	}

	@Test
	void testCreatePushGraph() {
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze3.txt");
		mazeStates = CollectionsReader.getInstance().readCollection(file);
		maze = SokoMaze.getInstance(mazeStates.get(0));
		GraphCreator gc = GraphCreator.getGraphCreator();
		gc.createPushGraph(maze);
	}
}
