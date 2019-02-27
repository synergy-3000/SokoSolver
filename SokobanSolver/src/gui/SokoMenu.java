package gui;

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
//TODO Implement "Start again" code
//add undo, redo menu items under Edit menu : completed

//add a "Show dead positions tick" check box menu item under "Preferences" menu : completed
//add a "Preferences" menu : completed
//add an "Edit" menu : completed
//TODO add a ZSokoban menu at beginning
//TODO add Quit ZSokoban under ZSokoban menu
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
