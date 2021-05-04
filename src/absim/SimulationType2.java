/*
 * Copyright All rights reserved.
 * File: SimulationType2.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.util.LinkedList;

/**
 * This is a actual simulation for boats and oilGrids
 * Manual -you can setting wind's direction, add oilGrids, add boats by yourself.
 * this simulation will continue until oilGridLiist are empty  
 * @author Mon-Hong Shen
 */
public final class SimulationType2 extends Simulation {
	@Override
	public void start() {
		maxAutoGenBostFlg = 0; // initialize one boat 
		maxWindDirectionFlg = 0; // constant wind
		
		if (boatList == null) 
    		boatList = new LinkedList<>();
    	
    	if (oilGridList == null)
			oilGridList = new LinkedList<>();
    	
	}
}
