package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Stroke;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class MyPanel extends JPanel {
	
	public static int MAX_WIDTH = 1080;
	public static int MAX_HEIGHT = 592;
	public static int PREF_DRAWINGWIDTH = 50;
	public static int PREF_DRAWINGHEIGHT = 50;
	private static int drawingWidth = PREF_DRAWINGWIDTH;
	private static int drawingHeight = PREF_DRAWINGHEIGHT;
	
	Canvas canvas;
	
	/* Custom colors */
	Color lightBlue = new Color(200,200,255);
	
	Person person;
	Vec dv; 
	Vec tx = new Vec(50,0);
	Vec ty = new Vec(0,50);
	Vec negTx = new Vec(-50,0);
	Vec negTy = new Vec(0,-50);
	
	/* Create some shapes */
    GraphicObj para = Utils.makeParallelogram(Color.LIGHT_GRAY);
    GraphicObj diamond = Utils.makeDiamond(Color.BLACK);
    GraphicObj square = Utils.makeSquare(Color.GRAY);
    GraphicObj star = Utils.makeStar(Color.DARK_GRAY);
    GraphicObj tri = Utils.makeTriangle(Color.WHITE);
    
    GraphicObj wall = Utils.makeWall();
    
    GraphicObj oldWall = Utils.makeOldWall();
    
    
	// Keep track of person position
	Rectangle personLoc;
	private Rectangle bounds;
	
	private BasicStroke dashed;
	
    public MyPanel(Person person, int dWidth, int dHeight, Canvas canvas) {
    	bounds = new Rectangle();
    	
    	dashed = new BasicStroke(1, BasicStroke.CAP_SQUARE,BasicStroke.JOIN_MITER,10.0f, new float[] {2f, 10f},0f);
    	
    	drawingWidth = dWidth;
    	drawingHeight = dHeight;
    	personLoc = new Rectangle(drawingWidth, drawingHeight);
    	dv = new Vec(drawingWidth,0);
    	
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.WHITE);
        this.person = person;
        person.setDrawingArea(drawingWidth, drawingHeight);
        
        System.out.println("getMaximumSize(): " + getMaximumSize());
        
        this.canvas = canvas;
        
       /* diamond.translate(new Vec(55,100));
        diamond.setDrawingArea(200, 200);
        diamond.translate(new Vec(55,100));
        star.translate(new Vec(110,0));
        square.translate(new Vec(200,0));
        oldWall.translate(new Vec(150,100));
        
        // Test
        printRGB("Light  Gray RGB: " , Color.LIGHT_GRAY);
        printRGB("       Gray RGB: " , Color.GRAY);
        printRGB("Dark   Gray RGB: " , Color.DARK_GRAY);
        printRGB("Darker Gray RGB: " , Color.GRAY.darker());
        printRGB("Custom Gray RGB: " , new Color(100,100,100));
        printRGB("Blue RGB: " , Color.BLUE);
        printRGB("Brighter Blue RGB: " , Color.BLUE.darker());
        printRGB("White RGB: " , Color.WHITE);
        
        // Test
        byte bigbyte = 127;
        System.out.println("bigbyte " + bigbyte);
        bigbyte += 2;
        System.out.println("bigbyte + 2 " + bigbyte);
        System.out.println("bigbyte & 0xFF " + (bigbyte & 0xFF)); */
        
        
    }
    public void setDrawingWidthHeight(int dWidth, int dHeight) {
    	drawingWidth = dWidth;
    	drawingHeight = dHeight;
    	personLoc.setSize(dWidth, dHeight);
    	dv.setX(drawingWidth);
        person.setDrawingArea(drawingWidth, drawingHeight);
    }
    private void printRGB(String s, Color color) {
    	int red = color.getRed();
    	int green = color.getGreen();
    	int blue = color.getBlue();
    	int alpha = color.getAlpha();
    	
    	System.out.println(s + "(" + red + ", " + green + ", " + blue + ") alpha: " + alpha );
    }
    /*public Dimension getPreferredSize() {
        return new Dimension(400,400);
    }
    */
    /*public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	int dy=0,dx=0,x=0,y=0;
    	int rowH = 0;
    	
    	Rectangle bnds;
    	
    	Graphics2D g2d = (Graphics2D)g;
    	
    	AffineTransform save = g2d.getTransform();
    	
    	ArrayList<GraphicObj> objs = PersonTest.getInstance().getDrawings();
    	
    	for (GraphicObj obj: objs) {
    		
    		bnds = obj.getBounds();
    		rowH = Math.max(rowH, bnds.height);
    		
    		// New row?
    		if ((bnds.width + x) > getWidth()) {
    			g2d.translate(-x, rowH);
    			rowH=0;
    			x=0;
    		}
    		obj.draw(g2d);
    		x += bnds.width;
    		g2d.translate(bnds.width, 0);
    		
    	}
    	g2d.setTransform(save);
    }
    */
    /*public void paintComponent(Graphics g) {
    	super.paintComponent(g);
    	
    	g.setColor(Color.BLUE);
    	g.fillOval(0, 0, 50, 50);
    	g.setColor(lightBlue);
    	g.fillOval(55, 0, 50, 50);
    	
    }*/
    public void paintComponent(Graphics g) {
        super.paintComponent(g);       
        
        
        // test some shapes
        /*para.draw((Graphics2D)g);
        diamond.draw((Graphics2D)g);
        star.draw((Graphics2D)g);
        square.draw((Graphics2D)g);
        oldWall.draw((Graphics2D)g);
        wall.draw((Graphics2D)g);
        wall.translate(tx);
        wall.draw((Graphics2D)g);
        wall.translate(ty);
        wall.draw((Graphics2D)g);
        wall.translate(negTx);
        wall.draw((Graphics2D)g);*/
        
        //wall.draw((Graphics2D)g);
        
       
       
        canvas.draw((Graphics2D)g);
        
        Color save = g.getColor();
        g.setColor(Color.GRAY);
        Stroke saveStroke = ((Graphics2D)g).getStroke();
        ((Graphics2D)g).setStroke(dashed);
        drawGrid(g, drawingWidth);
        
        g.setColor(save);
        ((Graphics2D)g).setStroke(saveStroke);
        
        //g.setColor(Color.RED);
        //g.drawRect(personLoc.x, personLoc.y, personLoc.width, personLoc.height);
    }
	
	private void drawGrid(Graphics g, int gridSpace) {
        Insets insets = getInsets();
        int firstX = insets.left;
        int firstY = insets.top;
        int lastX = getWidth() - insets.right;
        int lastY = getHeight() - insets.bottom;
        
        
        //Draw vertical lines.
        int x = firstX;
        while (x < lastX) {
            g.drawLine(x, firstY, x, lastY);
            x += gridSpace;
        }
        
        //Draw horizontal lines.
        int y = firstY;
        while (y < lastY) {
            g.drawLine(firstX, y, lastX, y);
            y += gridSpace;
        }
    }
}
