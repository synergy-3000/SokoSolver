package setup;

public interface Node {
	public int getId();
	public boolean visited(int playerRow, int playerCol);
	public int[] visit(int playerRow, int playerCol);
	public boolean canPushTo(int nodeId);
	public void clearVisits();
}
