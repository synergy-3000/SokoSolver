package gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class Utils {
	    // Triangle 
		static int[] xTri = {0, 25,50};
		static int[] yTri = {43,0, 43};
		static int npTri = 3;
		// Paralellogram
		static int[] xParal = {0, 25, 50, 25};
		static int[] yParal = {24, 0,  0, 24};
		static int nParal = 4;
		// Diamond
		static int[] xDia = {0,   0, 15, 30, 30, 15};
		static int[] yDia = {32, 16,  0, 16, 32, 50};
		static int nDia = 6;
		// Star
		static int[] xStar = {25, 13, 0, 6,  0, 13, 25, 37, 50, 44, 50, 37};
		static int[] yStar = {50, 32, 32,24, 16, 16, 0, 16, 16, 24, 32, 32};
		static int nStar = 12;
		// Star
		static int[] xSquare = {0, 50, 50, 0};
		static int[] ySquare = {0,  0, 50, 50};
		static int nSquare = 4;
		
		
		public static GraphicObj makePolygon(int[] xPoints, int[] yPoints, int nPoints, Color fillColor) {
			return new Polygon(xPoints, yPoints, nPoints, fillColor);
		}
		public static GraphicObj makeTriangle(Color fillColor) {
			return makePolygon(xTri, yTri, npTri, fillColor);
		}
		public static GraphicObj makeParallelogram(Color fillColor) {
			return makePolygon(xParal, yParal, nParal, fillColor);
		}
		public static GraphicObj makeDiamond(Color fillColor) {
			return makePolygon(xDia, yDia, nDia, fillColor);
		}
		public static GraphicObj makeStar(Color fillColor) {
			return makePolygon(xStar, yStar, nStar, fillColor);
		}
		public static GraphicObj makeSquare(Color fillColor) {
			return makePolygon(xSquare, ySquare, nSquare, fillColor);
		}
		public static GraphicObj makeWall() {
			return new Wall();
		}
		public static GraphicObj makeOldWall() {
			return new OldWall();
		}
}
/*
 * Gray square patterned with white diamonds.
 */
class OldWall implements GraphicObj {
	
	GraphicObj graySquare, whiteDia;
	
	// For drawing diamond pattern on gray square
	int sxe = 8, sxo = 3; 
	int sy = 6;
	int wd = 10, hd = 10;  // width & height of diamond
	int dx = 6, dy = 13; // space between diamonds
	Vec diffv;
	Vec deltax, deltay;
	
	private Vec temp = new Vec(0,0);
	
	public OldWall() {
		graySquare = Utils.makeSquare(Color.GRAY);
		whiteDia = Utils.makeDiamond(Color.WHITE);
		whiteDia.setDrawingArea(wd, hd);
		
		diffv = new Vec(0,0);
		deltax = new Vec(dx,0);
		deltay = new Vec(0, dy);
	}
	/*
	 * Sets dx=0 and dy=0 for given obj
	 */
	private void translateToOrigin(GraphicObj obj) {
		int[] dv = obj.getTranslation();
		temp.dx = -dv[0]; temp.dy = -dv[1];
		obj.translate(temp);
	}
	@Override
	public void draw(Graphics2D g) {
		graySquare.draw(g);
		
		float scale = graySquare.getScale();
		int[] dv = graySquare.getTranslation();
		
		AffineTransform save = g.getTransform();
		
		g.translate(dv[0], dv[1]);
		g.scale(scale, scale); // Scale before translating
		
		// Draw diamond pattern
		translateToOrigin(whiteDia);
		diffv.dx = sxe; diffv.dy = sy;
		whiteDia.translate(diffv);
		// First row
		for(int col=0; col<3; col++) {
			whiteDia.draw(g);
			whiteDia.translate(deltax);
		}
		// 2nd Row
		translateToOrigin(whiteDia);
		
		
		g.setTransform(save);
	}

	@Override
	public void scale(float scale) {
		graySquare.scale(scale);
	}

