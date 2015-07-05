package com.lostagain.nl.GWTish;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.GlowingSquareShader;

public class VerticalPanel extends Widget {

	Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
		
	static Material WhiteBackground = new Material("IconMaterial",
			 new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.99f),
			new GlowingSquareShader.GlowingSquareAttribute(3f,Color.BLACK,Color.WHITE,Color.BLACK));

	float spaceing = 0f;
	
	//current stats
	float currentTotalWidgetHeight = 0f;
	float currentLargestWidgetsWidth = 0f;
	
	//widget list
	ArrayList<Widget> contents = new ArrayList<Widget>();

	private Runnable updateContainerSize; 
	
	
	/**
	 * Creates a background and lets you position widgets vertical within it
	 * 
	 */
	public VerticalPanel() {
		super(10,10,WhiteBackground); //default size and background
		
		
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

		
		
		PosRotScale newLocation = new PosRotScale(newLocationX,-newLocationY,3); //hover above for now (3 is currently a bit arbirtary, guess we should make this a option in future)
		
		this.attachThis(widget, newLocation);

		currentTotalWidgetHeight=currentTotalWidgetHeight+height+spaceing;
				
		
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
		
		//regenerate list
		repositionWidgets(); //we can optimize if we only reposition after the one removed
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
	


	

}
