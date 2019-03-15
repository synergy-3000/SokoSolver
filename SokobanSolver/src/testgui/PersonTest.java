package testgui;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import gui.Controller;
import gui.GraphicObj;
import gui.MyPanel;

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
        
       
        
        
        
        /*Reader aReader = Reader.getReader();
        File file = new File("/Users/zhipinghe/Desktop/SokobanMaze1.txt");
		Maze maze = aReader.readMaze(file);
		GraphCreator gc = GraphCreator.getGraphCreator();
		Graph graph = gc.createPushGraph(maze);*/
        
        
        //person.scale(2f);
        
        JFrame f = Controller.getInstance().getFrame();
        f.setVisible(true);
        
        //f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel panel = Controller.getInstance().getPanel();
        System.out.println("panel.getSize(): " + panel.getSize());
        System.out.println("panel.getMaximumSize(): " + panel.getMaximumSize());
        System.out.println("6/4 : " + 6/4);
    }
	public ArrayList<GraphicObj> getDrawings() {
		return drawings;
	}
}
