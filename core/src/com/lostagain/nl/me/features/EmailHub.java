package com.lostagain.nl.me.features;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.IconType;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;

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
	int totalEmails = 0;
	
	private ArrayList<MeshIcon> DirectEmails = new  ArrayList<MeshIcon>();
	LocationHub parentHub;
	
	public EmailHub(LocationHub parentHub) {
		super(IconType.EmailHub, parentHub.parentLocation, generateFeature());

		super.setBackgroundColour(new Color(0.3f,0.3f,0.8f,0.7f));
		this.parentHub=parentHub;


	}

	private static GenericMeshFeature generateFeature() {

		return new InfoBox("Email Data","Emails:0","");
	}


	public void addEmailSource(SSSNode sssNode, SSSNode writtenIn) {

		//make new email page on a spoke from this one
		Email    emailPage = new Email(sssNode, writtenIn);
		MeshIcon emailIcon = new MeshIcon(IconType.Email,this.parentLocation, emailPage);
		ModelManagment.addmodel(emailIcon, RenderOrder.zdecides);
		
		emailIcon.hide();		
		DirectEmails.add(emailIcon);
		
		


		//increase total email count
		totalEmails++;
		refreshStats();
	}

	private void refreshStats() {
		InfoBox assFeature = (InfoBox)assocatiedFeature;
		assFeature.setSubtitle("Num Of Emails:"+totalEmails);

	}

	private float getCurrentAngleToHub() {
		return 180+this.getAngleTo(parentHub,Vector3.Y).getAngle();
	}

	
	public void layout() {
		//work out position to place it
		float ang = getCurrentAngleToHub();		
		
		Gdx.app.log(logstag,"angle to hub = "+ang);
		float totalSpread = 90;
		float spaceing = totalSpread/DirectEmails.size();		
		float minAngle = ang - (totalSpread/2);
				
		float distance = 200;
		
		for (MeshIcon icon : DirectEmails) {

			//temp; we place it at that angle (in future we space evenly around it as more email's are added
			//(ie, make first, then layout)
			Vector3 pos = this.transState.position.cpy();
			Vector3 newPosition  = new Vector3(pos);
			Vector3 displacement = new Vector3(0,distance,0);
			
			displacement.rotate(Vector3.Z, ang);

			newPosition.add(displacement);

			icon.setToPosition(newPosition);
			Gdx.app.log(logstag," pos = "+newPosition);
			icon.show();
			
			addLineTo(icon);
			
			
		}
		
	}

	//layout when opened
	@Override
	public void open() {
		super.open();
		layout();
	}

	@Override
	public void close() {
		super.close();
		HideEmails();
	}

	private void HideEmails() {

		for (MeshIcon icon : DirectEmails) {
			icon.hide();
			
			if (linkedIcons.get(icon)!=null){
				linkedIcons.get(icon).hide();
				//remove line to;
				linkedIcons.remove(icon);
			}
			
			
		}
		
		//NOTE: when its shown again we lose the text
		
	}
	
	

}
