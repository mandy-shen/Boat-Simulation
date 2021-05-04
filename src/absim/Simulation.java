/*
 * Copyright All rights reserved.
 * File: Simulation.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is a actual simulation for boats and oilGrids
 * @author Mon-Hong Shen
 */
public abstract class Simulation extends SimulationThread {

	// Canvas
    public static final int MAX_GRID = 100;
	protected static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
	protected static final Color ORG_CLR = new Color(255, 0, 0);
	
	// automation flag (to generate boats automatically)
	protected int autoGenBostFlg;
	protected int maxAutoGenBostFlg;	// only one boat
	// generate oilGrid
	protected int maxOilGridCountFlg = 30;	// initial oil size
	
	// oil diffusion 
	// darker factor
	protected int darkerFlg;
	protected int maxDarkerFlg = 20; ///=> 1/20
	// wind factor: Intensity
	protected int windIntensityFlg;
	protected int maxWindIntensityFlg = 50; ///=>> 1/50
	// wind factor: direction
	protected int windDirectionFlg;
	protected int maxWindDirectionFlg; 
	// wind's direction
	protected Direction wind = Direction.WEST;

	public int getMaxAutoGenBostFlg() {
		return maxAutoGenBostFlg;
	}	
	public int getMaxDarkerFlg() {
		return maxDarkerFlg;
	}
	public int getWindIntensityFlg() {
		return maxWindIntensityFlg;
	}
	public int getMaxWindDirectionFlg() {
		return maxWindDirectionFlg;
	}
	public Direction getWind() {
		return wind;
	}

	// OceanGrid
	protected LinkedList<OceanGrid> oilGridList;
	public LinkedList<OceanGrid> getOilList() {
		return oilGridList;
	}

	// boatList
	protected LinkedList<Boat> boatList;
	public LinkedList<Boat> getBoatList() {
		return boatList;
	}
    
	/// initialize different factors
	protected abstract void start();
	
	
	@Override
	public final void stop() {
		boatList.forEach(boat -> boat.forceStop());
	}
	
	@Override
	public final void update() {
    	// if no polluted oil, stop the simulation.
    	if (oilGridList == null || oilGridList.isEmpty()) {
    		stopSim();
    		return;
    	}
    	
    	autoGenBoats();
		
		darkerExistingOil();
		
		diffuseOilByWind();
		
		changeWindDirection();
    	
    	// Reposition moving items
    	boatList.forEach(boat ->boat.clean(oilGridList));
	}
	
    /**
     * change a wind's direction by UI
     */
	@Override
	protected final void changeDirection(Direction wind) {
		this.wind = wind;
	}
	
	/**
	 * new a boat into boatList, its position is starting from Port:(MAX_GRID, MAX_GRID)
	 */
	@Override
	protected final void newBoatToList() {
    	if (boatList == null) 
    		boatList = new LinkedList<>();
    	boatList.add(new Boat(MAX_GRID, MAX_GRID));
	}
	
	@Override
	protected void clearBoatList() {
		boatList.clear();
	}
	
	/**
	 * a new oilGrid into oilGridList
	 * if oilGridList is empty, add a RANDOM position oilGrid into oilGridList;
	 * else call diffuseNextOil()
	 */
	@Override
	protected final void addOilGrid() {
		if (oilGridList == null)
			oilGridList = new LinkedList<>();

		if (oilGridList.isEmpty())
			oilGridList.add(new OceanGrid(RANDOM.nextInt(MAX_GRID), RANDOM.nextInt(MAX_GRID), ORG_CLR));
		else 
			genNextOil();
	}
	
	@Override
	protected void clearOilGridList() {
		oilGridList.clear();
	}
	
	/**
	 * generate next nearest oil into oilGridList by RANDOM (no wind)
	 */
	private final void genNextOil() {
		OceanGrid lastOilGrid = oilGridList.peekLast();
		int x = lastOilGrid.getX();
		int y = lastOilGrid.getY();
		Color clr = lastOilGrid.getColor().brighter();

		int tempX = 0;
		int tempY = 0;
		switch (RANDOM.nextInt(4)) {
			case 0: tempX = x+1; tempY = y;   break;
			case 1: tempX = x-1; tempY = y;   break;
			case 2: tempX = x;   tempY = y+1; break;
			case 3: tempX = x;   tempY = y-1; break;
			default:
		}
		tempX = changeToValidGrid(tempX);
		tempY = changeToValidGrid(tempY);
		
		oilGridList.add(new OceanGrid(tempX, tempY, clr)); 
	}
	
