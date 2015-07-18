package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;
import com.lostagain.nl.GWTish.Widget.VerticalAlignment;


public class VerticalPanel extends CellPanel {

	//current stats
	float currentTotalWidgetHeight   = 0f;
	

	HorizontalAlignment DefaultAlignmentinCell = HorizontalAlignment.Center;
	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public VerticalPanel() {
		
		super(10,10); //default size and background
		
	}
	
	Vector2 getNextPosition(float incomingWidth,float incomingHeight,boolean updateHeight){
		
		float newLocationX = 0;		
		if (DefaultAlignmentinCell == HorizontalAlignment.Center){
			float maxW= (largestWidthOfStoredWidgets);
			newLocationX =  (maxW - incomingWidth)/2;
		}
		
		float newLocationY = currentTotalWidgetHeight; //under the last widget
		
		if (updateHeight){
			currentTotalWidgetHeight=currentTotalWidgetHeight+incomingHeight+spaceing;
		}
		
		return new Vector2(leftPadding+newLocationX,topPadding+newLocationY);
		
	
	}

	/**
	 * Refreshes the position of all widgets 
	 * recalculateLargestWidgets(); should be run first
	 */
	void repositionWidgets() {
		
		//simply clear and re-add them all
		
		//reset  stats
		currentTotalWidgetHeight = 0f;
	//	largestWidthOfStoredWidgets = 0f;
	//	largestHeightOfStoredWidgets = 0f;
				
		for (Widget widget : contents) {	
			
			super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}

		
		//update back size
		this.setSizeAs(leftPadding+largestWidthOfStoredWidgets+rightPadding,bottomPadding+currentTotalWidgetHeight+topPadding);
		
		
	}
	

	@Override
	public void add(Widget widget) {
		super.add(widget);
		//resize
		this.setSizeAs(leftPadding+largestWidthOfStoredWidgets+rightPadding,bottomPadding+currentTotalWidgetHeight+topPadding);
	}

	

}
