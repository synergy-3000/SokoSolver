package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.function.IntUnaryOperator;

public class Stone extends AbstractGraphicObj {
	public static final int STONE_ON_GOAL = 0;         // Yellow ?
	public static final int STONE_PUSHABLE = 1;        // Light blue
	public static final int STONE_SOLVER_NEXT = 2;     // next stone to push in solution RED
	public static final int STONE_ON_EMPTY_SQUARE = 3; // Dark BLUE
	
	/*public static final int UP_MASK = 1;
	public static final int	DOWN_MASK = 2;
	public static final int RIGHT_MASK = 4;
	public static final int LEFT_MASK = 8;
	private int pushes = 0;
	*/
	final int WIDTH = 50;
	final int HEIGHT = 50;
	
	private final int TRI_HEIGHT = 10;
	
	Color orange = new Color(255, 137, 11);
	Color lightblue = new Color(0x0153CC);
	
	Rectangle bounds = new Rectangle(0,0, WIDTH, HEIGHT);
	
	private int type = STONE_ON_EMPTY_SQUARE;
	
	
	private boolean canPush[];
	int[] upTriX, downTriX, leftTriX, rightTriX;
	int[] upTriY, downTriY, leftTriY, rightTriY;
	int[][] trianglesX;
	int[][] trianglesY;
	