	/**
	 * find maximum or minimum
	 * if isLargerVal=true, find the positive boundary
	 * if isLargerVal=false,
	 * @param oilMap
	 * @param key
	 * @param val
	 * @param isLargerVal
	 */
	protected final void findBoundary(Map<Integer, Integer> oilMap, int key, int val, boolean isLargerVal) {
		Integer temp = oilMap.get(key);
		if (temp == null)
			oilMap.put(key, val);
		else if (isLargerVal && (val > temp))
			oilMap.put(key, val);
		else if (!isLargerVal && (val < temp))
			oilMap.put(key, val);
	}

	/**
	 * change To a Valid Grid's position number.
	 * if (validGrid < 0)        validGrid = 0;
	 * if (validGrid > MAX_GRID) validGrid = MAX_GRID;
	 * @param validGrid
	 * @return validGrid's number, from 0 to MAX_GRID.
	 */
	protected final int changeToValidGrid(int validGrid) {
		if (validGrid < 0)        validGrid = 0;
		if (validGrid > MAX_GRID) validGrid = MAX_GRID;
		return validGrid;
	}
	
	/**
	 * auto generated boats
	 */
	private final void autoGenBoats() {
		// wind factor: diffuseOilByWind frequency
		autoGenBostFlg++;
		if (maxAutoGenBostFlg!=0 && autoGenBostFlg>=maxAutoGenBostFlg) {
			autoGenBostFlg = 0;
			newBoatToList();
		}
	}
	
	/**
	 * darker existing oil
	 */
	private final void darkerExistingOil() {
		// oil diffusion control flag
		darkerFlg++;
		if (maxDarkerFlg!=0 && darkerFlg >= maxDarkerFlg) {
			darkerFlg = 0;
			// darker existing oil
			oilGridList.forEach(v -> v.darker());
		}
	}
	
	/**
	 * diffuse oil by wind
	 */
	private final void diffuseOilByWind() {
		// wind factor: diffuseOilByWind frequency
		windIntensityFlg++;
		if (maxWindIntensityFlg!=0 && windIntensityFlg>=maxWindIntensityFlg) {
			windIntensityFlg = 0;
			
			Map<Integer, Integer> oilMap = new HashMap<>();
			switch(wind) {
				// find diffusing edge Y -
				case SOUTH:
					oilGridList.forEach(v -> findBoundary(oilMap, v.getX(), v.getY(), false));
					oilMap.forEach((k,v) -> oilGridList.add(new OceanGrid(k, changeToValidGrid(v-1), ORG_CLR)));
					break;
				// find diffusing edge Y +
				case NORTH:
					oilGridList.forEach(v -> findBoundary(oilMap, v.getX(), v.getY(), true));
					oilMap.forEach((k,v) -> oilGridList.add(new OceanGrid(k, changeToValidGrid(v+1), ORG_CLR)));
					break;
				// find diffusing edge X +
				case WEST:
					oilGridList.forEach(v -> findBoundary(oilMap, v.getY(), v.getX(), true));
					oilMap.forEach((k,v) -> oilGridList.add(new OceanGrid(changeToValidGrid(v+1), k, ORG_CLR)));
					break;
				// find diffusing edge X -
				case EAST:
					oilGridList.forEach(v -> findBoundary(oilMap, v.getY(), v.getX(), false));
					oilMap.forEach((k,v) -> oilGridList.add(new OceanGrid(changeToValidGrid(v-1), k, ORG_CLR)));
					break;
				default:
			}
		}
	}

	/**
	 * change Wind's Direction
	 */
	private final void changeWindDirection() {
		windDirectionFlg++;
		if (maxWindDirectionFlg!=0 && windDirectionFlg>=maxWindDirectionFlg) {
			windDirectionFlg = 0;
			switch(RANDOM.nextInt(5)) {
				case 0: wind = Direction.EAST;  break;
				case 1: wind = Direction.NORTH; break;
				case 2: wind = Direction.SOUTH; break;
				case 3: wind = Direction.WEST;  break;
				case 4: wind = Direction.NO;  break;
				default:
			}
		}
	}
}
