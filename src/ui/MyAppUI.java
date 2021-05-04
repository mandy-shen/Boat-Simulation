/*
 * Copyright All rights reserved.
 * File: MyAppUI.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import absim.SimulationThread;
import absim.SimulationThread.Direction;
import absim.SimulationType0;
import absim.SimulationType1;
import absim.SimulationType2;
import net.java.dev.designgridlayout.DesignGridLayout;

/**
 * This class is for showing every UI Components's behavior
 * @author Mon-Hong Shen
 */
public class MyAppUI extends ABApp implements Observer {

	private static final Logger LOG = Logger.getLogger(MyAppUI.class.getName());
	private static final String[] ITEM = {"Simple-One Boat, Random Wind", "Manual-Auto Genertating Boats, Change Wind by Hand", "Manual"};
	
	// northPanel group
	private JComboBox<String> comboBox;
	private JPanel northPanel;
	private JButton startBtn;
	private JButton stopBtn;
	private JButton pauseBtn;
	private JButton newBoatBtn;
	private JButton clearBoatBtn;
	private JButton newOilGridBtn;
	private JButton clearOilGridBtn;
	private JButton eastBtn;
	private JButton westBtn;
	private JButton southBtn;
	private JButton northBtn;

	// centerPanel group
	private MyCanvas canvas;
	
	// Simulation
	private SimulationThread mySim;
	private int simType;
	
	/**
	 * Constructor
	 */
	public MyAppUI() {
		LOG.info("MyAppUI started");

	 	frame.setSize(740, 750);
		frame.setTitle("MyAppUI");
		
		showUI(); // Cause the Swing Dispatch thread to display the JFrame
		
		initSim();
	}

	/**
	 * Initialize the simulation
	 */
	private void initSim() {
		switch (simType) {
			case 0: mySim = new SimulationType0(); break;
			case 1: mySim = new SimulationType1(); break;
			case 2: mySim = new SimulationType2(); break;
			default:
		}
		
		// make the subscription
		mySim.addObserver(canvas); // Allow the panel to hear about simulation events
		mySim.addObserver(this);
	}

	/**
	 * Create a north panel with buttons
	 */
	public JPanel getNorthPanel() {
		northPanel = new JPanel(); // Create a small canvas
		
		comboBox = new JComboBox<>(ITEM);
		comboBox.addItemListener((ItemEvent e) -> {
			simType = comboBox.getSelectedIndex();
			initSim();
			resetEnableBtns();
		});
		
		// addDirectionBtns
		addDirectionBtns();
		
		// add Boat and OilGrid
		addBoatAndOilGrid();
		
		// addActionBtns
		addActionBtns();
		
		// layout the panel
		DesignGridLayout pLayout = new DesignGridLayout(northPanel);
		pLayout.row().grid(new JLabel("Rule:")).add(comboBox);
		pLayout.row().grid(new JLabel("Direction:")).add(eastBtn, westBtn, southBtn, northBtn);
		pLayout.row().grid(new JLabel("Boat:")).add(newBoatBtn, clearBoatBtn)
					 .grid(new JLabel("OilGrid:")).add(newOilGridBtn, clearOilGridBtn);
		pLayout.row().grid(new JLabel("Action:")).add(startBtn, pauseBtn, stopBtn);
		
		// buttons status
		resetEnableBtns();
		
		return northPanel;
	}
	
	/**
	 * the buttons for adding and clearing boats/oilGrids
	 */
	private void addBoatAndOilGrid() {
		// boat
		newBoatBtn = new JButton("New Boat");
		newBoatBtn.addActionListener((ActionEvent e) -> {
			mySim.newBoatSim();
		});
		// clear boat
		clearBoatBtn = new JButton("Clear Boat");
		clearBoatBtn.addActionListener((ActionEvent e) -> {
			mySim.clearBoatSim();
		});
		
		// oil
		newOilGridBtn = new JButton("New OilGrid");
		newOilGridBtn.addActionListener((ActionEvent e) -> {
			mySim.newOilGridSim();
		});
		// clear oil
		clearOilGridBtn = new JButton("Clear OilGrid");
		clearOilGridBtn.addActionListener((ActionEvent e) -> {
			mySim.clearOilGridSim();
		});
	}
	
