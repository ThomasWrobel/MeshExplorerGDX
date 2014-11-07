package com.lostagain.nl.me.LocationGUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;

/** a link that represents a page on the computer  **/
public class InterfaceButton extends Label {
	
	
	public Boolean isVisible = true;
	
	public Boolean isDisabled = false;
	
	public InterfaceButton(String name,Boolean isVisible){				
		super(name,DefaultStyles.buttonstyle);
		this.isVisible=isVisible;
		
		//LabelStyle back = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));
		//back.fontColor = DefaultStyles.unlockedLabel;		
				
		//back.background = DefaultStyles.colors.newDrawable("white", Color.DARK_GRAY);
	//	back.font = DefaultStyles.linkstyle.getFont("default");
		
		
	//	super.setStyle(back);
		super.setColor( DefaultStyles.unlockedLabel);
		
		super.setAlignment(Align.center);
		
	}
	
	public void setDisabledStyle(){
		
		isDisabled = true;		
		super.setColor( DefaultStyles.lockedLabel);
		
		
	}
	
	public void setUpStyle(){

		isDisabled = false;	
		
		super.setColor( DefaultStyles.unlockedLabel);	
		
		
		
	}
		
	public void setDownStyle(){

		isDisabled = false;		
		super.setColor( DefaultStyles.labelpressed);			
		
	}
	
	public void setDownForABit(){

		setDownStyle();
		
		Timer.schedule(new Task() {
			
			@Override
			public void run() {
				setUpStyle();
			}
		}, 0.5f);
		
	}
	
			
}