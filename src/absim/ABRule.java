/*
 * Copyright All rights reserved.
 * File: ABRule.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.util.LinkedList;

/**
 * The behavior of a boat instance on the ocean
 * @author Mon-Hong Shen
 */
public interface ABRule {
	
	void forceStop();
	
	void restart(int x, int y); // restart from port
	
	void moveTo(int x, int y);
	
	void clean(LinkedList<OceanGrid> oilList);
	
}
