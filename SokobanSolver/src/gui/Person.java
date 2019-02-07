package gui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

public class Person extends Figure implements GraphicObj {
	
	// Person pushing left/right
	final int[][] p = {{0,0},{56,12},{60,31},{58,40},{70,45},{80,45},{80,38},{73,53},{82,52},{82,47},
			{50,65},{62,81},{54,97},{58,97},{42,86},{32,96},{37,96}};
	
	// Person pushing up
	int[][] pulinesX = { {31,32,50,67,68},
			           {50, 50, 50},
			           {48,44,44,50,56,56,60}};
	
    int[][] pulinesY = { {28,34,43,33,27},
    				   {30,43,65},
    				   {91,96,81,65,78,88,83} };
    
    int[] circle = {40,10,20,20};
    
    // Person pushing down
    int[][] pdlinesX = { {41,34,50,68,71},
    		             {50,50},
    		             {50,44,43,50,59,59,64}};
    
    int[][] pdlinesY = {{53,56,43,54,49},
    		            {30,65},
    		            {92,89,72,65,72,86,88}};
	
	// Push right
	int[] cHead = p[1];
	int hghtHead = 10;
	int wdthHead = 5;
	
	int[] neck = {2,3};
	int[] neckX;
	int[] neckY;
	
	int[] larm = {3,4,5,6};
	int[] larmX;
	int[] larmY;
	
	int[] rarm = {3,7,8,9};
	int[] rarmX;
	int[] rarmY;
	
	int[] torso = {3,10};
	int[] torsoX;
	int[] torsoY;
	
	int[] lleg = {10,11,12,13};
	int[] llegX;
	int[] llegY;
	
	int[] rleg = {10,14,15,16};
	int[] rlegX;
	int[] rlegY;
	
	

	private SokoMove dirn;
	
	// Reflection transform
	AffineTransform reflect = new AffineTransform(-1, 0, 0, 1, 100, 0);
	
	// Push left is a reflection about x = 50 of push right
	// ...
	
	public Person() {
		super();
		Circle head = new Circle(10, new Vec(50,20));
		Vec topNeck = new Vec(50,30);
		Vec botNeck = new Vec(50, 43);
		Vec endlarm = new Vec(28, 55);
		Vec endrarm = new Vec(72, 55);
		Vec botTorso = new Vec(50,65);
		Vec endlleg = new Vec(32, 93);
		Vec endrleg = new Vec(68,93);
		Line neck = new Line(topNeck, botNeck);
		Line larm = new Line(botNeck, endlarm);
		Line rarm = new Line(botNeck, endrarm);
		Line torso = new Line(botNeck, botTorso);
		Line lleg = new Line(botTorso, endlleg);
		Line rleg = new Line(botTorso, endrleg);
		add(head);add(neck);add(larm);add(rarm);add(torso);add(lleg);add(rleg);
		
		// Right push
		neckX = getX(this.neck);
		neckY = getY(this.neck);
		
		larmX = getX(this.larm);
		larmY = getY(this.larm);
		
		rarmX = getX(this.rarm);
		rarmY = getY(this.rarm);
		
		torsoX = getX(this.torso);
		torsoY = getY(this.torso);
		
		llegX = getX(this.lleg);
		llegY = getY(this.lleg);
		
		rlegX = getX(this.rleg);
		rlegY = getY(this.rleg);
		
		setDirection(SokoMove.UP);
		
	}
	private int[] getX(int[] idx) {
		int[] retVal = new int[idx.length];
		for(int i=0; i<idx.length; i++) {
			retVal[i] = p[idx[i]][0];
		}
		return retVal;
	}
	private int[] getY(int[] idx) {
		int[] retVal = new int[idx.length];
		for(int i=0; i<idx.length; i++) {
			retVal[i] = p[idx[i]][1];
		}
		return retVal;
	}
	// Reflect about x = 50
	private void reflectX50(int[] pts, int[] reflected) {
		for (int i = 0; i<pts.length; i++) {
			reflected[i] = 100 - pts[i];
		}
	}
	public void setDirection(SokoMove move) {
		dirn = move;
	}
	@Override
	public void draw(Graphics2D g) {
		Stroke save = g.getStroke();
		
		g.setStroke(new BasicStroke(2));
		//g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		switch(dirn) {
			case UP:
				draw(g,pulinesX, pulinesY, circle);
				break;
			case DOWN:
				draw(g,pdlinesX, pdlinesY, circle);
				break;
			case RIGHT:
			case LEFT:
			AffineTransform saveT = g.getTransform();
			g.translate(dx,dy);
			g.scale(getScale(), getScale());
			if (dirn == SokoMove.LEFT) {
				g.transform(reflect);
			}
			// Head
			g.drawOval(cHead[0], cHead[1], 10, 20);
			// neck
			g.drawPolyline(neckX, neckY, neckX.length);
			// left arm
			g.drawPolyline(larmX, larmY, larmX.length);
			// right arm
			g.drawPolyline(rarmX, rarmY, rarmX.length);
			// torso
			g.drawPolyline(torsoX, torsoY, torsoX.length);
			// left leg
			g.drawPolyline(llegX, llegY, llegX.length);
			// right leg
			g.drawPolyline(rlegX, rlegY, rlegX.length);
			g.setTransform(saveT);
			break;
			
		}
		g.setStroke(save);
	}
	private void draw(Graphics2D g, int[][] linesX, int[][] linesY, int[] circle) {
		AffineTransform saveT = g.getTransform();
		g.translate(dx,dy);
		g.scale(getScale(), getScale());
		
		for (int i=0; i<linesX.length; i++) {
			g.drawPolyline(linesX[i], linesY[i], linesY[i].length);
		}
		g.drawOval(circle[0], circle[1], circle[2], circle[3]);
		g.setTransform(saveT);
		
	}
}
