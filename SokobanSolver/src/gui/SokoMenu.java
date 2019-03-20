package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

//Add menus : completed
//add a "Start again" menu item under  Edit menu
//Implement "Start again" code : completed
//add undo, redo menu items under Edit menu : completed

//add a "Show dead positions tick" check box menu item under "Preferences" menu : completed
//add a "Preferences" menu : completed
//add an "Edit" menu : completed
//add a ZSokoban menu at beginning : Done
//add Quit ZSokoban under ZSokoban menu : Done

//TODO Add a "Help" menu - "How to play"
public class SokoMenu {
	JMenuItem miUndo;
	JMenuItem miRedo;
	
	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu;
		JMenuItem menuItem;
		JCheckBoxMenuItem cbMenuItem;

		// Create the menu bar.
		menuBar = new JMenuBar();

		// Build the first menu.
		menu = new JMenu("ZSokoban");
		System.out.println("Font size: " + menu.getFont().getSize()); 
		//menu.setFont(menu.getFont().deriveFont(Font.BOLD, 16f));
		menu.setFont(menu.getFont().deriveFont(Font.BOLD));
		menuBar.add(menu);
		
		// ZSokoban JMenuItems
		menuItem = new JMenuItem("About ZSokoban");
		menuItem.addActionListener(new ActionListener() {
			//TODO Implement "About ZSokoban"
			@Override
			public void actionPerformed(ActionEvent e) {
				//Controller.getInstance().();
			}
			
		});
		menu.add(menuItem);
		menu.addSeparator();
		
		menuItem = new JMenuItem("Quit ZSokoban");
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.META_MASK));
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().getFrame().dispose();
			}
			
		});
		menu.add(menuItem);
		
		// "Edit" Menu
		menu = new JMenu("Edit");
		menuBar.add(menu);

		// Edit JMenuItems
		menuItem = new JMenuItem("Undo");
		miUndo = menuItem;
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK));
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().undo();
			}
			
		});
		menuItem.setEnabled(false);
		menu.add(menuItem);

		menuItem = new JMenuItem("Redo");
		miRedo = menuItem;
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.META_MASK | ActionEvent.SHIFT_MASK));
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().redo();
			}
			
		});
		menu.add(menuItem);
		menuItem.setEnabled(false);
		menu.addSeparator();

		menuItem = new JMenuItem("Start Again");
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Controller.getInstance().reset();
			}
			
		});
		menu.add(menuItem);

		// 'Preferences' Menu
		menu = new JMenu("Preferences");
		
		cbMenuItem = new JCheckBoxMenuItem("Show Dead Positions");
		cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Controller.getInstance().showDead(e.getStateChange() == ItemEvent.SELECTED);
			}
        	
        });
        menu.add(cbMenuItem);
        
        cbMenuItem = new JCheckBoxMenuItem("Show Available Pushes");
		cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Controller.getInstance().showPushes(e.getStateChange() == ItemEvent.SELECTED);
			}
        	
        });
        menu.add(cbMenuItem);
        
        cbMenuItem = new JCheckBoxMenuItem("Show Grid");
		cbMenuItem.setSelected(true);
        cbMenuItem.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				Controller.getInstance().showGrid(e.getStateChange() == ItemEvent.SELECTED);
			}
        	
        });
        menu.add(cbMenuItem);
		menuBar.add(menu);

		return menuBar;
	}
	public void enableUndo(boolean enabled) {
		miUndo.setEnabled(enabled);
	}
	public void enableRedo(boolean enabled) {
		miRedo.setEnabled(enabled);
	}
}
