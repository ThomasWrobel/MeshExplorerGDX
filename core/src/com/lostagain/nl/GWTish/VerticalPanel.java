package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;


public class VerticalPanel extends CellPanel {

	//current stats
	float currentTotalWidgetHeight   = 0f;
	

	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public VerticalPanel() {
		super(10,10); //default size and background
		
		
	}
	
	Vector2 getNextPosition(float incomingWidth,float incomingHeight,boolean updateHeight){
		
		float newLocationX = 0;		
		float newLocationY = currentTotalWidgetHeight; //under the last widget
		
		if (updateHeight){
			currentTotalWidgetHeight=currentTotalWidgetHeight+incomingHeight+spaceing;
		}
		
		return new Vector2(newLocationX,newLocationY);
		
	
	}

	/**
	 * Refreshes the position of all widgets 
	 */
	void repositionWidgets() {
		
		//simply clear and re-add them all
		
		//reset  stats
		currentTotalWidgetHeight = 0f;
		currentLargestWidgetsWidth = 0f;
		currentLargestWidgetsHeight = 0f;
				
		for (Widget widget : contents) {	
			
			super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}
		
		//update back size
		this.setSizeAs(currentLargestWidgetsWidth,currentTotalWidgetHeight);
		
		
	}
	

	@Override
	public void add(Widget widget) {
		super.add(widget);
		//resize
		this.setSizeAs(currentLargestWidgetsWidth,currentTotalWidgetHeight);
	}

	

}
