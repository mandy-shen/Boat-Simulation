/*
 * Copyright All rights reserved.
 * File: SimulationThread.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.util.Observable;

/**
 * This abstract class is only display the Simulation's Thread code.
 * Others such as boatList and oilGridList, 
 * Please see the subclass Simulation.java.
 * @author Mon-Hong Shen
 */
public abstract class SimulationThread extends Observable implements Runnable {

	/////// for Thread  ///////
	private Thread thread = null; // the thread that runs my simulation
	private boolean paused = false;
	private boolean done = false; // set true to end the simulation loop
	private boolean running = false; // set true if the simulation is running
	private long simDelay = 100L; // time adjustment to slow down the simulation loop
	public enum Direction { NORTH, SOUTH, EAST, WEST, NO; }

    /**
     * Allow for external control of the periodic simulation thread delay
     * @param simDelay the time in millis to delay on each cycle (i.e. 500L = 0.5 seconds0
     */
	public void setSimDelay(long simDelay) {
		this.simDelay = simDelay;
	}
	
	/**
	 * Are we currently in a paused state
	 * @return true if paused
	 */
	public boolean isPaused() {
		return paused;
	}
	
	/**
	 * show if the pause button could be pausable
	 * @return true if running or not be done
	 */
	public boolean isPausable() {
		if (!running) return false;
		else if (done) return false;
		else return true;
	}
	
	/**
	 * Is this simulation currently running?
	 * @return true if the simulation is active
	 */
	public boolean isRunning() {
		return running;
	}
	
	/**
	 * Is this simulation stopped?
	 * @return true if the simulation is active
	 */
	public boolean isDone() {
		return done;
	}
	
	/**
	 * Is this simulation before its running?
	 * @return true if before simulation's running
	 */
	public boolean isBeforeStarting() {
		return thread==null;
	}

	/**
	 * The main run method for this simulation.
	 */
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		
		runSimLoop();
		
		long endTime = System.currentTimeMillis();
		long duration = (endTime - startTime);
		System.out.printf("%s duration: %s milliseconds\n", thread.getName(), duration);
		
		thread = null; // flag that the simulation thread is finished
	}
	
	/**
	 * A simulation loop that continuously runs
	 */
    private void runSimLoop() {
    	running = true;
    	while(!done) {
    		// do some simulation work
    		if (!paused)
    		    updateSim();
    		sleep(simDelay); // A half second sleep is the default
    	}
    	running = false;
    }
    
	/**
	 * Make the current thread sleep a little
	 * @param millis the time to sleep before the thread may re-awaken
	 */
    private void sleep(long millis) {
    	try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
	
	
	/////// for UI  ///////
	/**
	 * Start the simulation thread - create a Thread if needed
	 */
	public void startSim() {
		System.out.println("Starting the simulation");
		if (thread != null) return; // A thread is already running
		
		thread = new Thread(this); // Create a worker thread
		running = true;
		paused = false;
		done = false; // reset the done flag.
		
		start();
		
		thread.start();
	}
	
	
	/**
	 * for different type of simulation (startSim())
	 */
	protected abstract void start();

	/**
	 * Force an early stop of the simulation by setting done = true
	 */
	public void stopSim() {
		stop();
		
		if (thread == null) return; // defensive coding in case the thread is null
		done = true;
		customNotifyObservers("Stop the simulation");
	}
	
	/**
	 * Actual behavior of stop simulation
	 */
	protected abstract void stop();
	
	/**
	 * Pause the Simulation thread execution
	 */
	public void pauseSim() {
		paused = !paused;
		customNotifyObservers("Pause the simulation: " + paused);
	}
	
	/**
     * Perform an update on my simulation
     */
	public void updateSim() {
    	update();
    	customNotifyObservers();
    }
	
    /**
	 * Actual behavior of update simulation
	 */
    protected abstract void update();
	
    
    /**
     * change a wind's direction by UI
     */
	public void changeDirectionSim(Direction wind) {
		changeDirection(wind);
		customNotifyObservers();
	}
	
	/**
	 * Actual behavior of changing a wind's direction
	 */
    protected abstract void changeDirection(Direction wind);

	/**
	 * new a boat from button by UI
	 */
	public void newBoatSim() {
		newBoatToList();
		customNotifyObservers();
	}
	
	/**
	 * Actual behavior of new a boat
	 */
	protected abstract void newBoatToList();

	/**
	 * clear
	 */
	public void clearBoatSim() {
		clearBoatList();
		customNotifyObservers();
	}
	
	/**
	 * Actual behavior of clearing boats
	 */
	protected abstract void clearBoatList();
	
	/**
	 * new a oil grid from button by UI
	 */
	public void newOilGridSim() {
		addOilGrid();
		customNotifyObservers();
	}
	
	/**
	 * new a oil grid from button by UI
	 */
	protected abstract void addOilGrid();
	
	/**
	 * clear OilGrid from button by UI
	 */
	public void clearOilGridSim() {
		clearOilGridList();
		customNotifyObservers();
	}
	
	/**
	 * Actual behavior of clearing OilGrid
	 */
	protected abstract void clearOilGridList();
	
	/**
	 * my notify Observer (MyCanvas.java) for not printing message
	 * (simplify the codes)
	 */
	private void customNotifyObservers() {
		customNotifyObservers("");
	}
	
	/**
	 * my notify Observer (MyCanvas.java) for printing message
	 * (simplify the codes)
	 */
	private void customNotifyObservers(String msg) {
    	setChanged();
    	notifyObservers(this); // Send a copy of the simulation
    	if (!"".equals(msg))
    		System.out.println(msg);
	}

}
