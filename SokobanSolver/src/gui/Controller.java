package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import setup.CollectionsReader;
import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.MazeState;
import setup.SokoMaze;
import solver.DeadPositionFinder;
import solver.DeadPositionFinder3;

//Bug: Starting again shows available pushes for where the player was last located and NOT 
// for the starting position of the player : done
//TODO Screen flashes when changing mazes or 'starting again'
//TODO Create an icon for program 32x32
//TODO Improve player graphic for UP & DOWN pushes
//TODO Add name of maze collection and Level in stats label 
//TODO Paint squares outside maze in dark blue?
//Add "Show Grid" checkbox menu item under "Preferences" menu : done
//TODO update ReadMe file in my repository on GitHUb
//Get a local version of java api doc : done
//TODO Investigate lambda expressions
//Add "Show Available Pushes" check box item under "Preferences" Menu : done
//TODO Make a distribution, jar file ?, of java project 
//Show total number of pushes : done
//TODO Add 'Next' menu item
//TODO Change options after solving a maze to Previous, Start Again, Quit
//TODO Rename Git repository from SokoSolver to ZSokoban or Sokoban
//   ... create a big mazeChars[][] and record the start and finish row in mazeChars[][] for each maze.
//Also record the max for states and spaces.
//detect when all stones are on goal squares and display a "Well Done!!" message : completed
//move execute() code from BoxPush and PlayerMove to PushCmd and MoveCmd: completed
public class Controller implements KeyListener {
	MyPanel panel;
	JFrame frame;
	Maze maze;
	Canvas canvas;
	Person person;
	JLabel lblStats;
	JLabel lblTest;
	
	GraphicObj wall;
	GraphicObj goal;
	GraphicObj dead;
	GraphicObj empty;
	private Stone stoneOnGoal;
	private Stone stonePushable;
	private Stone stoneOnEmpty;
	
	boolean isDead[][];
	boolean isGoal[][];
	
	private boolean undoEnabled = false;
	private boolean redoEnabled = false;
	
	int[] to, from;
	int[] playerLoc;
	
	int nPushes = 0;
	
	GraphCreator gc;
	
	GraphicObj[][] sokoSquares; // Used for static features of maze - goal squares, wall, empty squares, dead positions
	                            // Not for player or boxes
	static Controller instance;
	
	ArrayList<Cmd> history;
	int current = 0; // (current - 1) to undo, (current) to redo
	private SokoMenu sokoMenu;
	
	int[][] initialStoneLocs;
	int[] initialPlayerLoc;
	
	int[][] reachable;
	private boolean[] canPush;
	private List<MazeState> mazeStates;
	private int currMaze;
	
	private DeadPositionFinder finder;
	private int nMoves = 0;
	private int[] topLeft;
	private boolean showPushes = true;
	private boolean showGrid = true;
	private Rectangle pnlBounds;
	
