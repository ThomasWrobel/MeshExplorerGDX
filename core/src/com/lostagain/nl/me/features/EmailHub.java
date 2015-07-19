package com.lostagain.nl.me.features;

import com.badlogic.gdx.graphics.Color;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.me.locationFeatures.Location;

/**
 * A centerpoint for emails, with individual emails appearing in spokes around it.
 * In future we might have sub-hubs for email threads? or each email leads to another one?
 * 
 * idea;
 * The email hub knows the direction from which its linked to its parenthub
 * it then uses the opersite direction (ie, 180 around) as the default direction for emails, spacing them
 * evenly left and right
 * Each email in turn, links to its own reply emails in the same way.
 * 
 * @author Tom
 *
 */
public class EmailHub extends MeshIcon {
float totalEmails = 0;

	public EmailHub(LocationHub parentHub) {
		super(IconType.EmailHub, parentHub.parentLocation, generateFeature());
		
		super.setBackgroundColor(new Color(0.3f,0.3f,0.8f,0.7f));
		
		
	}

	private static GenericMeshFeature generateFeature() {
		float num = 0;
		return new InfoBox("Email Data","Emails:"+num,"");
	}
	

	public void addEmailSource(SSSNode sssNode, SSSNode writtenIn) {
		//make new email page on a spoke from this one
		
		//increase total email count
		totalEmails++;
	}

}