	/**
	 * add wind's direction buttons
	 */
	private void addDirectionBtns() {
		// EAST
		eastBtn = new JButton(Direction.EAST.name());
		eastBtn.addActionListener((ActionEvent e) -> {
			mySim.changeDirectionSim(Direction.EAST);
		});
		
		// WEST
		westBtn = new JButton(Direction.WEST.name());
		westBtn.addActionListener((ActionEvent e) -> {
			mySim.changeDirectionSim(Direction.WEST);
		});
		
		// SOUTH
		southBtn = new JButton(Direction.SOUTH.name());
		southBtn.addActionListener((ActionEvent e) -> {
			mySim.changeDirectionSim(Direction.SOUTH);
		});
		
		// NORTH
		northBtn = new JButton(Direction.NORTH.name());
		northBtn.addActionListener((ActionEvent e) -> {
			mySim.changeDirectionSim(Direction.NORTH);
		});
	}

	/**
	 * add start stop pause buttons
	 */
	private void addActionBtns() {
		/// startBtn
		startBtn = new JButton("Start");
		startBtn.addActionListener((ActionEvent e) -> {
			mySim.startSim();
		});
		
		/// stopBtn
		stopBtn = new JButton("Stop");
		stopBtn.addActionListener((ActionEvent e) -> {
			mySim.stopSim();
		});
		
		/// pauseBtn
		pauseBtn = new JButton("Pause");
		pauseBtn.addActionListener((ActionEvent e) -> {
			mySim.pauseSim();
			pauseBtn.setText(mySim.isPaused() ? "Unlock Pause" : "Pause");
		});
	}

	/**
	 * reset all the buttons' and comboBox' enable setting
	 */
	private void resetEnableBtns() {
		comboBox.setEnabled(true);
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		pauseBtn.setEnabled(false);
		newBoatBtn.setEnabled(false);
		newOilGridBtn.setEnabled(false);
		clearBoatBtn.setEnabled(false);
		clearOilGridBtn.setEnabled(false);
		eastBtn.setEnabled(false);
		westBtn.setEnabled(false);
		southBtn.setEnabled(false);
		northBtn.setEnabled(false);
		switch(comboBox.getSelectedIndex()) {
			case 0:
				break;
			case 1:
				eastBtn.setEnabled(true);
				westBtn.setEnabled(true);
				southBtn.setEnabled(true);
				northBtn.setEnabled(true);
				break;
			case 2:
				eastBtn.setEnabled(true);
				westBtn.setEnabled(true);
				southBtn.setEnabled(true);
				northBtn.setEnabled(true);
				newBoatBtn.setEnabled(true);
				newOilGridBtn.setEnabled(true);
				clearBoatBtn.setEnabled(true);
				clearOilGridBtn.setEnabled(true);
				break;
			default:
		}
	}

	/**
	 * Create a center panel that has a drawable JPanel canvas
	 */
	@Override
	public JPanel getCenterPanel() {
		canvas = new MyCanvas(); // Build the drawable panel
		return canvas;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {}

	/**
	 * get value from Simulation
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof SimulationThread) {
			mySim = (SimulationThread) arg;
			
			comboBox.setEnabled(!mySim.isRunning());
			startBtn.setEnabled(mySim.isBeforeStarting() || mySim.isDone());
			pauseBtn.setEnabled(mySim.isPausable());
			stopBtn.setEnabled(!mySim.isPaused());
			
			// if thread is stopped
			if (mySim.isDone())
				resetEnableBtns();
		}
	}
	
	/**
	 * initial myApp
	 * @param args
	 */
	public static void main(String[] args) {
		new MyAppUI();
		System.out.println("MyAppUI is exiting !!!!!!!!!!!!!!");
	}

}
