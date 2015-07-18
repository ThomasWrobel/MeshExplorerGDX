package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;


public class HorizontalPanel extends CellPanel {
	final static String logstag = "GWTish.HorizontalPanel";
	
	//current stats
	float currentTotalWidgetWidth   = 0f;
	
	VerticalAlignment DefaultAlignmentinCell = VerticalAlignment.Middle;
	
	
	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public HorizontalPanel() {
		super(10,10); //default size and background
		
		
	}
	
	Vector2 getNextPosition(float incomingWidth,float incomingHeight,boolean updateWidth){
		
		float newLocationX = currentTotalWidgetWidth;		
		float maxH = (largestHeightOfStoredWidgets);
		
		float newLocationY = 0;
		
		if (DefaultAlignmentinCell == VerticalAlignment.Middle){
			newLocationY =  (maxH - incomingHeight)/2; //center in panel
		}
		
		if (updateWidth){
			currentTotalWidgetWidth=currentTotalWidgetWidth+incomingWidth+spaceing;
		}
		
		return new Vector2(leftPadding+newLocationX,topPadding+newLocationY);
		
	
	}

	/**
	 * Refreshes the position of all widgets 
	 * 
		recalculateLargestWidgets(); should be run first
	 */
	void repositionWidgets() {
		
		//simply clear and re-add them all
		
		//reset  stats
		
		currentTotalWidgetWidth = 0f;
		//largestWidthOfStoredWidgets = 0f;
		//largestHeightOfStoredWidgets = 0f;
		
				
		for (Widget widget : contents) {	
			
			super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}
		Gdx.app.log(logstag,"new size:"+currentTotalWidgetWidth+","+largestHeightOfStoredWidgets);
		//update back size
		this.setSizeAs(leftPadding+currentTotalWidgetWidth+rightPadding,
			     bottomPadding+largestHeightOfStoredWidgets+topPadding);
		
	}
	
	/**
	 * add many widgets at once 
	 * @param widgets
	 */
	public void add(Widget... widgets) {
		
		for (Widget widget : widgets) {
			super.add(widget);
			//resize
			Gdx.app.log(logstag,"added widget.");
		}
		
		Gdx.app.log(logstag,"new size:"+currentTotalWidgetWidth+","+largestHeightOfStoredWidgets);
		this.setSizeAs(leftPadding+currentTotalWidgetWidth+rightPadding,
			     bottomPadding+largestHeightOfStoredWidgets+topPadding);
	}
	
	@Override
	public void add(Widget widget) {
		super.add(widget);
		//resize
		Gdx.app.log(logstag,"new size:"+currentTotalWidgetWidth+","+largestHeightOfStoredWidgets);
		
		this.setSizeAs(leftPadding+currentTotalWidgetWidth+rightPadding,
				     bottomPadding+largestHeightOfStoredWidgets+topPadding);
	}

	

}
