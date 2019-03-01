package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Line implements GraphicObj {
	Vec from, to;
	Rectangle bounds;
	float origWidth,origHeight, newWidth, newHeight;
	int dx, dy;
	float scalexy = 1.0f;
	int[] dv;
	
	public Line(Vec from, Vec to) {
		// Make a copy
		this.from = new Vec(from.dx, from.dy);
		this.to = new Vec(to.dx, to.dy);
		bounds = new Rectangle();
		dx = dy = 0;
		dv = new int[2];
		dv[0] = 0; dv[1] = 0;
	}
	@Override
	public void draw(Graphics2D g) {
		int x1 = Math.round(from.dx);
		int y1 = Math.round(from.dy);
		int x2 = Math.round(to.dx);
		int y2 = Math.round(to.dy);
		g.drawLine(x1,y1,x2,y2);
	}

	@Override
	public void scale(float scale) {
		from.mult(scale);
		to.mult(scale);
		scalexy = scale;
	}

	@Override
	public void translate(Vec v) {
		from.translate(v);
		to.translate(v);
		dv[0] += Math.round(v.dx);
		dv[1] += Math.round(v.dy);
	}
	@Override
	public String toString() {
		return "Line: " + from + " to " + to;
	}
	@Override
	public Rectangle getBounds() {
		int x,y,width,height;
		
		x = Math.round(from.dx);
		width = Math.round(to.dx-from.dx);
		
		if (from.dx > to.dx) {
			x = Math.round(to.dx);
			width *= -1;
		}
		
		y = Math.round(from.dy);
		height = Math.round(to.dy-from.dy);
		
		if (from.dy > to.dy) {
			y = Math.round(to.dy);
			height *= -1;
		}
		bounds.setBounds(x, y, width, height);
		return bounds;
	}

	@Override
	public void setDrawingArea(float newWidth, float newHeight) {
		// Now handled by AbstractGraphicObj (the composite of GraphicObjs)
		
	}
	@Override
	public float getScale() {
		return scalexy;
	}
	@Override
	public int[] getTranslation() {
		return dv;
	}
	
}
