/*
 * Copyright All rights reserved.
 * File: MyCanvas.java
 * Author: Mon-Hong Shen
 * Date: 2020/08/06
 * Version: 1.0
 * JDK: JDK 1.8.0_241
 */
package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import absim.Boat;
import absim.OceanGrid;
import absim.Simulation;
import absim.SimulationThread.Direction;

/**
 * draw in the central panel
 * @author Mon-Hong Shen
 */
public class MyCanvas extends JPanel implements Observer {

	private static final long serialVersionUID = 1L;
	private static final int BOAT_SIZE = 15;
	private static final int DESC_HEIGHT = 3;
	private static final Color LAND_CLR = new Color(34, 139, 34); // green land
	private static final Color OCEAN_CLR = new Color(30, 144, 255); // blue ocean
	
	private Dimension size;
	private int halfWidth;
	private int halfHeight;
	private int gridWidth;
	private int gridHeight;
	private int edgeX;
	private int edgeY;
	private Simulation mySim;
	private LinkedList<OceanGrid> oilGridList;
	private LinkedList<Boat> boatList;
	private Direction wind;
	private int maxDarkerFlg;
	private int maxWindIntensityFlg;
	private int maxWindDirectionFlg;
	
	// Swing calls when a redraw is needed
	@Override
	public void paint(Graphics g) {
		drawCanvas(g);
	}

	// Draw the contents of the panel
	private void drawCanvas(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		// default setting
		size = getSize();
		halfWidth  = size.width  /2;
		halfHeight = size.height /2;
		gridWidth  = (size.width/10*9)  /Simulation.MAX_GRID;
		gridHeight = (size.height/10*9) /Simulation.MAX_GRID;
		edgeX = gridWidth  *Simulation.MAX_GRID;
		edgeY = gridHeight *Simulation.MAX_GRID;
		g2d.setFont(new Font("default", Font.BOLD, 12));
		
		drawLand(g2d);
		drawOcean(g2d);
		drawOil(g2d);
		drawWind(g2d);
		drawBoat(g2d);
		drawStation(g2d);
	}
	
	private void drawStation(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.drawOval(edgeX, edgeY, gridWidth, gridHeight);
		g2d.drawString("PORT", edgeX, edgeY);
	}

	private void drawWind(Graphics2D g2d) {
		if (wind == null) wind = Direction.NO;
		if (oilGridList == null) oilGridList = new LinkedList<>();
		if (boatList == null) boatList = new LinkedList<>();
		int descGH = gridHeight*DESC_HEIGHT;
		
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.drawString("[WIND]", edgeX+5, descGH*1);
		if(maxWindIntensityFlg != 0)
			g2d.drawString("intensity: 1/"+maxWindIntensityFlg, edgeX+5, descGH*2);
		g2d.drawString("direction: "+wind.name(), edgeX+5, descGH*3);
		if(maxWindDirectionFlg != 0)
			g2d.drawString("change rate: 1/"+maxWindDirectionFlg, edgeX+5, descGH*4);
		
		g2d.drawString("[OIL]", edgeX+5, descGH*6);
		g2d.drawString("remain: "+oilGridList.size(), edgeX+5, descGH*7); 
		g2d.drawString("darker rate: 1/"+maxDarkerFlg, edgeX+5, descGH*8); 
		
		g2d.drawString("[BOAT]", edgeX+5, descGH*10);
		g2d.drawString("count: "+boatList.size(), edgeX+5, descGH*11); 
	}

	private void drawLand(Graphics2D g2d) {
		g2d.setColor(LAND_CLR);
		g2d.fillRect(0, 0, size.width, size.height);
		
		g2d.setColor(LAND_CLR.darker().darker());
		g2d.drawString("LAND", (edgeX+size.width)*0.5f, halfHeight); 
	}

	// drawOceanGrid
	private void drawOcean(Graphics2D g2d) {
		for (int i = 0; i < Simulation.MAX_GRID; i++) {
			for (int j = 0; j < Simulation.MAX_GRID; j++) {
				int startx = i * gridWidth;
				int starty = j * gridHeight;
				g2d.setColor(drawWaveColor(OCEAN_CLR, j));
				g2d.fillRect(startx, drawWaveLine(i,starty), gridWidth, gridHeight);
			}
		}
		
		g2d.setColor(OCEAN_CLR.darker().darker());
		g2d.drawString("OCEAN", halfWidth, halfHeight); 
	}
	
	private Color drawWaveColor(Color color, int cnt) {
		Color newColor = new Color(color.getRGB());
		return (cnt%5==0) ? newColor.brighter() : newColor;
	}
	
	private int drawWaveLine(int i, int y) {
		return (i%3==0) ? y : y-1;
	}

	// drawOilGrid
	private void drawOil(Graphics2D g2d) {
		if (oilGridList == null) return;
		
		oilGridList.forEach(oilGrid -> {
			int startx = oilGrid.getX() * gridWidth;
			int starty = oilGrid.getY() * gridHeight;
			g2d.setColor(drawWaveColor(oilGrid.getColor(), oilGrid.getY()));
			g2d.fillRect(startx, drawWaveLine(oilGrid.getX(),starty), gridWidth, gridHeight);
		});
	}
	
	// my gray boat
	private void drawBoat(Graphics2D g2d) {
		if (boatList == null) return;
		
		for (int i=0; i<boatList.size(); i++) {
			Boat boat = boatList.get(i);
			int x = boat.getX();
			int y = boat.getY();
			// draw boat
			g2d.setColor(Color.WHITE);
			g2d.fillRect((x  )*gridWidth, (y-1)*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x+1)*gridWidth, (y-1)*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x-2)*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x-1)*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x  )*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x+1)*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x+2)*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			g2d.fillRect((x+3)*gridWidth, (y  )*gridHeight, BOAT_SIZE, BOAT_SIZE);
			
			// show boat's name and id
			g2d.setColor(Color.BLACK);
			g2d.drawString(boat.getName(), (x-2)*gridWidth , (y+2)*gridHeight); 
			
			// show boat's status
			g2d.setColor(Color.ORANGE);
			g2d.drawString(boat.toString(), 10, size.height-10*(i+1));
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Simulation) {
			mySim = (Simulation) arg;
			boatList = mySim.getBoatList();
			oilGridList = mySim.getOilList();
			wind = mySim.getWind();
			maxDarkerFlg = mySim.getMaxDarkerFlg();  
			maxWindIntensityFlg = mySim.getWindIntensityFlg();
			maxWindDirectionFlg = mySim.getMaxWindDirectionFlg();  
		}
		repaint(); // Tell the GUI thread that it should schedule a paint() call
	}
	
}