	@Override
	public void translate(Vec v) {
		graySquare.translate(v);
	}

	@Override
	public Rectangle getBounds() {
		return graySquare.getBounds();
	}

	@Override
	public void setDrawingArea(float newWidth, float newHeight) {
		graySquare.setDrawingArea(newWidth, newHeight);
	}
	@Override
	public float getScale() {
		return graySquare.getScale();
	}
	@Override
	public int[] getTranslation() {
		return graySquare.getTranslation();
	}
	
}
class Polygon implements GraphicObj {
	
	int dx,dy;
	int[] dv;
	float scalexy = 1f;
	float originalWidth = 50, originalHeight = 50;
	float newWidth,newHeight;
	Color fillColor;
	int[] xCoord, yCoord;
	int nCoords;
	Rectangle bounds;
	
	/* 
	 * fillColor can be null
	 */
	public Polygon(int[] xPoints, int[] yPoints, int nPoints, Color fillColor) {
		dx=0;dy=0;
		xCoord = new int[nPoints]; yCoord = new int[nPoints]; nCoords = nPoints;
		System.arraycopy(xPoints, 0, xCoord, 0, nPoints);
		System.arraycopy(yPoints, 0, yCoord, 0, nPoints);
		int minX=xCoord[0], minY=yCoord[0];
		int maxX=xCoord[0], maxY=yCoord[0];
		for (int i=1; i<nCoords; i++) {
			minX = Math.min(minX, xCoord[i]);
			maxX = Math.max(maxX, xCoord[i]);
			minY = Math.min(minY, yCoord[i]);
			maxY = Math.max(maxY, yCoord[i]);
		}
		bounds = new Rectangle(minX, minY, (maxX-minX), (maxY-minY));
		this.fillColor = fillColor;
		originalWidth = bounds.width;
		originalHeight = bounds.height;
		dv = new int[2];
		dv[0] = 0; dv[1] = 0;
		
	}
	
	/* Origin assumed to be at (0,0). Use translate if a different
       origin is needed.
	*/
	public void setDrawingArea(float newWidth,float newHeight) {

		this.newWidth = newWidth;
		this.newHeight = newHeight;
		
		// Preserve aspect ratio
		float scalex = newWidth/originalWidth;
		float scaley = newHeight/originalHeight;
		scalexy = Math.min(scalex, scaley);
		
		// Centre in drawing area
		Rectangle bnds = getBounds();
		float scaledW = bnds.width * scalexy;
		float scaledH = bnds.height * scalexy;
		float scaledX = bnds.x * scalexy;
		float scaledY = bnds.y * scalexy;
		
		float dxCentre = (newWidth - scaledW)/2.0f - scaledX;
		float dyCentre = (newHeight - scaledH)/2.0f - scaledY;
		
		dx += dxCentre;
		dy += dyCentre;
	}
	
	@Override
	public void draw(Graphics2D g) {
		// Scale and then translate or translate then scale?
		// Last change of transform is applied first
		AffineTransform save = g.getTransform();
		g.translate(dx, dy);
		g.scale(scalexy, scalexy); // Applied before translating
		if(fillColor!=null) {
			Color saveColor = g.getColor();
			g.setColor(fillColor);
			g.fillPolygon(xCoord, yCoord, nCoords);
			g.setColor(saveColor);
		}
		else {
			g.drawPolygon(xCoord, yCoord, nCoords);
		}
		g.setTransform(save);
	}
	
	/* Scale is now calculated automatically from originalWidth, originalHeight
	   and 
	 */
	@Override
	public void scale(float scale) {
		; // Deliberately blank
	}

	@Override
	public void translate(Vec v) {
		dx += v.dx;
		dy += v.dy;
	}

	@Override
	public Rectangle getBounds() {
		return bounds;
	}

	@Override
	public float getScale() {
		return scalexy;
	}
	
	@Override
	public int[] getTranslation() {
		dv[0] = dx; dv[1] = dy;
		return dv;
	}
	
}