	public Stone(int type) {
		super();
		this.type = type;
		canPush = new boolean[4];
		Arrays.fill(canPush, false);
		
		// Make triangles
		makeTriangles();
		// Put triangles in same order as Direction.values()
		trianglesX = new int[4][];
		trianglesY = new int[4][];
		int i=0;
		for (Direction dirn : Direction.values()) {
			switch(dirn) {
			case UP:
				trianglesX[i] = upTriX;
				trianglesY[i] = upTriY;
				break;
			case DOWN:
				trianglesX[i] = downTriX;
				trianglesY[i] = downTriY;
				break;
			case LEFT:
				trianglesX[i] = leftTriX;
				trianglesY[i] = leftTriY;
				break;
			case RIGHT:
				trianglesX[i] = rightTriX;
				trianglesY[i] = rightTriY;
				break;
			}
			i += 1;
		}
	}
	private void makeTriangles() {
		upTriX = new int[3];
		downTriX = new int[3];
		leftTriX = new int[3];
		rightTriX = new int[3];
		
		upTriY = new int[3];
		downTriY = new int[3];
		leftTriY = new int[3];
		rightTriY = new int[3];
		
		int p1 = Math.round((float)(25.0 - TRI_HEIGHT / Math.sqrt(3.0)));
		int p2 = Math.round((float)(25.0 + TRI_HEIGHT / Math.sqrt(3.0)));
		
		// Left
		leftTriX[0] = 0;
		leftTriY[0] = 25;
		leftTriX[1] = TRI_HEIGHT;
		leftTriY[1] = p1;
		leftTriX[2] = TRI_HEIGHT;
		leftTriY[2] = p2;
		
		// Right - a reflection of LEFT about x = 25 
		System.arraycopy(leftTriY, 0, rightTriY, 0, 3);
		System.arraycopy(leftTriX, 0, rightTriX, 0, 3);
		Arrays.setAll(rightTriX, i -> 50 - rightTriX[i]);
		
		// Up - swap x and y in LEFT
		System.arraycopy(leftTriY, 0, upTriX, 0, 3);
		System.arraycopy(leftTriX, 0, upTriY, 0, 3);
		
		// Down - a reflection of UP about y = 25
		System.arraycopy(upTriX, 0, downTriX, 0, 3);
		System.arraycopy(upTriY, 0, downTriY, 0, 3);
		Arrays.setAll(downTriY, i -> 50 - downTriY[i]);
	}
	/*
	 * Sets which directions the stone can be pushed to. In the same order as Direction.values()
	 */
	public void setPushes(boolean canPush[]) {
		System.arraycopy(canPush, 0, this.canPush, 0, 4);
	}
	@Override
	public Rectangle getBounds() {
		return bounds;
	}
	protected void drawStoneOnEmpty(Graphics2D g) {
		 Graphics2D g2 = (Graphics2D) g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        // Retains the previous state
	        Paint oldPaint = g2.getPaint();

	        // Fills the circle with solid blue color
	        Color sphereCol = new Color(0x0153CC);
	        
	        g2.setColor(sphereCol);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Adds shadows at the top
	        Paint p;
	        p = new GradientPaint(0, 0, new Color(0.0f, 0.0f, 0.0f, 0.4f),
	                0, HEIGHT, new Color(0.0f, 0.0f, 0.0f, 0.0f));
	        g2.setPaint(p);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Adds highlights at the bottom 
	        p = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.0f),
	                0, HEIGHT, new Color(1.0f, 1.0f, 1.0f, 0.4f));
	        g2.setPaint(p);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Creates dark edges for 3D effect
	        p = new RadialGradientPaint(new Point2D.Double(WIDTH / 2.0,
	                HEIGHT / 2.0), WIDTH / 2.0f,
	                new float[] { 0.0f, 1.0f },
	                new Color[] { new Color(6, 76, 160, 127),
	                    new Color(0.0f, 0.0f, 0.0f, 0.8f) });
	        g2.setPaint(p);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Adds oval inner highlight at the bottom
	        p = new RadialGradientPaint(new Point2D.Double(WIDTH / 2.0,
	                HEIGHT * 1.5), WIDTH / 2.3f,
	                new Point2D.Double(WIDTH / 2.0, HEIGHT * 1.75 + 6),
	                new float[] { 0.0f, 0.8f },
	                new Color[] { new Color(64, 142, 203, 255),
	                    new Color(64, 142, 203, 0) },
	                RadialGradientPaint.CycleMethod.NO_CYCLE,
	                RadialGradientPaint.ColorSpaceType.SRGB,
	                AffineTransform.getScaleInstance(1.0, 0.5));
	        g2.setPaint(p);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Adds oval specular highlight at the top left
	        p = new RadialGradientPaint(new Point2D.Double(WIDTH / 2.0,
	                HEIGHT / 2.0), WIDTH / 1.4f,
	                new Point2D.Double(45.0, 25.0),
	                new float[] { 0.0f, 0.5f },
	                new Color[] { new Color(1.0f, 1.0f, 1.0f, 0.4f),
	                    new Color(1.0f, 1.0f, 1.0f, 0.0f) },
	                RadialGradientPaint.CycleMethod.NO_CYCLE);
	        g2.setPaint(p);
	        g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);
	        
	        // Restores the previous state
	        g2.setPaint(oldPaint);	
	}
	protected void drawStone(Graphics2D g, Color color) {
		//setFont(getFont().deriveFont(70.f).deriveFont(Font.BOLD));

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Retains the previous state
		Paint oldPaint = g2.getPaint();

		// Fills the circle with solid colour
		Color sphereCol = color;

		g2.setColor(sphereCol);
		g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);

		Paint p;

		// MB Test of Radial Gradient Paint
		p = new RadialGradientPaint(new Point2D.Double(WIDTH / 2.0, HEIGHT / 2.0), WIDTH / 1.4f,
				new Point2D.Double(WIDTH * 45.0 / 120.0, HEIGHT * 25.0 / 120.0), new float[] { 0.0f, 0.6f },
				new Color[] { new Color(1.0f, 1.0f, 1.0f, 1.0f), sphereCol }, RadialGradientPaint.CycleMethod.NO_CYCLE);
		g2.setPaint(p);
		g2.fillOval(0, 0, WIDTH - 1, HEIGHT - 1);	
		
		g2.setPaint(oldPaint);
	}
	protected void drawStoneSolverNext(Graphics2D g) {
		
	}
	protected void drawStonePushable(Graphics2D g) {
		drawStone(g, lightblue);
		//Draw green triangles to show directions of available pushes : done
		Color save = g.getColor();
		g.setColor(Color.GREEN);
		for (int i=0; i<4; i++) {
			if (canPush[i]) {
				g.setColor(Color.GREEN);
				g.fillPolygon(trianglesX[i], trianglesY[i], 3);
				g.setColor(Color.BLACK);
				g.drawPolygon(trianglesX[i], trianglesY[i], 3);
			}
		}
		g.setColor(save);
	}
	@Override
	protected void render(Graphics2D g) {
		switch(type) {
		case STONE_ON_GOAL:
			drawStone(g, orange);
			break;
		case STONE_PUSHABLE:
			drawStonePushable(g);
			break;
		case STONE_SOLVER_NEXT:
			drawStoneSolverNext(g);
			break;
		case STONE_ON_EMPTY_SQUARE:
			default:
			drawStoneOnEmpty(g);
			break;
		}
	}

}
