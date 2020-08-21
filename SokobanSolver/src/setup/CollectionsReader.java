package setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

//Read in all puzzles from ClassicLevels file show # Rows # Cols # Boxes # Spaces # States : done
//When setting a new maze state will also need to re-create the graph : done, new implementations of DeadPositionFinder
//TODO calc max of numStates
/*
 * Reads a file containing multiple mazes, commonly called a collection.
 */
public class CollectionsReader {
	
	static final int MAX_MAZES = 60;
	static final int MAX_MAZE_ROWS = 30;
	
	public static String[] defStr = {    
	    "    #####",
		"    #   #",
		"    #$  #",
		"  ###  $##",
		"  #  $ $ #",
		"### # ## #   ######",
		"#   # ## #####  ..#",
		"# $  $          ..#",
		"##### ### #@##  ..#",
		"    #     #########",
		"    #######", 
		"Title: Level"}; // Need this last line to indicate end of maze
	
	ArrayList<MazeState> mazeStates;
	private static List<MazeState> defaultStates = null;
	
	ArrayList<char[]> rows;
	
	int[] rowBegin;        // Start row for each maze
	int[] nCols;           // # Columns for each maze
	int biggest;           // Maze with biggest size (rows x cols)
	int[] spaces;          // # of empty spaces inside maze
	int nMazes = 0;        // # of mazes
	int nRows = 0;
	
	int[] playerLoc = new int[2];
	private static CollectionsReader instance = null;
	
	
	
	private CollectionsReader() {
		rowBegin = new int[MAX_MAZES];
		nCols = new int[MAX_MAZES];
		spaces = new int[MAX_MAZES];
		rowBegin[0] = 0;
		rows = new ArrayList<char[]>();
	}
	public static CollectionsReader getInstance() {
		if (instance == null) {
			instance = new CollectionsReader();
		}
		return instance;
	}
	public List<MazeState> getDefault() {
		if (defaultStates == null) {
			defaultStates = readCollection(Stream.of(defStr));
		}
		return defaultStates;
	}
	private List<MazeState> readCollection(Stream<String> collection) {
		ArrayList<MazeState> coll = new ArrayList<MazeState>();
		
		collection.forEachOrdered(new Consumer<String>() {
			boolean newMaze = true;
			int idxTitle;
			int i=0;
			
			@Override
			public void accept(String t) {
				System.out.println("Reading Line " + i++ + ": " + t);
				if ((idxTitle = t.indexOf("Title: Level")) >= 0) {
					List<char[]> maze = rows.subList(rowBegin[nMazes], nRows);
					System.out.printf("Maze %d ", nMazes);
					MazeState ms = Maze.parseMazeChars(maze.toArray(new char[maze.size()][]));
					ms.title = t.substring(idxTitle + "Title: ".length());
					coll.add(ms);
					System.out.println(ms);
					nMazes++;
					newMaze = true;
				}
				else {
					if (newMaze) {
						rowBegin[nMazes] = nRows;
						newMaze = false;
					}
					rows.add(t.toCharArray());
					nRows += 1;
				}
			}
		});
		return coll;
	}
	
	public List<MazeState> readCollection(File file) {
		BufferedReader d;
		List<MazeState> ret = null;
		
		try {
			d = new BufferedReader(new FileReader(file));
			Stream<String> lines = d.lines();
			Stream<String> strFiltered = lines.filter(s -> (s.indexOf('#') >= 0 || s.indexOf("Title: Level") >= 0));
			//lines.
			ret = readCollection(strFiltered);
			d.close();
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());		
		}
		catch (IOException e) {
			System.err.println(e.getMessage());		
		}
		return ret;
	}
}
