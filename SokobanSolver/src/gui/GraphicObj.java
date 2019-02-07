package gui;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface GraphicObj {
	public void draw(Graphics2D g);
	public void scale(float scale);
	public void translate(Vec v);
	public Rectangle getBounds();
	public void setDrawingArea(float newWidth, float newHeight);
	public float getScale();
	public int[] getTranslation();
}
