package setup;

public class PushBoxNode implements Node {
	int id;
	int[] neighbours;
	boolean visited = false;
	
	public PushBoxNode(int id, int[] neighbours) {
		this.id = id;
		this.neighbours = neighbours;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public int[] visit(int playerRow, int playerCol) {
		visited = true;
		return neighbours;
	}

	@Override
	public boolean visited(int playerRow, int playerCol) {
		return visited;
	}

	@Override
	public boolean canPushTo(int toNodeId) {
		boolean found = false;
		for (int i=0; (i<neighbours.length) && !found; i++) {
			if (neighbours[i] == toNodeId) {
				found = true;
			}
		}
		return found;
	}
}
