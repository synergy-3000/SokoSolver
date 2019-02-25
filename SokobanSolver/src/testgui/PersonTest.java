package testgui;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import gui.Controller;
import gui.GraphicObj;
import gui.MyPanel;
import gui.Person;
import setup.Graph;
import setup.GraphCreator;
import setup.Maze;
import setup.Reader;

public class PersonTest {
	
	private MyPanel panel;
	
	private static PersonTest instance;
	
	private ArrayList<GraphicObj> drawings;
	
	public static void main(String[] args) {
		PersonTest.getInstance().show();
    }
	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(); 
            }
        });
	}
	public PersonTest() {
		drawings = new ArrayList<GraphicObj>();
	}
	public static PersonTest getInstance() {
		if (instance == null) {
			instance = new PersonTest();
		}
		return instance;
	}
	public void addDrawing(GraphicObj drawing) {
		drawings.add(drawing);
	}
	private  void createAndShowGUI() {
        System.out.println("Created GUI on EDT? "+
        SwingUtilities.isEventDispatchThread());
        JFrame f = new JFrame("ZSokoban");
       
        
        
        
        /*Reader aReader = Reader.getReader();
        File file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);*/
        
        
        //person.scale(2f);
        
        
        panel = Controller.getInstance().getPanel();
        
        f.addKeyListener(Controller.getInstance());
        
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.add(panel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        
        //f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        System.out.println("panel.getSize(): " + panel.getSize());
        System.out.println("panel.getMaximumSize(): " + panel.getMaximumSize());
        System.out.println("6/4 : " + 6/4);
    }
	public ArrayList<GraphicObj> getDrawings() {
		return drawings;
	}
}
