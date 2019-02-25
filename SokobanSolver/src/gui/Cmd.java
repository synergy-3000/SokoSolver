package gui;

import javax.swing.JPanel;

import setup.Maze;

public interface Cmd {
	public void undo(JPanel drawArea, Maze maze, Canvas canvas);
	public void execute(JPanel drawArea, Maze maze, Canvas canvas);
}
