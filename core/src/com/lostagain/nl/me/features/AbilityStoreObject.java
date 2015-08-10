package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;

/**
 * Same as a concept object store, just coloured differently
 * @author Tom
 *
 */
public class AbilityStoreObject extends ConceptStoreObject {

	public AbilityStoreObject(LocationHub locationHub) {
		super(locationHub);
		
		//super.getStyle().setBackgroundColor(new Color(0.8f,0.6f,0.1f,0.7f));
		
		super.getStyle().clearBackgroundColor();
		super.getStyle().clearBorderColor();
		
		TitleLabel.setText("Ability Store:");
	}

}
