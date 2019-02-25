package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import setup.GraphCreator;
import setup.Maze;

public class Canvas {
	
	Maze maze;
	GraphicObj player;
	Stone stone;
	int[][] stoneLocs;
	int[] playerRowCol;
	Rectangle clip, square;
	boolean[] canPush;
	
	GraphicObj[][] canvas;  // Static features. Not player and stones.
	int sSize;
	AffineTransform save;
	
	int[][] reachable;
	
	public Canvas(Maze maze, GraphicObj player, Stone stone, GraphicObj[][] sokoSquares, int squareSize) {
		sSize = squareSize;
		canvas = sokoSquares;
		clip = new Rectangle();
		square = new Rectangle(0,0,squareSize,squareSize);
		this.stone = stone;
		this.player = player;
		this.maze = maze;
		playerRowCol = new int[2];
		canPush = new boolean[Direction.values().length];
		reachable = new int[maze.numRows()][maze.numCols()];
	}
	public void draw(Graphics2D g) {
		g.getClipBounds(clip);
		int x,y;
		
		for(int r=0; r<canvas.length; r++) {
			for (int c=0; c<canvas[r].length; c++) {
				x = c * sSize;
				y = r * sSize;
				square.setLocation(x, y);
				
				if (clip.contains(square)) {
					save = g.getTransform();
					g.translate(c*sSize, r*sSize);
					canvas[r][c].draw(g);
					g.setTransform(save);
				}
			}
		}
		// Draw player
		maze.getPlayerLocation(playerRowCol);
		save = g.getTransform();
		g.translate(playerRowCol[1]*sSize, playerRowCol[0]*sSize);
		player.draw(g);
		g.setTransform(save);
		
		// If last action was a push then update all boxes for pushable directions
		// else just a player move so don't need to update boxes
		stoneLocs = maze.getBoxLocations();
		
		for (int i=0; i<stoneLocs.length; i++) {
			x = stoneLocs[i][1] * sSize;
			y = stoneLocs[i][0] * sSize;
			square.setLocation(x,y);
			
			if (clip.contains(square)) {
				save = g.getTransform();
				g.translate(x,y);
				if (maze.isGoalSquare(stoneLocs[i][0], stoneLocs[i][1])) {
					stone.setType(Stone.STONE_ON_GOAL);
				}
				else {
//						GraphCreator.getGraphCreator().getPushableDirections(stoneLocs[i][0], stoneLocs[i][1], canPush, reachable);
					stone.setType(Stone.STONE_ON_EMPTY_SQUARE);
				}
				stone.draw(g);
				g.setTransform(save);
			}
		}
	}
	private void updateAllStones(Graphics2D g) {
		int x,y;
		reachable = maze.getDistances(playerRowCol[0], playerRowCol[1]);
		for (int i=0; i<stoneLocs.length; i++) {
			x = stoneLocs[i][1] * sSize;
			y = stoneLocs[i][0] * sSize;
			square.setLocation(x,y);
			
			save = g.getTransform();
			g.translate(x,y);
			if (maze.isGoalSquare(stoneLocs[i][0], stoneLocs[i][1])) {
				stone.setType(Stone.STONE_ON_GOAL);
			}
			else {
				GraphCreator.getGraphCreator().getPushableDirections(stoneLocs[i][0], stoneLocs[i][1], canPush, reachable);
			}
				stone.setType(Stone.STONE_ON_EMPTY_SQUARE);
			stone.draw(g);
			g.setTransform(save);
		}
		
	}
	public int getMazeSquareSize() {
		return sSize;
	}
	public void xytoRowCol(int[] xy, int[] rowcol) {
		rowcol[0] = xy[0]/sSize;
		rowcol[1] = xy[1]/sSize;
	}
	public void rowColToxy(int[] rowcol, int[] xy) {
		xy[0] = rowcol[0] * sSize;
		xy[1] = rowcol[1] * sSize;
	}
}
