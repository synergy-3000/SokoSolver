package gui;

public class Vec {
	float dx,dy;
	float[] coords = new float[2];
	
	public Vec(float dx, float dy) {
		this.dx = dx;
		this.dy = dy;
	}
	public void translate(Vec v1) {
		dx += v1.dx;
		dy += v1.dy;
	}
	public float[] add(Vec v) {
		coords[0] = v.dx + dx;
		coords[1] = v.dy + dy;
		return coords;
	}
	public void mult(float scalar) {
		dx *= scalar;
		dy *= scalar;
	}
	@Override
	public String toString() {
		return "Vec: ("+dx+","+dy+")";
	}
	public void setX(float x) {
		dx = x;
	}
	public void setY(float y) {
		dy = y;
	}
	
	
}
