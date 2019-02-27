package gui;

import java.awt.Rectangle;

import javax.swing.JPanel;

import setup.Maze;

public enum BoxPush implements Cmd {
	PUSH_UP(Direction.UP), PUSH_DOWN(Direction.DOWN), PUSH_RIGHT(Direction.RIGHT), PUSH_LEFT(Direction.LEFT);
	
	Direction dirn;
	private boolean enabled;
	private Rectangle update;
	
	int[] prc;
	int[] to;
	int[] box;
	
	private int[][] stoneLocs;
	
	BoxPush(Direction dirn) {
		this.dirn = dirn;
		enabled = true;
		update = new Rectangle();
		prc = new int[2];
		to = new int[2];
		box = new int[2];
	}
	public void execute(JPanel drawArea, Maze maze, Canvas canvas) {
		int sSize = canvas.getMazeSquareSize();
		maze.getPlayerLocation(prc);
		dirn.getToPosition(prc, to);
		
		// box 
		maze.moveBox(to[0], to[1], dirn);
		
		// player
		maze.movePlayer(dirn);
		
		to[0] *= sSize;
		to[1] *= sSize;
		setUpdateArea(update, sSize, to);
		drawArea.repaint(update);
		
		// update all stones to show available pushes
		stoneLocs = maze.getBoxLocations();
		for (int[] coord : stoneLocs) {
			drawArea.repaint(coord[1] * sSize, coord[0] * sSize, sSize, sSize);
		}
	}
	@Override
	public void undo(JPanel drawArea, Maze maze, Canvas canvas) {
		
		int sSize = canvas.getMazeSquareSize();
		maze.getPlayerLocation(prc);
		
		dirn.getToPosition(prc, box);
		Direction undoDirn = dirn.opposite();
		
		// player
		maze.movePlayer(undoDirn);
		
		// box 
		maze.moveBox(box[0], box[1], undoDirn);
		
		prc[0] *= sSize;
		prc[1] *= sSize;
		setUpdateArea(update, sSize, prc);
		drawArea.repaint(update);
		
		// update all stones to show available pushes
		stoneLocs = maze.getBoxLocations();
		for (int[] coord : stoneLocs) {
			drawArea.repaint(coord[1] * sSize, coord[0] * sSize, sSize, sSize);
		}
	}
	/* Update 3 squares. 1) player's old position 2) new player position 3) new box position
	   int[]from contains the old box position
	*/
	public void setUpdateArea(Rectangle area, int delta, int[] from) {
		int x,y,width,height;
		
		switch(dirn) {
		case UP:
		case DOWN:
			y = from[0] - delta;
			x = from[1];
			width = delta;
			height = 3 * delta;
			break;
		case LEFT:
		case RIGHT:
			y = from[0];
			x = from[1] - delta;
			width = 3 * delta;
			height = delta;
			break;
		default:
			x = from[1];
			y = from[0];
			width = height = delta;
		}
		area.setBounds(x, y, width, height);
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean val) {
		enabled = val;
	}
}
