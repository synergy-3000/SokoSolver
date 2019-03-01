package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import setup.GraphCreator;
import setup.Maze;
//Show pushable directions on each stone using green triangles : done
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
	
	public Canvas(Maze maze, GraphicObj player, GraphicObj[][] sokoSquares, int squareSize) {
		sSize = squareSize;
		canvas = sokoSquares;
		clip = new Rectangle();
		square = new Rectangle(0,0,squareSize,squareSize);
		this.player = player;
		this.maze = maze;
		playerRowCol = new int[2];
		canPush = new boolean[4];
	} 
	public void draw(Graphics2D g) {
		g.getClipBounds(clip);
		System.out.println("Clip bounds: " + clip);
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
		
		// Draw stones
		stoneLocs = maze.getBoxLocations();
		
		for (int i=0; i<stoneLocs.length; i++) {
			x = stoneLocs[i][1] * sSize;
			y = stoneLocs[i][0] * sSize;
			square.setLocation(x,y);
			
			if (clip.contains(square)) {
				save = g.getTransform();
				g.translate(x,y);
				Controller.getInstance().getStone(stoneLocs[i][0], stoneLocs[i][1]).draw(g);
				g.setTransform(save);
			}
		}
	}
	public int getMazeSquareSize() {
		return sSize;
	}
	/*public void xytoRowCol(int[] xy, int[] rowcol) {
		rowcol[0] = xy[0]/sSize;
		rowcol[1] = xy[1]/sSize;
	}
	public void rowColToxy(int[] rowcol, int[] xy) {
		xy[0] = rowcol[0] * sSize;
		xy[1] = rowcol[1] * sSize;
	}*/
}
