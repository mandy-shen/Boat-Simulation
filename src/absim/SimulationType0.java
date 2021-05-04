/*
 * Copyright All rights reserved.
 * File: SimulationType0.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.util.LinkedList;

/**
 * This is a actual simulation for boats and oilGrids
 * Simple-One Boat, Random Wind
 * @author Mon-Hong Shen
 */
public final class SimulationType0 extends Simulation {
	@Override
	public void start() {
		maxAutoGenBostFlg = 0; // one boat
		maxWindDirectionFlg = 15; // random wind's direction
		
		if (boatList == null) 
    		boatList = new LinkedList<>();
    	boatList.clear();
    	boatList.add(new Boat(MAX_GRID, MAX_GRID));
    	
    	if (oilGridList == null)
			oilGridList = new LinkedList<>();
		oilGridList.clear();
		for (int i=0; i<maxOilGridCountFlg; i++)
			addOilGrid();    	
	}
}
