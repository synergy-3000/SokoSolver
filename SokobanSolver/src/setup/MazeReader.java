package setup;

import java.io.File;

public interface MazeReader {
	public Maze readMaze(File file); 
	public Maze getMaze();

}
