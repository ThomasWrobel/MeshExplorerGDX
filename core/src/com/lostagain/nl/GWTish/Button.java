package com.lostagain.nl.GWTish;

import com.badlogic.gdx.graphics.Color;
import com.lostagain.nl.GWTish.Element.EventType;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * currently just a widget that has a runnable that fires when clicked.
 * This will naturally change a lot, but having a basic button to test stuff is useful
 * 
 * 
 * @author Tom
 *
 */
public class Button extends Widget {
	
	//private ClickHandler onClick;
	
	
	/**
	 * creates a button with specified size and runnable when clicked
	 * @param sizeX
	 * @param sizyY
	 * @param onClick
	 */
	public Button(float sizeX, float sizyY, ClickHandler onClick) {
		super(sizeX, sizyY);
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.GRAY);
		
	//	this.onClick=onClick;	
		if (onClick!=null){
			addClickHandler(onClick);
		}
	}

	
	protected Label Caption;

	public Button(String caption) {
		this(caption, null);
	}
	/**
	 * 
	 * @param caption
	 * @param onClick
	 */
	public Button(String caption, ClickHandler onClick) {
		super(1, 1); //Arbitrary, we have to resize after the font is made
		
		setupCaption(caption);
		
		//for testing
		//in future we need a way to get a unique name for the zIndex group
		//Also change the set function to set everything attached to Zindex +1 as a option
		setZIndex(1, "testbutton");
		Caption.setZIndex(2, "testbutton");
		
		
		this.setAsHitable(true);
		super.getStyle().setBackgroundColor(Color.GRAY);
		
		if (onClick!=null){
			addClickHandler(onClick);
		}
		
	}
	private void setupCaption(String caption) {
		this.Caption = new Label(caption);
		Caption.getStyle().clearBackgroundColor();
		Caption.getStyle().clearBorderColor();
		this.setSizeAs(Caption.getWidth()+10, Caption.getHeight()+10);
		
		PosRotScale captionPosition = new PosRotScale(5f,5f,3f);
		
		this.attachThis(Caption, captionPosition);
		
		
	}

	


	public void click(){
		fireHandlersForType(EventType.ClickEvent);
	}
	
	public void setText(String text) {
		if (Caption!=null){
			Caption.setText(text);
		} else {
			setupCaption(text);
		}
		
	}
	
	/*
	
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
		
	}*/
}
