package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Circle implements GraphicObj {
	float r;
	Vec centre;
	Rectangle bounds;
	float originalWidth = 100, originalHeight = 100;
	float newWidth,newHeight;
	float scalexy = 1.0f;
	Vec translation;
	int[] dv;
	
	public Circle(float radius, Vec centre) {
		r=radius;
		this.centre = new Vec (centre.dx, centre.dy);
		bounds = new Rectangle();
		dv = new int[2];
		dv[0]=0;dv[1]=0;
	}
	@Override
	public void draw(Graphics2D g) {
        Rectangle rec = getBounds();
		g.drawOval(rec.x, rec.y, rec.width, rec.height);;
	}

	@Override
	public void scale(float scale) {
		scalexy = scale;
		r = r * scale;
		centre.mult(scale);
	}

	@Override
	public void translate(Vec v) {
		centre.translate(v);
		dv[0] += Math.round(v.dx);
		dv[1] += Math.round(v.dy);
	}
	@Override
	public Rectangle getBounds() {
		int x = Math.round(centre.dx - r);
		int y = Math.round(centre.dy - r);
		int width = Math.round(2*r);
		
		bounds.setBounds(x, y, width, width);
		
		return bounds;
	}

	@Override
	public void setDrawingArea(float newWidth, float newHeight) {
		
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
