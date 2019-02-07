package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Composite of GraphicObjs
 * 
 * @author zhipinghe
 *
 */
public class Figure implements GraphicObj {
	private Collection<GraphicObj> objs;
	int dx,dy;
	int[] dv;
	float scalexy = 1f;
	float originalWidth = 100, originalHeight = 100;
	float newWidth,newHeight;
	
	public Figure() {
		objs = new ArrayList<GraphicObj>();
		dx=0;dy=0;
		dv = new int[2];
		dv[0]=0;dv[1]=0;
	}
	// Origin assumed to be at (0,0). Use translate if different
	// origin needed.
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

	public void add(GraphicObj toAdd) {
		objs.add(toAdd);
	}
	@Override
	public void draw(Graphics2D g) {
		// Scale and then translate or translate then scale?
		// Last change of transform is applied first
		AffineTransform save = g.getTransform();
		g.translate(dx, dy);
		g.scale(scalexy, scalexy); // Applied before translating
		for (GraphicObj obj : objs)  {
			obj.draw(g);
		}
		g.setTransform(save);
	}
	/* Scale is now calculated automatically from originalWidth, originalHeight
	 */
	@Override
	public void scale(float scale) {
		for (GraphicObj obj : objs)  {
			obj.scale(scale);
		}
	}

	@Override
	public void translate(Vec v) {
		/*for (GraphicObj obj : objs)  {
			obj.translate(v);
		}
		*/
		dx += v.dx;
		dy += v.dy;
	}

	@Override
	public Rectangle getBounds() {
		Rectangle bounds=null;
		
		for (GraphicObj obj : objs) {
			if (bounds == null) {
				bounds = obj.getBounds();
			}
			else {
				bounds = obj.getBounds().union(bounds);
			}
		}
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
