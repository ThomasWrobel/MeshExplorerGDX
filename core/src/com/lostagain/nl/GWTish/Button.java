package com.lostagain.nl.GWTish;

import com.badlogic.gdx.graphics.Color;

/**
 * currently just a widget that has a runnable that fires when clicked.
 * This will naturally change a lot, but having a basic button to test stuff is usefull
 * 
 * 
 * @author Tom
 *
 */
public class Button extends Widget {
	
	private Runnable onClick;
	

	public Button(float sizeX, float sizyY, Runnable onClick) {
		super(150, 35);
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.GRAY);
		this.onClick=onClick;
	}

	@Override
	public void fireTouchDown() {
		super.fireTouchDown();
		super.getStyle().setBackgroundColor(Color.RED);
	}
	
	@Override
	public void fireTouchUp() {
		super.fireTouchDown();
		super.getStyle().setBackgroundColor(Color.GRAY);
		if (onClick!=null){
			onClick.run();
		}
		
	}
}
