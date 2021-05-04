/*
 * Copyright All rights reserved.
 * File: OceanGrid.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package absim;

import java.awt.Color;

/**
 * every single ocean grid
 * @author Mon-Hong Shen
 */
public class OceanGrid {
	
	private int x;
	private int y;
	private Color color;
	
	public OceanGrid(int x, int y, Color color) {
		super();
		this.x = x;
		this.y = y;
		this.color = color;
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
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void darker() {
		this.color = color.darker();
	}
	
	public void brighter() {
		this.color = color.brighter();
	}
}
