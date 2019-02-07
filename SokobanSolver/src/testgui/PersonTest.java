package testgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.Figure;
import gui.GraphicObj;
import gui.Person;
import gui.SokoMove;
import gui.Utils;
import gui.Vec;

public class PersonTest {
	
	private static PersonTest instance;
	
	private ArrayList<GraphicObj> drawings;
	
	public static void main(String[] args) {
		PersonTest.getInstance().show();
    }
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
	}
	public PersonTest() {
		drawings = new ArrayList<GraphicObj>();
	}
	public static PersonTest getInstance() {
		if (instance == null) {
			instance = new PersonTest();
		}
		return instance;
	}
	public void addDrawing(GraphicObj drawing) {
		drawings.add(drawing);
	}
	private static void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
        SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("Swing Paint Demo");
        System.out.println("1/3f = " + 1/3f);
        Person person = new Person();
        MyPanel panel = new MyPanel(person);
        
        
        
        //person.scale(2f);
        f.addKeyListener(panel);
        
        System.out.println("person.getBounds(): " + person.getBounds());
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(panel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        System.out.println("panel.getSize(): " + panel.getSize());
        System.out.println("panel.getMaximumSize(): " + panel.getMaximumSize());
    }
	public ArrayList<GraphicObj> getDrawings() {
		return drawings;
	}
}
class MyPanel extends JPanel implements KeyListener {
	private static final int drawingWidth = 50;
	private static final int drawingHeight = 50;
	
	/* Custom colors */
	Color lightBlue = new Color(200,200,255);
	
	Person person;
	Vec dv = new Vec(drawingWidth,0);
	
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
	Rectangle personLoc = new Rectangle(drawingWidth, drawingHeight);
	
    public MyPanel(Person person) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        setBackground(Color.WHITE);
        this.person = person;
        person.setDrawingArea(drawingWidth, drawingHeight);
        
        addKeyListener(this);
        System.out.println("getMaximumSize(): " + getMaximumSize());
        
        diamond.translate(new Vec(55,100));
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
        System.out.println("bigbyte + 1 " + bigbyte);
        System.out.println("bigbyte & 0x80 " + (bigbyte & 0xFF)); 
        
        
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
        
        person.draw((Graphics2D) g);
        
        // test some shapes
        para.draw((Graphics2D)g);
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
        wall.draw((Graphics2D)g);
        
        g.setColor(Color.GRAY);
        drawGrid(g, drawingWidth);
        //g.setColor(Color.RED);
        //g.drawRect(personLoc.x, personLoc.y, personLoc.width, personLoc.height);
    }

	@Override
	public void keyTyped(KeyEvent e) {
		//System.out.println("KeyTyped: " + e.getKeyChar());
		int dy = 0, dx = 0;
		SokoMove dirn = null;
		boolean bMove = true;
		
		System.out.println("keyTyped getSize(): " + getSize());
		
		switch(e.getKeyChar()) {
		case 'i': // UP
			dy = -drawingHeight;
			dirn = SokoMove.UP;
			break;
		case 'j': // LEFT
			dx = -drawingWidth;
			dirn = SokoMove.LEFT;
			break;
		case 'k': // DOWN
			dy = drawingHeight;
			dirn = SokoMove.DOWN;
			break;
		case 'l': // RIGHT
			dx = drawingWidth;
			dirn = SokoMove.RIGHT;
			break;
		 default:
			bMove = false; 
		}
		dv.setX(dx); dv.setY(dy);
		
		// A 'move' key was pressed?
		if (bMove) {
		// Ignore if person would be outside the window
			if (getBounds().contains(personLoc.x + dx,personLoc.y + dy,personLoc.width,personLoc.height)) {
				person.setDirection(dirn);
				person.translate(dv);
				repaint(personLoc);
				personLoc.translate(dx, dy);
				repaint(personLoc);
			}
		}
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
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