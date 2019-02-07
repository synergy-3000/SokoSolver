package solver;

import setup.Maze;
import setup.MazePosition;
import solver.SokoBoxPush.Direction;

public interface BoxPush {
	public MazePosition getOldBoxPosition();
	public Direction getDirection();
	public void doPush(Maze maze); 
	public MazePosition getNewBoxPosition();
}
