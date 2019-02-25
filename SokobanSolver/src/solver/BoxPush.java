package solver;

import gui.Direction;
import setup.Maze;
import setup.MazePosition;

public interface BoxPush {
	public MazePosition getOldBoxPosition();
	public Direction getDirection();
	public void doPush(Maze maze); 
	public MazePosition getNewBoxPosition();
}
