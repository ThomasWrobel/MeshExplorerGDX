package com.lostagain.nl.GWTish;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * A cell panel is the parent of both VerticalPanel and HorizontalPanel
 * it handles a collection of widgets inside itself, laid out in some fashion
 * 
 * @author Tom
 *
 */
public abstract class CellPanel extends ComplexPanel {

	protected float spaceing = 0f;

	
	public CellPanel(int x, int y) {
		super(x,y);
		
	
	}

	/**
	 * Sets the spacing between elements vertically
	 * @param f
	 */
	public void setSpaceing(float f) {
		this.spaceing = f;
		
		repositionWidgets();
		
	}


	//abstract Vector3 getNextPosition(float width, float height, boolean assumeNewWidgetWillBeAdded);


}
