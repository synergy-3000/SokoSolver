package gui;

import javax.swing.JPanel;

import setup.Maze;

public class MoveCmd implements Cmd {
	
	PlayerMove pmove;
	PlayerMove undoMove;
	Direction undoDirn;
	
	int[] prc;
	
	public MoveCmd(PlayerMove pmove) {
		this.pmove = pmove;
		undoDirn = pmove.dirn.opposite();
		prc = new int[2];
		for (PlayerMove move : PlayerMove.values()) {
			if (move.dirn == undoDirn) {
				undoMove = move;
			}
		}
	}
	@Override
	public void undo(JPanel drawArea, Maze maze, Canvas canvas) {
		undoMove.execute(drawArea, maze, canvas);
	}

	@Override
	public void execute(JPanel drawArea, Maze maze, Canvas canvas) {
		pmove.execute(drawArea, maze, canvas);
		
	}

}
