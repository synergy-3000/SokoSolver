package setup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

//TODO Read in all puzzles from ClassicLevels file show # Rows # Cols # Boxes # Spaces # States
/*
 * Reads a file containing multiple mazes, commonly called a collection.
 */
public class CollectionsReader {
	ArrayList<char[]> rows;
	int[] rowBegin;        // Start roe for each maze
	int[] nCols;           // # Columns for each maze
	int biggest;           // Maze with biggest size rows x cols
	int[] spaces;          // # of empty spaces inside maze
	int nMazes = 0;        // # of mazes
	int nRows = 0;
	
	public void readCollection(File file) {
		BufferedReader d;
		try {
			d = new BufferedReader(new FileReader(file));
			Stream<String> lines = d.lines();
			Stream<String> strFiltered = lines.filter(s -> (s.indexOf('#') >= 0 || s.indexOf("Title: Level") >= 0));
			strFiltered.forEachOrdered(new Consumer<String>() {

				@Override
				public void accept(String t) {
					if (t.indexOf("Title: Level") >= 0) {
						//rowEnd[n]
						processMaze(rows.subList(rowBegin[nMazes], nRows));
						nMazes++;
						
					}
				}
				
			});
			//String aLine = d.readLine(); 
			d.close();
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());		
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e.getMessage());		
		}
		//boolean[] barray = new boolean[10];
		
	}
	/*
	 * Calculate # rows, # cols, # spaces # states
	 */
	private void processMaze(List<char[]> maze) {
		
	}
}
