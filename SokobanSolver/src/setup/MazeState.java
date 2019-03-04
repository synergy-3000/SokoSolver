package setup;

/**
 * Decided this class is unnecessary. Changed mind again! Decided to use it because it
 * seems silly for the Maze class to keep re-parsing mazeChars[][] every time a new
 * maze is set. 
 * Actually all properties of a maze can be derived from char[][] mazeChars but
 * to avoid re-parsing mazeChars[][] each time we can use this class to save the
 * results of parsing. If we wanted to save memory then re-parse each time.
 * 
 * @author zhipinghe
 *
 */
// TODO calculate # of dead positions in each maze
public class MazeState {
	public int[] player; // Row and Column of player position
	public int[][] stoneLocs; // r,c of stones
	public int numStones;
	public int numRows;
	public int numCols;
	public int[][] goalLocs;
	public char[][] mazeChars; // The maze with walls etc. Not necessarily rectangular.
	public long numStates;
	public int numSpaces;

	MazeState(int[] player, int[][] stoneLocs, int numStones, int numRows, int numCols, int[][] goalLocs,
			char[][] mazeChars, long numStates, int numSpaces) {
		this.player = player; 
		this.stoneLocs = stoneLocs; 
		this.numStones = numStones;
		this.numRows = numRows;
		this.numCols = numCols;
		this.goalLocs = goalLocs;
		this.mazeChars = mazeChars; 
		this.numStates = numStates;
		this.numSpaces = numSpaces;
	}

	// # rows, # cols, # spaces # states
	@Override
	public String toString() {

		String outStr = "MazeState:\n";
		outStr += String.format("# of rows %d # of cols %d # of stones %d # of spaces %d # of states %d", numRows,
				numCols, numStones, numSpaces, numStates);
		outStr += "\n";
		// DEBUG check that stoneLocs[][] and goalLocs[][] are correct
		/*outStr += "Check stone locations. char at stone location:\n";
		for (int[] stone : stoneLocs) {
			outStr += (mazeChars[stone[0]][stone[1]]) + "\n";
		}
		outStr += "Check goal locations. char at goal location:\n";
		for (int[] goal : goalLocs) {
			outStr += mazeChars[goal[0]][goal[1]] + "\n";
		}*/
		return outStr;
	}
}
