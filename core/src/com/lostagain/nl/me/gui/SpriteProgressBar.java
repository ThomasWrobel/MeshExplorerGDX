package com.lostagain.nl.me.gui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.lostagain.nl.DefaultStyles;
/**
 * A simple horizontal bar that expands to the right 
 * 
 * @author Tom
 *
 */
public class SpriteProgressBar extends WidgetGroup {

	final static String logstag = "ME.SpriteProgressBar";
	Label baroverlay;
	
	float totalWidth = 0;
	float currentPercentage = 0;
	public SpriteProgressBar(){

		LabelStyle baroverlaystyle = new LabelStyle(DefaultStyles.linkstyle.get(LabelStyle.class));		
		Color ColorM = new Color(Color.GREEN);
		ColorM.a=0.8f;
		baroverlaystyle.background = DefaultStyles.colors.newDrawable("white", ColorM);
		

		baroverlay = new Label("",baroverlaystyle);
		
		super.addActor(baroverlay);
		
		
		
		
	}
	@Override
	public void layout(){
		super.layout();
		
		totalWidth = super.getWidth();
		
		baroverlay.setHeight(super.getHeight());
		
		Gdx.app.log(logstag,"laying out SpriteProgressBar");
		
		
		
	}
	public void setPercentage(float percent){
		currentPercentage = percent;
		
		float filledWidth = (totalWidth / 100f)*currentPercentage;

		//Gdx.app.log(logstag,"currentPercentage="+currentPercentage+" filledWidth="+filledWidth);
		baroverlay.setWidth(filledWidth);
		baroverlay.validate();
		
	}
	
}
