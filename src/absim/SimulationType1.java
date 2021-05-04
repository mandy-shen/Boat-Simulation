/*
 * Copyright All rights reserved.
 * File: SimulationType1.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.util.LinkedList;

/**
 * This is a actual simulation for boats and oilGrids
 * Manual-Auto Generating Boats, Change Wind by Hand
 * @author Mon-Hong Shen
 */
public final class SimulationType1 extends Simulation {
	@Override
	public void start() {
		maxAutoGenBostFlg = 50; // auto generate boats
		maxWindDirectionFlg = 0; // constant wind
		
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
