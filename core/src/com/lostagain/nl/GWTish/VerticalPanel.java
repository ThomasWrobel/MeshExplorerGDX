package com.lostagain.nl.GWTish;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


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

	Vector3 getNextPosition(float incomingWidth,float incomingHeight,boolean updateHeight,int index){

		float newLocationX = 0;	
		
		if (DefaultAlignmentinCell == HorizontalAlignment.Center){
			float maxW= (largestWidthOfStoredWidgets);
			newLocationX =  (maxW - incomingWidth)/2;
		}

		float newLocationY = currentTotalWidgetHeight; //under the last widget

		if (updateHeight){
			currentTotalWidgetHeight=currentTotalWidgetHeight+incomingHeight+spaceing; //should spacing be scaled?
		}
		
		
			
			Gdx.app.log(logstag,index+" adding incomingHeight: "+incomingHeight+" total="+currentTotalWidgetHeight);
			
		

		return new Vector3(leftPadding+newLocationX,topPadding+newLocationY,3f);


	}

	/**
	 * Refreshes the position of all widgets
	 * recalculateLargestWidgets(); should be run first
	 */
	void repositionWidgets() {
		Gdx.app.log(logstag,"Reposition "+contents.size()+" widgets in ");
		//simply clear and re-add them all

		//reset  stats
		currentTotalWidgetHeight = 0f;
		//	largestWidthOfStoredWidgets = 0f;
		//	largestHeightOfStoredWidgets = 0f;

		for (Widget widget : contents) {
			//super.removeAttachment(widget); //remove	
			internalAdd(widget); //re add only when needed

		}
		
		Gdx.app.log(logstag,"new size:"+largestWidthOfStoredWidgets+","+currentTotalWidgetHeight);


	}




	//@Override
	//public boolean add(Widget widget) {
	//	super.add(widget);

	//}




	@Override
	void sizeToFitContents() {
		setSizeAs(leftPadding+largestWidthOfStoredWidgets+rightPadding,bottomPadding+currentTotalWidgetHeight+topPadding);
	}


}
