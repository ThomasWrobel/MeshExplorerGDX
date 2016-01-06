package com.lostagain.nl.me.features;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.features.MeshIcon.IconType;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.me.models.GWTishModelManagement.RenderOrder;

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
public class MessageHub extends MeshIcon {

	final static String logstag = "ME.MessageHub";
	int totalEmails = 0;

	private ArrayList<MeshIcon> DirectEmails = new  ArrayList<MeshIcon>();

	LocationHub parentHub;

	public MessageHub(LocationHub parentHub) {
		super(IconType.MessageHub, parentHub.parentLocation, generateFeature());

		//	super.setBackgroundColour(new Color(0.2f,0.2f,0.7f,0.88f));
		this.parentHub=parentHub;

		OpenHeightDisplacement = 2f; //we dont raise as high as other things. (this way emails go ontop of the hub even when the hub is open)

		super.setZIndex(150,super.getUniqueName()); 

	}

	private static GenericMeshFeature generateFeature() {

		return new InfoBox("Message Data","Messages:0","");
	}

	ArrayList<SSSNode> StoredEmails = new ArrayList<SSSNode>();

	public void addEmailSource(SSSNode sssNode, SSSNode writtenIn) {

		//ignore if already displayed
		if (StoredEmails.contains(sssNode)){
			Gdx.app.log(logstag,"already displaying email:"+sssNode);			
			return;
		}

		//get email's title
		//This is the label of the SSSNode, cropped to remove any url locations
		//(which I don't think labels should have anyway?)		
		String emailsTitle = sssNode.getPLabel();
		emailsTitle = tidyEmailTitle(emailsTitle);		
		
		//make new email page on a spoke from this one
		MessagePage    emailPage = new MessagePage(this,sssNode, writtenIn);


		MeshIcon emailIcon = new MeshIcon(IconType.Message,emailsTitle,150,50,parentLocation, emailPage);
		emailIcon.OpenHeightDisplacement = 30f; //bit higher then normal

		emailPage.setParentMeshIcon(emailIcon);
		GWTishModelManagement.addmodel(emailIcon);//, RenderOrder.zdecides);

		emailIcon.hide();		
		
		DirectEmails.add(emailIcon);
		StoredEmails.add(sssNode);


		//increase total email count
		totalEmails++;
		refreshStats();
	}

	private String tidyEmailTitle(String emailsTitle) {
		int slashloc = Math.max(emailsTitle.indexOf("\\"), emailsTitle.indexOf("/"));
		if (slashloc>-1){
			emailsTitle = emailsTitle.substring(slashloc+1);				
		}		
		//also remove any extension (like .txt)
		int dotloc = emailsTitle.indexOf(".");
		if (dotloc>-1){
			emailsTitle = emailsTitle.substring(0,dotloc);				
		}
		return emailsTitle;
	}

	private void refreshStats() {
		InfoBox assFeature = (InfoBox)assocatiedFeature;
		assFeature.setSubtitle("Num Of Messages:"+totalEmails);

	}

	private float getCurrentAngleToHub() {


		Quaternion  angle =     getAngleTo(parentHub,Vector3.Y);
		float   ang_value = (angle.getRoll()+180);
		Gdx.app.log(logstag,"angle from hub is:"+ang_value);


		return ang_value;
	}


	public void layout() {

		//work out position to place it
		float ang = getCurrentAngleToHub();		

		Gdx.app.log(logstag,"Angle to hub = "+ang);
		float total_emails = DirectEmails.size();

		Gdx.app.log(logstag,"total_emails = "+total_emails);

		float spaceing = 40;		
		float totalSpread = (total_emails*spaceing)-spaceing; //30 degrees each
		float minAngle = ang - (totalSpread/2);

		float distance = 200;
		int num=0;
		for (MeshIcon icon : DirectEmails) {

			//temp; we place it at that angle (in future we space evenly around it as more email's are added
			//(ie, make first, then layout)
			Vector3 pos          = this.transState.position.cpy();
			Vector3 newPosition  = new Vector3(pos);
			Vector3 displacement = new Vector3(0,distance,0);

			float cangle = minAngle + (spaceing*num);
			displacement.rotate(Vector3.Z, cangle);

			newPosition.add(displacement);

			icon.setToPosition(newPosition);
			Gdx.app.log(logstag," pos = "+newPosition);
			icon.show();

			addLineTo(icon);
			num++;

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
