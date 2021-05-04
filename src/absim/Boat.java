/*
 * Copyright All rights reserved.
 * File: Boat.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Every Boat has its own id
 * @author Mon-Hong Shen
 */
public class Boat implements Serializable, ABRule {
	
	private static final long serialVersionUID = 1L;
	private static final int MAX_LOAD = 100;
	private static final int MIN_LOAD = 0;
	private static final int MAX_BATT = 500;
	private static final int MIN_BATT = 0;
	private enum Status {MOVING, STOP, CHARGING, CLEAN_LOADS}
	
	private static int idCounter = 0;
	private String id;
	private String name;
	private Status status;
	private int x;
	private int y;
	private double direction; // degrees relative to North
	private boolean speed; // true=moving, false=stop
	private int loadCap;   // loading capacity (kg)
	private int loadUsg;   // usage of load (kg) 
	private int battCap;   // battery capacity
	private int battUsg;   // usage of battery
	
	public Boat(int x, int y) {
		this.id = "b" + (++idCounter);
		this.name = "Boat_" + id; // only setName in the constructor
		this.status = Status.STOP;
		this.x = x;
		this.y = y;
		this.direction = 270;     // default=Northwest
		this.speed = true;       // default=true(start to working)
		this.loadCap = MAX_LOAD; // default=100
		this.loadUsg = MIN_LOAD; // default=no usage
		this.battCap = MAX_BATT; // default=100
		this.battUsg = MIN_BATT; // default=no usage
	}
	
	public String getName() {
		return name;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * degrees relative to North
	 * 0=North, 90=East, 180=South, 270=West
	 * 45=Northeast, 135=Southeast, 225=Southwest, 270=Northwest
	 * @param x
	 * @param y
	 */
	private void setDirection(int x, int y) {
		int diffX = x-this.x;
		int diffY = y-this.y;
		
		if (diffX==0 && diffY<0) direction = 0;
		else if (diffX>0 && diffY==0) direction = 90;
		else if (diffX==0 && diffY>0) direction = 180;
		else if (diffX<0 && diffY==0) direction = 270;
		else if (diffX>0 && diffY<0) direction = 45;
		else if (diffX>0 && diffY>0) direction = 135;
		else if (diffX<0 && diffY>0) direction = 225;
		else if (diffX<0 && diffY<0) direction = 315;
	}
	
	/**
	 * @param batteryUsg
	 */
	private void setBattUsg(int battUsg) {
		this.battUsg = battUsg;
		if (battUsg < MIN_BATT) this.battUsg = MIN_BATT;
		if (battUsg > MAX_BATT) this.battUsg = MAX_BATT;
	}
	
	/**
	 * if it would consume more than remaining
	 * @param extraBattUsg
	 */
	private boolean isNotEnoughBatt(int extraBattUsg) {
		return (battCap-battUsg) < extraBattUsg;
	}

	/**
	 * @param loadUsg
	 */
	private void setLoadUsg(int loadUsg) {
		this.loadUsg = loadUsg;
		if (loadUsg < MIN_LOAD) this.loadUsg = MIN_LOAD;
		if (loadUsg > MAX_LOAD) this.loadUsg = MAX_LOAD;
	}
	
	/**
	 * if it would consume more than remaining
	 * @param extraloadUsg
	 */
	private boolean isNotEnoughLoad(int extraLoadUsg) {
		return (loadCap-loadUsg) < extraLoadUsg;
	}
	
	private void charging() {
		this.speed = false;
		this.status = Status.CHARGING;
		setBattUsg(0);
	}
	
	private void cleanLoads() {
		this.speed = false;
		this.status = Status.CLEAN_LOADS;
		setLoadUsg(0); // clean loads
	}
	
	private void stop() {
		this.speed = false;
		this.status = Status.STOP;
	}
	
	private void start() {
		this.speed = true;
		this.status = Status.MOVING;
	}

	/**
	 * for printing Boat's content.
	 */
	@Override
	public String toString() {
		return String.format("[%8s]%4s(%3d,%3d), direction=%5.1f, speed=%5s, load(usg/cap)=%3d/%3d, battery(usg/cap)=%3d/%3d"
				, status, name, x, y, direction, speed, loadUsg, loadCap, battUsg, battCap);
	}
	
	/**
	 * move boat to the destination:
	 * every step consumes one battery.
	 * @param x
	 * @param y
	 */
	@Override
	public void moveTo(int x, int y) {
		if (this.x == x && this.y == y) {
			stop();
			return;
		}
		
		int speed = 1;
		if (isNotEnoughBatt(speed)) {
			charging();
			return;
		}
		
		start();

		setDirection(x, y);
		
		if (x > this.x) this.x+=speed;
		else if (x < this.x) this.x-=speed;
		
		if (y > this.y) this.y+=speed;
		else if (y < this.y) this.y-=speed;
		
		battUsg+=1;
	}
	
	
	/**
	 * clean Oil Grid 
	 */
	@Override
	public void clean(LinkedList<OceanGrid> oilList) {
		if (oilList == null) return;
		
		OceanGrid nextGrid = oilList.peekFirst();
		if (nextGrid == null) return;
		
		int extraLoadUsg = 1;
		if (isNotEnoughLoad(extraLoadUsg)) {
			cleanLoads();
			return;
		}
		
		int x = nextGrid.getX();
		int y = nextGrid.getY();
		moveTo(x, y);
		
		if (this.x == x && this.y == y) {
			oilList.remove(nextGrid);
			loadUsg+=extraLoadUsg;
		}
	}

	@Override
	public void forceStop() {
		stop();
	}

	@Override
	public void restart(int x, int y) {
		this.x = x;
		this.y = y;
		this.direction = 270;
		this.battUsg = 0;
		this.loadUsg = 0;
		start();
	}
	
}
