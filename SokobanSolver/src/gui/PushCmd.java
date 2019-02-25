package gui;

import java.awt.Rectangle;

import javax.swing.JPanel;

import setup.Maze;

public class PushCmd implements Cmd {
	
	BoxPush boxPush;
	
	private Rectangle update;
	
	int[] prc;
	int[] box;
	
	public PushCmd(BoxPush boxPush) {
		this.boxPush = boxPush;
		update = new Rectangle();
		prc = new int[2];
		box = new int[2];
	}
	@Override
	public void undo(JPanel drawArea, Maze maze, Canvas canvas) {
		
		int sSize = canvas.getMazeSquareSize();
		maze.getPlayerLocation(prc);
		
		boxPush.dirn.getToPosition(prc, box);
		Direction undoDirn = boxPush.dirn.opposite();
		
		// player
		maze.movePlayer(undoDirn);
		
		// box 
		maze.moveBox(box[0], box[1], undoDirn);
		
		prc[0] *= sSize;
		prc[1] *= sSize;
		boxPush.setUpdateArea(update, sSize, prc);
		drawArea.repaint(update);
		Controller.getInstance().setLastAction(Controller.PUSH_ACTION);
	}

	@Override
	public void execute(JPanel drawArea, Maze maze, Canvas canvas) {
		boxPush.execute(drawArea, maze, canvas);
	}

}
