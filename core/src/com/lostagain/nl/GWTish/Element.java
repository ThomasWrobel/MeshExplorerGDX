package com.lostagain.nl.GWTish;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

/**
 * This will approximate a similar function as GWTs Element class does
 * 
 * @author Tom
 *
 */
public class Element extends AnimatableModelInstance {
	
	final static String logstag = "GWTish.Element";
	Style objectsStyle;
	
	public Element(Model model) {
		super(model);
	}
	
	
	/**
	 *  returns the style object which will controll a small fraction of
	 *  the functionality that true GWT styles do.
	 *  Specifically this is currently for a few style options on text labels.
	 * @return 
	 */
	public Style getStyle() {
		return objectsStyle;
		
	}


	public void setStyle(Material material) {
		objectsStyle=new Style(material);
	}
}
