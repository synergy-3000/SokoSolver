package gui;

import java.awt.Rectangle;

import javax.swing.JPanel;

import setup.Maze;

// A Player move
public enum PlayerMove {
	UP(Direction.UP), DOWN(Direction.DOWN), RIGHT(Direction.RIGHT), LEFT(Direction.LEFT);
	
	Direction dirn;
	private boolean enabled;
	private Rectangle update;
	
	int[] prc;
	
	PlayerMove(Direction dirn) {
		this.dirn = dirn;
		enabled = true;
		update = new Rectangle();
		prc = new int[2];
	}
	public void execute(JPanel drawArea, Maze maze, Canvas canvas) {
		int sSize = canvas.getMazeSquareSize();
		maze.getPlayerLocation(prc);
		prc[0] *= sSize;
		prc[1] *= sSize;
		maze.movePlayer(dirn);
		setUpdateArea(update, sSize, prc);
		drawArea.repaint(update);
		Controller.getInstance().setLastAction(Controller.MOVE_ACTION);
	}
	public void setUpdateArea(Rectangle area, int delta, int[] from) {
		int x,y,width,height;
		
		switch(dirn) {
		case UP:
			y = from[0] - delta;
			x = from[1];
			width = delta;
			height = 2 * delta;
			break;
		case DOWN:
			y = from[0];
			x = from[1];
			width = delta;
			height = 2 * delta;
			break;
		case LEFT:
			y = from[0];
			x = from[1] - delta;
			width = 2 * delta;
			height = delta;
			break;
		case RIGHT:
			y = from[0];
			x = from[1];
			width = 2 * delta;
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
	@Override
	public String toString() {
		String outStr;
		
		switch(this) {
		case UP:
			outStr = "UP";
			break;
		case DOWN:
			outStr = "DOWN";
			break;
		case LEFT:
			outStr = "LEFT";
			break;
		case RIGHT:
			outStr = "RIGHT";
			break;
		default:
			outStr = "";
		}
		return outStr;
	}
	
}
