package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * A deck panel is a stack of other panels ontop of eachother
 * You can choose to either display one at a time (like GWTs) or multiple at once.
 * 
 * 
 * @author Tom
 *
 */
public class DeckPanel extends ComplexPanel {

	public DeckPanel(float sizeX, float sizeY) {
		super(sizeX, sizeY);
		// TODO Auto-generated constructor stub
	}
	
	//@Override
	//public void add(Widget widget) {
	//	super.add(widget);
		//resize
	//	sizeToFitContents();
	//}

	/**
	 * even though we have all the widgets ontop of eachother we need to reposition them if our size changes in order
	 * to keep them centralized
	 */
	@Override	
	void repositionWidgets() {
		
		
		//simply clear and re-add them all
		
		//reset  stats
		
		//currentTotalWidgetWidth = 0f;
		//largestWidthOfStoredWidgets = 0f;
		//largestHeightOfStoredWidgets = 0f;
		
				
		for (Widget widget : contents) {	
			
			//super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}
		
		sizeToFitContents();
		/*
		float cy  = largestHeightOfStoredWidgets/2;
		
		if (largestHeightOfStoredWidgets < this.MinSizY){
			cy  = MinSizY/2;
		}
		
		float cx  = largestWidthOfStoredWidgets/2;
		
		if (largestWidthOfStoredWidgets < this.MinSizX){
			cx  = MinSizX/2;
		}
				
		
		for (Widget widget : contents) {
			
			
			
			this.updateAtachment(widget,new PosRotScale(cx ,cy,3f));
		}*/

	}

	public void showWidget(int index) {
		showWidget(index,true);
	}
	/**
	 * 
	 * @param index
	 * @param hideOthers - hide other widgets
	 */
	public void showWidget(int index,boolean hideOthers) {

		Widget selected= contents.get(index);
		selected.show();
		
		if (hideOthers){
			for (Widget widget : contents) {
				if (widget!=selected){
					widget.hide();
				}

			}
		}
	}

	
	@Override
	Vector3 getNextPosition(float incomingWidth, float incomingHeight, boolean b,int index) {
		float cy  = largestHeightOfStoredWidgets/2;
		
		if (largestHeightOfStoredWidgets < this.MinSizY){
			cy  = MinSizY/2;
		}
		
		float cx  = largestWidthOfStoredWidgets/2;
		
		if (largestWidthOfStoredWidgets < this.MinSizX){
			cx  = MinSizX/2;
		}
		
		return new Vector3(cx-(incomingWidth/2),cy-(incomingHeight/2),(5f+10f*index)); //widgets are stacked 5 apart vertically
	}

	@Override
	void sizeToFitContents() {
		this.setSizeAs(leftPadding+largestWidthOfStoredWidgets+rightPadding,bottomPadding+this.largestHeightOfStoredWidgets+topPadding);

	}

}
