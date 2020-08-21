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

class TestCollectionsReader {

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testReadCollection() {
		File file = new File("SokobanOriginalLevels.txt");
		Maze maze = Controller.getInstance().getMaze();
		//new CollectionsReader().readCollection(file);
		List<MazeState> mstates = CollectionsReader.getInstance().readCollection(file);
		int i=0;
		for (MazeState ms : mstates) {
			maze.setNewMaze(ms);
			System.out.println("Maze " + i++ + "\n" + ms);
		}
	}

}
