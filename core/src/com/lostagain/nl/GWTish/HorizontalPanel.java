package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;


public class HorizontalPanel extends CellPanel {

	//current stats
	float currentTotalWidgetWidth   = 0f;
	

	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public HorizontalPanel() {
		super(10,10); //default size and background
		
		
	}
	
	Vector2 getNextPosition(float incomingWidth,float incomingHeight,boolean updateWidth){
		
		float newLocationX = currentTotalWidgetWidth;		
		float newLocationY = 0; //under the last widget
		
		if (updateWidth){
			currentTotalWidgetWidth=currentTotalWidgetWidth+incomingWidth+spaceing;
		}
		
		return new Vector2(newLocationX,newLocationY);
		
	
	}

	/**
	 * Refreshes the position of all widgets 
	 */
	void repositionWidgets() {
		
		//simply clear and re-add them all
		
		//reset  stats
		currentTotalWidgetWidth = 0f;
		currentLargestWidgetsWidth = 0f;
		currentLargestWidgetsHeight = 0f;
				
		for (Widget widget : contents) {	
			
			super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}
		
		//update back size
		this.setSizeAs(currentTotalWidgetWidth,currentLargestWidgetsHeight);
		
		
	}
	

	@Override
	public void add(Widget widget) {
		super.add(widget);
		//resize
		this.setSizeAs(currentTotalWidgetWidth,currentLargestWidgetsHeight);
	}

	

}
