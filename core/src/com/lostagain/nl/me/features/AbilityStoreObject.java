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
		super.setBackgroundColor(Color.ORANGE);
		
		TitleLabel.setText("Ability Store:");
	}

}
