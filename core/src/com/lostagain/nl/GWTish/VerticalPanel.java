package com.lostagain.nl.GWTish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.me.newmovements.PosRotScale;

public class VerticalPanel extends Widget {

	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
		
	

	float spaceing = 0f;
	
	//current stats
	float currentTotalWidgetHeight   = 0f;
	float currentLargestWidgetsWidth = 0f;
	
	//widget list
	ArrayList<Widget> contents = new ArrayList<Widget>();

	private Runnable updateContainerSize; 
	
	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public VerticalPanel() {
		super(10,10); //default size and background
		
		
		//this will be given to child widgets to inform the parent of size changes
		updateContainerSize = new Runnable(){
			@Override
			public void run() {
				//reposition this panels widgets
				repositionWidgets();
			}			
		};
		
				
	}

	/**
	 * Adds a widget below the current ones centralized.
	 * 
	 * @param widget
	 */
	public void add(Widget widget){
		
		//add to the widget list
		contents.add(widget);
		
		internalAdd(widget);

		//resize
		this.setSizeAs(currentLargestWidgetsWidth,currentTotalWidgetHeight);
	}

	/**
	 * Attaches the widget at the end of the current ones without resizing or adding to lists
	 * @param widget
	 */
	private void internalAdd(Widget widget) {
		//get size of widget
		BoundingBox size = widget.getLocalBoundingBox();
		
		float height = size.getHeight();
		float width  = size.getWidth();
		
		if (width>currentLargestWidgetsWidth){
			currentLargestWidgetsWidth=width;
		}

		
		//currently set to position on the right hand side
		float newLocationX = 0;
		float newLocationY = currentTotalWidgetHeight; //under the last widget
		
		Gdx.app.log(logstag,"______________placing new widget at: "+newLocationY+" its height is:"+height);
	
		PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,3); //hover above for now (3 is currently a bit arbitrary, guess we should make this a option in future)
		
		this.attachThis(widget, newLocation);

		currentTotalWidgetHeight=currentTotalWidgetHeight+height+spaceing;
				
		//set widget to inherit visibility
		widget.setInheritedVisibility(true);
		
		
		//Now we need to register handlers so we can reform stuff if the size of anything inside changes
		widget.addOnSizeChangeHandler(updateContainerSize);
		
	}
	

	/**
	 * removes a widget from this panel and hides it.
	 * Note; The widget will still exist if you wish to unhide it, it just wont be attached to this panel anymore
	 * @param widget
	 */
	public void remove(Widget widget){
		contents.remove(widget);
		widget.hide();
		widget.removeOnSizeChangeHandler(updateContainerSize);
		this.removeAttachment(widget);
		
		//regenerate list
		repositionWidgets(); //we can optimize if we only reposition after the one removed
	}

	
	public void clear(){
		
		for (Widget widget : contents) {
			widget.hide();
			widget.removeOnSizeChangeHandler(updateContainerSize);			
			this.removeAttachment(widget);
		}
		contents.clear();
	}
	/**
	 * Sets the spacing between elements vertically
	 * @param f
	 */
	public void setSpaceing(float f) {
		this.spaceing = f;
		
		repositionWidgets();
		
	}
	

	private void repositionWidgets() {
		
		//simply clear and re-add them all
		
		//reset  stats
		currentTotalWidgetHeight = 0f;
		currentLargestWidgetsWidth = 0f;
				
		for (Widget widget : contents) {	
			
			super.removeAttachment(widget); //remove			
			internalAdd(widget); //re add
			
		}
		
		//update back size
		this.setSizeAs(currentLargestWidgetsWidth,currentTotalWidgetHeight);
		
		
	}
	
	@Override
	public void setOpacity(float opacity) {		
		super.setOpacity(opacity);
		//repeat for our attached widgets
		for (Widget widget : contents) {
			widget.setOpacity(opacity);			
		}
		
	}

	

}
