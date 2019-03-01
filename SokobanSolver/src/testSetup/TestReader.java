package testSetup;

import java.io.File;
import java.util.HashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import setup.Maze;
import setup.MazePosition;
import setup.MazeReader;
import setup.Reader;
import solver.MyMazePosition;

class TestReader {
	MazeReader aReader;
	File file;
	
	@BeforeEach
	void setUp() throws Exception {
		aReader = Reader.getReader();
	}
	

	@Test
	void testGetReader() {
		assert(aReader != null);
		// Test of HashMaps and arrays
		int[] array1 = new int[2];
		array1[0] = 6;
		array1[1] = 8;
		int[] array2 = new int[2];
		array2[0] = 6;
		array2[1] = 8;
		
		if (array1.equals(array2)) {
			System.out.println("array1.equals(array2)");
		}
		else {
			System.out.println("!array1.equals(array2) ");
		}
		MazePosition pos1 = new MyMazePosition(6,8);
		MazePosition pos2 = new MyMazePosition(6,8);
		HashMap<MazePosition, String> hashMapTest = new HashMap<MazePosition, String>();
		hashMapTest.put(pos1, "Pos 1");
		if (hashMapTest.containsKey(pos2)) {
			System.out.println("hashMapTest contains key" + pos2);
		}
		else {
			System.out.println("hashMapTest does not contain key " + pos2);
		}
		System.out.println("hashMapTest.get(pos2): " + hashMapTest.get(pos2));
	
	}

	@Test
	void testReadMaze() {
		file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		Maze maze = aReader.readMaze(file);
		int[] playerCell = new int[2];
		maze.getPlayerLocation(playerCell);
		String outStr = String.format("Player located at (%d,%d)", playerCell[0], playerCell[1]); 
		System.out.println(outStr);
		assert(maze != null);
		int[][] reachable = new int[maze.numRows()][maze.numCols()];
		
		maze.getDistances(8, 8, reachable);
		maze.getDistances(3, 9, reachable);
		
		// File does not exist
		file = new File("/Users/zhipinghe/Desktop/SokobanMaz1.rtf");
		maze = aReader.readMaze(file);
		// File not a Sokoban file
		file = new File("/Users/zhipinghe/Desktop/MissingSokobanHeader.rtf");
		maze = aReader.readMaze(file);
		assert(maze == null);
	}

}
