/* 
 * Copyright All rights reserved.
 * File: ABApp.java 
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0 
 * JDK: JDK 1.8.0_241
 */
package ui;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * This abstract class only displays the frame's behavior and setting
 * Demonstrating the UI's layout
 * @author Mon-Hong Shen
 */
public abstract class ABApp implements ActionListener {

	protected JFrame frame;
	
	public ABApp() {
		System.out.println("ABApp constructor starting");
		initGUI();
	}
	
	/**
	 * Initialize the Graphical User Interface
	 */
    public void initGUI() {
    	frame = new JFrame();
		frame.setTitle("ABApp");

		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //JFrame.DISPOSE_ON_CLOSE)
		
		frame.setLayout(new BorderLayout());
		frame.add(getNorthPanel(), BorderLayout.NORTH);
		frame.add(getCenterPanel(), BorderLayout.CENTER);
    }
 
    
    /**
     * Override this method to provide the control panel panel.
     * @return a JPanel, which contains the north content of of your application
     */
    public abstract JPanel getNorthPanel();
    
    /**
     * Override this method to provide the main content panel.
     * @return a JPanel, which contains the main content of of your application
     */
    public abstract JPanel getCenterPanel();
    
    /**
     * A convenience method that uses the Swing dispatch threat to show the UI.
     * This prevents concurrency problems during component initialization.
     */
    public void showUI() {
        SwingUtilities.invokeLater(() -> {
            frame.setVisible(true);
        });
    }
    
    /**
     * Shut down the application
     */
    public void exit() {
    	frame.dispose();
    	System.exit(0);
    }

    /**
     * Override this method to show an About Dialog
     */
    public void showHelp() {
    }
 
}

