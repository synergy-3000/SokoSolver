package testSetup;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.GraphCreator;
import setup.Maze;
import setup.MazeReader;
import setup.Reader;

class GraphCreatorTest {
	MazeReader aReader;
	File file;
	
	@BeforeEach
	void setUp() throws Exception {
		aReader = Reader.getReader();
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
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze2.txt");
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze3.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		gc.createPushGraph(maze);
		
		
		
	}

}
