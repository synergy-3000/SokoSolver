package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

public abstract class AbstractGraphicObj implements GraphicObj {
	int dx,dy;
	int[] dv;
	float scalexy = 1f;
	float originalWidth = 50, originalHeight = 50;
	float newWidth,newHeight;
	
	@Override
	public void draw(Graphics2D g) {
		// Last change of transform is applied first
		AffineTransform save = g.getTransform();
		g.translate(dx, dy);
		g.scale(scalexy, scalexy); // Applied before translating
		
		render(g);
		
		g.setTransform(save);

	}

	@Override
	public void scale(float scale) {

	}

	@Override
	public void translate(Vec v) {
		dx += v.dx;
		dy += v.dy;
	}

	abstract public Rectangle getBounds();
	abstract protected void render(Graphics2D g);

	@Override
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
	public float getScale() {
		return scalexy;
	}

	@Override
	public int[] getTranslation() {
		dv[0] = dx; dv[1] = dy;
		return dv;
	}

}
