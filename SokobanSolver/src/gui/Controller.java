package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.Reader;
import solver.SokoDeadPositionFinder;

//TODO detect when all stones are on goal squares and display a "Well Done!!" message
//move execute() code from BoxPush and PlayerMove to PushCmd and MoveCmd: completed
public class Controller implements KeyListener {
	MyPanel panel;
	Maze maze;
	Canvas canvas;
	Person person;
	
	GraphicObj wall;
	GraphicObj goal;
	GraphicObj dead;
	GraphicObj empty;
	Stone box;
	
	boolean isDead[][];
	boolean isGoal[][];
	
	private boolean undoEnabled = false;
	private boolean redoEnabled = false;
	
	int[] to, from;
	int[] playerLoc;
	
	int playerPushes = 0;
	
	GraphCreator gc;
	
	GraphicObj[][] sokoSquares; // Used for static features of maze - goal squares, wall, empty squares, dead positions
	                            // Not for player or boxes
	static Controller instance;
	
	ArrayList<Cmd> history;
	int current = 0; // (current - 1) to undo, (current) to redo
	private boolean showDeadPos = true;
	private SokoMenu sokoMenu;
	
	int[][] initialStoneLocs;
	int[] initialPlayerLoc;
	
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	private void saveInitialState() {
		int[][] stonePosns;
		stonePosns = maze.getBoxLocations();
		// Make a copy because a reference to the array is returned by maze.getBoxLocations()
		// and this will change as boxes are moved!
		initialStoneLocs = new int[stonePosns.length][2];
		for (int i=0; i<stonePosns.length; i++) {
			for (int j=0; j<2; j++) {
				initialStoneLocs[i][j] = stonePosns[i][j];
			}
		}
		// player
		initialPlayerLoc = new int[2];
		maze.getPlayerLocation(initialPlayerLoc);
	}
	private void restoreInitialState() {
		maze.setPlayerLocation(initialPlayerLoc);
		maze.setBoxLocations(initialStoneLocs);
		history.clear();
		current = 0;
		undoEnabled = false;
		redoEnabled = false;
		sokoMenu.enableRedo(false);
		sokoMenu.enableUndo(false);
		playerPushes = 0;
	}
	private Controller() {
		
		history = new ArrayList<Cmd>();
		
		File file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		maze = Reader.getReader().readMaze(file);
		gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		SokoDeadPositionFinder finder = SokoDeadPositionFinder.getInstance();
		
		to = new int[2];
		from = new int[2];
		playerLoc = new int[2];
		
		// Dead positions
		int[] deadPosns = finder.getDeadPositions(graph, maze);
		isDead = new boolean[maze.numRows()][maze.numCols()];
		for (int i=0; i<isDead.length; i++) {
			Arrays.fill(isDead[i], false);
		}
		for (int i=0; i<deadPosns.length; i++) {
			isDead[gc.getNodeRow(deadPosns[i])][gc.getNodeCol(deadPosns[i])] = true;
		}
		// Goal Squares
		boolean goalIds[] = maze.getIsGoalNode();
		isGoal = new boolean[maze.numRows()][maze.numCols()];
		for (int i=0; i<isGoal.length; i++) {
			Arrays.fill(isGoal[i], false);
		}
		for (int i=0; i<goalIds.length; i++) {
			isGoal[gc.getNodeRow(i)][gc.getNodeCol(i)] = goalIds[i];
		}
		// The finding dead positions algorithm clears all the boxes from the maze so
		// need to put them back!
		int[][] boxLocs = maze.getBoxLocations();
		for (int i=0; i<boxLocs.length; i++) {
			maze.setBoxAt(boxLocs[i][0], boxLocs[i][1]);
		}
		
		// Calc square size
		int nRows = maze.numRows();
        int nCols = maze.numCols();
        
        int drawH = MyPanel.MAX_HEIGHT/nRows;
        int drawW = MyPanel.MAX_WIDTH/nCols;
        int cSize = Math.min(drawW, drawH);
        cSize = Math.min(cSize, MyPanel.PREF_DRAWINGHEIGHT);
        
        System.out.println("nRows: " + nRows + " nCols: " + nCols);
        person = new Person();
        System.out.println("person.getBounds(): " + person.getBounds());
        
        wall = new Wall();
        goal = new GoalSquare();
        dead = new DeadPosition();
        empty = new EmptySquare();
        box = new Stone();
        
        // Create canvas
        createSokoSquares(cSize);
        
        //create box GraphicObj : done
        //add controller as keylistener : done
        //get player pushes and player moves : done
        canvas = new Canvas(maze, person, box, sokoSquares, cSize);
        
        panel = new MyPanel(person, cSize, cSize, canvas);
        panel.setPreferredSize(new Dimension(cSize * nCols, cSize * nRows));
        
        // Menu
        sokoMenu = new SokoMenu();
        
        saveInitialState();
        
        //System.out.println("panel drawing width/height: " + cSize);
		
	}
	public PlayerMove[] getPlayerMoves() {
		maze.getPlayerLocation(playerLoc);
		//System.out.println("getPlayerMoves(): ");
		//System.out.println("Player: (" + playerLoc[0]+","+playerLoc[1]+")");
		
		for (PlayerMove move : PlayerMove.values()) {
			//System.out.println("dirn: " + move.dirn);
			move.dirn.getToPosition(playerLoc, to);
			//System.out.println("to[]: (" + to[0] + "," + to[1] + ")");
			//System.out.println("maze.isEmpty(to[0],to[1]): " + maze.isEmpty(to[0], to[1]));
			move.setEnabled(maze.isEmpty(to[0], to[1]));
		}
		return PlayerMove.values();
	}
	public BoxPush[] getPlayerPushes() {
		maze.getPlayerLocation(playerLoc);
		
		for(BoxPush push : BoxPush.values()) {
			push.setEnabled(false);
			push.dirn.getToPosition(playerLoc, from);
			if (maze.isBox(from[0], from[1])) {
				push.dirn.getToPosition(from, to);
				push.setEnabled(maze.isEmpty(to[0], to[1]));
			}
		}
		return BoxPush.values();
	}
	public MyPanel getPanel() {
		return panel;
	}
	public Canvas getCanvas() {
		return canvas;
	}
	private void createSokoSquares(int sqrSize) {
		sokoSquares = new GraphicObj[maze.numRows()][maze.numCols()];
		
		for (int r=0; r<sokoSquares.length; r++) {
			for (int c=0; c<sokoSquares[r].length; c++) {
				if (maze.isWall(r, c)) {
					sokoSquares[r][c] = wall; // need only one instance or multiple instances ?
				}
				else if (isDead[r][c]) {
					sokoSquares[r][c] = dead;
				}
				else if (isGoal[r][c]) {
					sokoSquares[r][c] = goal;
				}
				else {
					sokoSquares[r][c] = empty;
				}
				sokoSquares[r][c].setDrawingArea(sqrSize, sqrSize);
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
		System.out.println("KeyTyped: " + e.getKeyChar());
		Direction dirn = null;
		boolean bMove = true;
		
		
		switch(e.getKeyChar()) {
		case 'i': // UP
			dirn = Direction.UP;
			break;
		case 'j': // LEFT
			dirn = Direction.LEFT;
			break;
		case 'k': // DOWN
			dirn = Direction.DOWN;
			break;
		case 'l': // RIGHT
			dirn = Direction.RIGHT;
			break;
		 default:
			bMove = false; 
		}
		// A 'move' key was pressed?
		if (bMove) {
			person.setDirection(dirn);
			boolean moved = false;
			// Try moves first and then pushes
			for (PlayerMove move : getPlayerMoves()) {
				if (dirn == move.dirn) {
					if (move.isEnabled()) {
						move.execute(panel, maze, canvas);
						addToHistory(move);
						moved = true;
					}
				}
			}
			// Try a push
			if (!moved) {
				System.out.println("Trying a push");
				for (BoxPush push : getPlayerPushes()) {
					if (dirn == push.dirn) {
						if (push.isEnabled()) {
							push.execute(panel, maze, canvas);
							addToHistory(push);
							moved = true;
						}
					}
				}
			}
			// Sound an error?
			if (!moved) {
				
			}
		    /*// Ignore if person would be outside the window
			if (getBounds().contains(personLoc.x + dx,personLoc.y + dy,personLoc.width,personLoc.height)) {
				person.setDirection(dirn);
				person.translate(dv);
				repaint(personLoc);
				personLoc.translate(dx, dy);
				repaint(personLoc);
			}*/
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}  
	private void addToHistory(Cmd cmd) {
		if (current != history.size()) {
			history.subList(current, history.size()).clear();
		}
		history.add(cmd);
		current += 1;
		updateRedoUndo();
		
		System.out.println("addToHistory(): current = " + current + " history.size() = " + history.size());
		
	}
	public void undo() {
		if ( (current - 1) >= 0 ) {
			current -= 1;
			history.get(current).undo(panel, maze, canvas);
			updateRedoUndo();
		}
	}
	public void redo() {
		if ( current < history.size()) {
			System.out.println("redo()");
			history.get(current).execute(panel, maze, canvas);
			current += 1;
			updateRedoUndo();
		}
	}
	private boolean canUndo() {
		return (current > 0);
	}
	private boolean canRedo() {
		return (current < history.size());
	}
	private void updateRedoUndo() {
		if (canUndo() != undoEnabled) {
			undoEnabled = !undoEnabled;
			sokoMenu.enableUndo(undoEnabled);
		}
		if (canRedo() != redoEnabled) {
			redoEnabled = !redoEnabled;
			sokoMenu.enableRedo(redoEnabled);
		}
	}
	//TODO implement show dead positions in canavs.java or controller.java
	public void showDead(boolean show) {
		showDeadPos = show;
	}
	/* Start again from the beginning */
	//implement reset() : completed
	public void reset() {
		restoreInitialState();
		// Repaint everything
		panel.repaint(panel.getBounds());
	}

	public SokoMenu getMenu() {
		return sokoMenu;
	}
}
class EmptySquare extends AbstractGraphicObj {
	
	Rectangle bounds = new Rectangle(0,0,50,50);
	
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	protected void render(Graphics2D g) {
		;
	}
	
}
class DeadPosition extends AbstractGraphicObj {
	
	Rectangle bounds = new Rectangle(0,0,50,50);
	
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	protected void render(Graphics2D g) {
		Color save = g.getColor();
		g.setColor(Color.MAGENTA);
		g.fill3DRect(10, 10, 30, 30, true);
		g.setColor(save);
	}
	
}
class GoalSquare extends AbstractGraphicObj {
	
	Rectangle bounds = new Rectangle(0,0,50,50);
	BasicStroke stroke;
	
	public GoalSquare() {
		super();
		stroke = new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER);
	}
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	protected void render(Graphics2D g) {
		Color save = g.getColor();
		Stroke saveStroke = g.getStroke();
		
		g.setColor(Color.GREEN);
		g.setStroke(stroke);
		g.drawLine(10, 10, 40, 40);
		g.drawLine(10, 40, 40, 10);
		
		g.setColor(save);
		g.setStroke(saveStroke);
	}
	
}
class Wall extends AbstractGraphicObj {
	final int[] px = { 0, 50, 50, 0, 7, 7, 43, 43 };
	final int[] py = { 0, 0,  50,50,43, 7, 7,  43 };
	
	int[] topEdgex = { px[0], px[1], px[6], px[5] };
	int[] topEdgey = { py[0], py[1], py[6], py[5] };
	Color colTopE = Color.WHITE;
	
	int[] rightEdgex = { px[1], px[2], px[7], px[6] };
	int[] rightEdgey = { py[1], py[2], py[7], py[6] };
	Color colRightE = Color.GRAY;
	
	int[] botEdgex = { px[3], px[4], px[7], px[2] };
	int[] botEdgey = { py[3], py[4], py[7], py[2] };
	Color colBotE = Color.DARK_GRAY;
	
	int[] leftEdgex = { px[0], px[5], px[4], px[3] };
	int[] leftEdgey = { py[0], py[5], py[4], py[3] };
	Color colLeftE = Color.LIGHT_GRAY;
	
	int[] centreSquarex = { px[5], px[6], px[7], px[4] };
	int[] centreSquarey = { py[5], py[6], py[7], py[4] };
	Color colCentreS = Color.GRAY;
	
	// Shapes
	Color[] fillColors = { colTopE, colRightE, colBotE, colLeftE, colCentreS };
	int[][] polyX = { topEdgex, rightEdgex, botEdgex, leftEdgex, centreSquarex };
	int[][] polyY = { topEdgey, rightEdgey, botEdgey, leftEdgey, centreSquarey };
	
	Rectangle bounds = new Rectangle(0,0,50,50);
	
	Color darkerGray = new Color(100,100,100);
	
	BufferedImage bufferedI;
    boolean useBuffered = false;
    
	public Wall() {
		super();
		bufferedI = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bufferedI.createGraphics();
        //g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        drawWall(g2);
        g2.dispose();
	}
	
	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	protected void render(Graphics2D g) {
		if (useBuffered) {
			g.drawImage(bufferedI,0,0,null);
		}
		else {
			drawWall(g);
		}
	}
	protected void drawWall(Graphics2D g) {
		Color save = g.getColor();
		
		for (int i=0; i<fillColors.length; i++) {
			//g.setColor(Color.BLACK);
			//g.drawPolygon(polyX[i], polyY[i], polyX[i].length);
			g.setColor(fillColors[i]);
			g.fillPolygon(polyX[i], polyY[i], polyX[i].length);
		}
		g.setColor(darkerGray);
		g.drawLine(px[7], py[7], px[6], py[6]);
		
		g.setColor(save);
	}
}