	public static Controller getInstance() {
		if (instance == null) {
			instance = new Controller();
		}
		return instance;
	}
	/*
	 * A lot of the code in the constructor needs to be placed
	   in a method setNewMaze(MazeState ms) called when a new MazeState is set : done
	*/
	private Controller() {
		
		history = new ArrayList<Cmd>();
		to = new int[2];
		from = new int[2];
		playerLoc = new int[2];
		canPush = new boolean[4];
		topLeft = new int[2];
		
        person = new Person();
        wall = new Wall();
        goal = new GoalSquare();
        dead = new DeadPosition();
        empty = new EmptySquare();
        sokoSquares = new GraphicObj[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
        
        stonePushable = new Stone(Stone.STONE_PUSHABLE);
        stoneOnEmpty = new Stone(Stone.STONE_ON_EMPTY_SQUARE);
        stoneOnGoal = new Stone(Stone.STONE_ON_GOAL);
		isDead = new boolean[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
		isGoal = new boolean[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
		reachable = new int[SokoMaze.MAX_ROWS][SokoMaze.MAX_COLS];
		// Frame 
        frame = new JFrame("ZSokoban");
		
		//File file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		//File file = new File("/Users/zhipinghe/Desktop/SokobanMaze3.txt");
        //File file = new File("ThreeSokoMazes.txt");
		mazeStates = CollectionsReader.getInstance().getDefault();
		maze = SokoMaze.getInstance(mazeStates.get(0));
		currMaze = 0;
		gc = GraphCreator.getGraphCreator();
		//finder = SokoDeadPositionFinder.getInstance();
		//finder = new DeadPositionFinder2();
		finder = new DeadPositionFinder3();
		
		canvas = new Canvas(maze, person, sokoSquares, 50);
        
        panel = new MyPanel(person, 50, 50, canvas);
        
        // Menu
        sokoMenu = new SokoMenu();
        
        Container container = frame.getContentPane();
        container.setLayout(new BoxLayout(container,
                BoxLayout.PAGE_AXIS));
		container.add(panel);
		
		// Add bottom labels to panel
		JPanel aPanel = new JPanel();
		lblTest = new JLabel("This is a test");
		lblStats = new JLabel();
		//System.out.println("JLabel font size: " + lblStats.getFont().getSize());
		lblStats.setFont(lblStats.getFont().deriveFont(Font.BOLD));
		aPanel.add(lblStats);
		aPanel.add(lblTest);
		container.add(aPanel);
		//container.add(lblStats);
		
		//Align the left edges of the components.
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		//lblStats.setAlignmentX(Component.LEFT_ALIGNMENT); //redundant
		aPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		initNewMaze(maze);
        frame.setJMenuBar(sokoMenu.createMenuBar());
        frame.addKeyListener(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        frame.pack();
        frame.setLocationRelativeTo(null);
        
        updateLabel();
        //frame.setVisible(true);
        //lblStats.setHorizontalAlignment(SwingConstants.TRAILING);
		//lblStats.setHorizontalTextPosition(SwingConstants.TRAILING);
		
	}
	private void updateLabel() {
		System.out.println("currMaze: " + currMaze);
		lblStats.setText("Pushes " + nPushes + " Moves " + nMoves + " " + mazeStates.get(currMaze).title);
		//lblStats.setHorizontalAlignment(SwingConstants.LEADING);
		//lblTest.setHorizontalAlignment(SwingConstants.TRAILING);
	}
	private void initNewMaze(Maze newMaze) {
		
		maze = newMaze;
		
		// Player reachable squares
        updateReachable();
        
		gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);
		
		// Dead positions
		int[] deadPosns = finder.getDeadPositions(graph, maze);
		for (int i=0; i<isDead.length; i++) {
			Arrays.fill(isDead[i], false);
		}
		for (int i=0; i<deadPosns.length; i++) {
			isDead[gc.getNodeRow(deadPosns[i])][gc.getNodeCol(deadPosns[i])] = true;
		}
		// Goal Squares
		// TODO goalIds[] is a misleading name. Change to isGoalByIdx[]
		boolean goalIds[] = maze.getIsGoalNode();
		
		for (int i=0; i<isGoal.length; i++) {
			Arrays.fill(isGoal[i], false);
		}
		int nEmpty = gc.getNodes().length;  // A node is just an empty square in the maze. Includes goal, box and player squares.
		for (int i=0; i<nEmpty; i++) {
			isGoal[gc.getNodeRow(i)][gc.getNodeCol(i)] = goalIds[i];
		}
		// The finding dead positions algorithm clears all the boxes from the maze so
		// need to put them back! 
		//restore boxes in the finding dead positions algorithm : done
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
        stonePushable.setDrawingArea(cSize, cSize);
        stoneOnEmpty.setDrawingArea(cSize, cSize);
        stoneOnGoal.setDrawingArea(cSize, cSize);
        
        //System.out.println("nRows: " + nRows + " nCols: " + nCols);
        //System.out.println("person.getBounds(): " + person.getBounds());
        
        // Create canvas
        createSokoSquares(cSize);
        panel.setDrawingWidthHeight(cSize, cSize);
        //create box GraphicObj : done
        //add controller as keylistener : done
        //get player pushes and player moves : done
        canvas.setNewMaze(newMaze, cSize);
        pnlBounds = new Rectangle(50,50,cSize * nCols, cSize * nRows);
        panel.setPreferredSize(new Dimension(cSize * nCols, cSize * nRows));
        //frame.setBounds(pnlBounds);
        System.out.printf("panel.setPreferredSize() cSize:%d nCols:%d nRows:%d\n",cSize, nCols, nRows);
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
		System.out.println("createSokoSquares() sqrSize= " + sqrSize);
		int nRows = maze.numRows(), nCols = maze.numCols();
		for (int r=0; r<nRows; r++) {
			for (int c=0; c<nCols; c++) {
				if (maze.isWall(r, c)) {
					sokoSquares[r][c] = wall; // need only one instance
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
							if (maze.allStonesOnGoals()) {
								solved();
							}
							// Update the player reachable area
							updateReachable();
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
	private boolean hasNextMaze() {
		return (currMaze < (mazeStates.size()-1));
	}
	private void setNewMaze(MazeState newMs) {
		updateLabel();
		history.clear();
		current = 0;
		undoEnabled = false;
		redoEnabled = false;
		sokoMenu.enableRedo(false);
		sokoMenu.enableUndo(false);
		incDecPushes(-nPushes);
		incDecMoves(-nMoves);
		maze.setNewMaze(newMs);
		
		initNewMaze(maze);
		
		//Dimension size = panel.getPreferredSize();
		//System.out.printf("panel preferred size:%s", size.toString());
		//panel.setSize(size);
		//frame.setB
		//panel.invalidate();
		//frame.validate();
		//frame.setSize(size.width, size.height);
		//System.out.printf("nextMaze() is gui thread = %b\n",SwingUtilities.isEventDispatchThread());
		
		
		
		//frame.setVisible(false);
		frame.pack();
		//frame.invalidate();
		//frame.setLocation(10, 10);
		
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	
            	//frame.setVisible(true);
            	frame.setLocationRelativeTo(null);
            }
        });
		//frame.setVisible(true);
	}
	private void nextMaze() {
		currMaze = (currMaze + 1) % mazeStates.size();
		setNewMaze(mazeStates.get(currMaze));
	}
	private void updateReachable() {
		maze.getPlayerLocation(playerLoc);
		maze.getDistances(playerLoc[0], playerLoc[1], reachable, topLeft);
	}

	private void solved() {
		//frame.setVisible(false);
		//Custom button text
		Object[] options = {"Quit",
		                    "Next",
		                    "Start Again"};  // These are displayed on the dialog in reverse order.
		int n = JOptionPane.showOptionDialog(frame,
				"Well Done!!",
			    "Solved",
		    JOptionPane.YES_NO_CANCEL_OPTION,
		    JOptionPane.QUESTION_MESSAGE,
		    null,
		    options,
		    options[2]);
		switch(n) {
		case JOptionPane.YES_OPTION:
			frame.dispose();
			break;
		case JOptionPane.NO_OPTION:
			//Check if its the last maze so 'Next' should be disabled : Now 'Next' cycles to first maze after last maze.
			//JOptionPane.showMessageDialog(frame, "Not implemented yet.");
			
			nextMaze();
			break;
		case JOptionPane.CANCEL_OPTION:
			reset();
			break;
		
		}

	}
	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
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
	public void incDecPushes(int incdec) {
		nPushes += incdec;
		updateLabel();
	}
	public void incDecMoves(int incdec) {
		nMoves += incdec;
		updateLabel();
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
	//implement show dead positions in canavs.java or controller.java : completed
	public void showDead(boolean show) {
		for (int r=0; r<sokoSquares.length; r++) {
			for (int c=0; c<sokoSquares[r].length; c++) {
				if (isDead[r][c]) {
					if (show) {
						sokoSquares[r][c] = dead;
					}
					else {
						sokoSquares[r][c] = empty;
					}
				}
			}
		}
		panel.repaint(panel.getBounds());
	}
	public void showPushes(boolean show) {
		showPushes = show;
		panel.repaint(panel.getBounds());
	}
	/* Start again from the beginning */
	//implement reset() : completed
	public void reset() {
		setNewMaze(mazeStates.get(currMaze));
	}

	public SokoMenu getMenu() {
		return sokoMenu;
	}
	public JFrame getFrame() {
		return frame;
	}
	public int[][] getReachable() {
		return reachable;
	}
	public Maze getMaze() {
		return maze;
	}
	public GraphicObj getStone(int row, int col) {
		Stone stone;
		if (maze.isGoalSquare(row, col)) {
			stone = stoneOnGoal;
		}
		else {
			if (showPushes  && (GraphCreator.getGraphCreator().getPushableDirections(row, col, canPush) > 0)) {
				stone = stonePushable;
				stone.setPushes(canPush);
			}
			else {
				stone = stoneOnEmpty;
			}
		}
		return stone;
	}
	public void showGrid(boolean b) {
		showGrid  = b;
		panel.repaint(panel.getBounds());
	}
	public boolean getShowGrid() {
		return showGrid;
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
