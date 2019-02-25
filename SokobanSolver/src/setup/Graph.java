package setup;

public interface Graph {
	public int getNodeRow(int nodeId);
	public int getNodeCol(int nodeId);
	public Node[] getNodes();
	public int getNodeAt(int row, int col);
	public void clearVisits();
}
