package gui;

import javax.swing.JPanel;

// A box push
public enum SokoMove {
	UP(0,-1), DOWN(0,1), RIGHT(1,0), LEFT(-1,0);
	
	private final int dx,dy;
	private boolean enabled;
	
	SokoMove(int dx, int dy) {
		this.dx = dx;
		this.dy = dy;
	}
	public void execute(Person player, JPanel drawArea) {
		// TODO implement
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean val) {
		enabled = val;
	}
}
