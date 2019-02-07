package testSetup;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.Maze;
import setup.MazeReader;
import setup.Reader;
import solver.MyDeadPositionFinder;

class testMyDeadPositionFinder {
	
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
	void testGetDeadPositions() {
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		//file = new File("/Users/zhipinghe/Desktop/SokobanMaze2.txt");
		Maze maze = aReader.readMaze(file);
		MyDeadPositionFinder mdpf = new MyDeadPositionFinder();
		
		mdpf.getDeadPositions(null);
	}

}
