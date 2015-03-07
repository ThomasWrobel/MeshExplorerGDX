package com.lostagain.nl.me.domain;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;

public class ColourMap {

	private static String logstag="ME.ColourMap";

	/*
	 * Generates a gradient based on color points. Start with simplee 4 points of colors (randomly offset from base color), but support more eleborate gradients for future

- get requested x/y
- get distances to color points
- 5,6,3,9    
- get total distance 23
- work out ratio of each relative to that (5/23,6/23,3/23,9/23) etc
- Loop over ratios and tint according to those proportions!






If too slow burn everything into a 2D array to look up later?
	 */
	
	class ColourPoint {
		
		Color pointsColor = Color.BLACK;
		Vector2 location = new Vector2();
		
		/**used to store distances to another point temporarily (makes getting the color easier latter)**/
		public float distanceTemp=0;
			
		
		public ColourPoint(Color pointsColor, Vector2 location) {
			super();
			this.pointsColor = pointsColor;
			this.location = location;
		}			
		
	}
	
	
	Color baseColour = Color.GREEN;
	Rectangle coversArea = null;
	ArrayList<ColourPoint> colourPoints = new ArrayList<ColourPoint>();
	
			
	public  ColourMap(Rectangle coversArea,Color baseColour)
	{
		this.baseColour = baseColour;
		this.coversArea = coversArea;
		
		generateMap();				
		
	}
	
	private void generateMap()
	{
		colourPoints.clear(); //ensure clear in case regenerating
		
		//pick 4 random color points
		int NumOfColors = 4;
		
		int minX = (int) coversArea.x;
		int maxDisX = (int) (coversArea.width);
		
		int minY = (int) coversArea.y;
		int maxDisY = (int) (coversArea.height);
		
		for (int i = 0; i < NumOfColors; i++) {
			
			int X = (int) (minX+(Math.random()*maxDisX));
			int Y = (int) (minY+(Math.random()*maxDisY));
			
			Color randomColor = new Color((float)Math.random(),(float)Math.random(),(float)Math.random(),1f);
			//blend with base color 50%
			//randomColor.lerp(baseColour, 0.5f);
			
			//add it to our point list
			colourPoints.add(new ColourPoint(randomColor,new Vector2(X,Y)));

			
	    	Gdx.app.log(logstag,"______setting "+randomColor.toString()+" colour point at :________"+X+","+Y);
	    	
						
		}
			
	}
	
	/**
	 * gets the color to use as the background at a particular x/y point.
	 * This could be optimised with a "cache" of an image to avoid all the distance tests
	 * 
	 * 
- get requested x/y
- get distances to color points
- 5,6,3,9    
- get total distance 23
- work out ratio of each relative to that (5/23,6/23,3/23,9/23) etc
- Loop over ratios and tint final color according to those proportions!
	 **/
	public Color getColourAtPosition(Vector2 Position)
	{
	
		float totaldistance = 0;
		
		//get distance to all points
		for (ColourPoint cp : colourPoints) {
			
			//get distance to location
			float distance = cp.location.dst2(Position.x, Position.y); //not we are actually getting the SQUARE of the distance as this is cheaper cpu wise, and we only need to compare them relatively anyway, not in absolute terms
			
			//store it in the colourpoint
			cp.distanceTemp = distance;
			
			//add it to our total
			totaldistance = totaldistance + distance;
			
			
			
		}
		
		Color requestedPositionsColor = new Color(0, 0, 0, 0);; //we start at 0,0,0,0 (r,g,b,a)
		
		//now we have all the distances we find how much their distances are relative to the total, this will tell us how much of each color to use
		//unfortunately this needs a second loop - we cant do it in the first as we need the total distance first
		
		//loop over adding amounts of color to the initial blank color created above till we get the specific blend at the requested location
		for (ColourPoint cp : colourPoints) {
			
		
			//get a value between 0-1 of how much color this contributes to the result
			float amountOfColorToUse  = cp.distanceTemp / totaldistance;

	    	//  Gdx.app.log(logstag,"______amountOfColorToUse:________"+amountOfColorToUse);
	    	  
			//now we get the R,G,B values of the locations color and reduce them to that amount (being a random of 0-1 is usefull, as you can take a % just by multipling things
			float R = cp.pointsColor.r * amountOfColorToUse;
			float G = cp.pointsColor.g * amountOfColorToUse;
			float B = cp.pointsColor.b * amountOfColorToUse;
			float A = cp.pointsColor.b * amountOfColorToUse;
						
			//now we add these color quanitys to our requested colors total and loop around for the next set to add
			requestedPositionsColor.add(R,G,B,A);
			
		}
		requestedPositionsColor.a=1;//set alpha to 1;
		
		return requestedPositionsColor;
	}
	
	
	
}
