package gui;

import java.awt.Rectangle;

import javax.swing.JPanel;

import setup.Maze;

public enum BoxPush {
	PUSH_UP(Direction.UP), PUSH_DOWN(Direction.DOWN), PUSH_RIGHT(Direction.RIGHT), PUSH_LEFT(Direction.LEFT);
	
	Direction dirn;
	private boolean enabled;
	private Rectangle update;
	
	int[] prc;
	int[] to;
	
	BoxPush(Direction dirn) {
		this.dirn = dirn;
		enabled = true;
		update = new Rectangle();
		prc = new int[2];
		to = new int[2];
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
		//TODO repaint all stones to show available pushes for each stone
		Controller.getInstance().setLastAction(Controller.PUSH_ACTION);
	}
	/* Update 3 squares. 1) player's old position 2) new player position 3) new box position
	   int[] from contains the old box position
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
