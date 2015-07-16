package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.ModelManagment;

/**
 * A location hub is the center point of any location
 * It provides a way to unlock the location (if its locked)
 * It then generates other features for the locations as spokes around it when opened.
 * The center then acts as a infobox like feature
 * 
 * @author Tom
 *
 */
public class LocationHub extends MeshIcon {

	private SSSNode LocationsNode;

	//All features of this location
	HashMap<GenericMeshFeature,MeshIcon> HubsFeatures = new HashMap<GenericMeshFeature,MeshIcon>();

	private ConceptStoreObject linkedConceptDataStore;
	private AbilityStoreObject linkedAbilityDataStore;;
	
	public LocationHub(SSSNode locationsNode,Location location) {		
		super(IconType.LocationHub, location, getDefaultFeature(locationsNode));
		Color basicS = Color.YELLOW;
		basicS.a = 0.5f;
		super.setBackgroundColor(basicS); //temp
		LocationsNode = locationsNode;
		
	}

	/** returns either a InfoBox or a Lockscreen depending on if this location is locked or not **/
	private static GenericMeshFeature getDefaultFeature(SSSNode locationsNode) {
		
		//detect if a lock is present
		
		//if so make sure it isn't already unlocked
		
		//if unlocked we return a InfoBox for display
		String  title = locationsNode.getPLabel();
		String  info  = "URI:"+locationsNode.getPURI();
		if (title.length()>40){
			title=title.substring(0, 40);
		}

		if (info.length()>40){
			info=" ..."+info.substring(info.length()-40);
		}

		InfoBox locationCenterInformation = new InfoBox(title,info);
		
		
		return locationCenterInformation;
	}
	
	private void unlockLocation(){
		//hide the lockscreen
		
		//swap for infobox
		
	}
	
	private void generateLocationContents(){
		
		//first set our back color-------------------------------------------------
		ArrayList<Color> backcolours = DefaultStyles.getColorsFromNode(LocationsNode);				
		if (backcolours!=null){

			Gdx.app.log(logstag,"setting backcolor to first in :"+backcolours.toString());
			setBackgroundColour(backcolours.get(0));		
		}
		//-----------------------------------------------------------------------
		getContentOfMachine(LocationsNode);

		//first load the links visible to this one
		//getVisibleMachines(mycomputerdata);
		//make emails
		
		//linke emails to hub
		
		//make contents
		
		//link content to hub
		
		//make links
		
		//link links to hub
		
		
	}
	

	/** creates all the contents for this location but does not yet lay them out **/
	protected void populateContents(ArrayList<SSSNode> contentResults) {

		Gdx.app.log(logstag,"populateContents for "+LocationsNode);
		
		//clear existing features contents
		for (GenericMeshFeature feature : HubsFeatures.keySet()) {
			
			feature.clear();
			
		}
		
		int emails=0;
		int data=0;
		int abil=0;

		Gdx.app.log(logstag,"_____________contents:  "+contentResults.size());

		for (SSSNode sssNode : contentResults) {


			Gdx.app.log(logstag,"Adding :"+sssNode.getPLabel());
			
			if (sssNode.isOrHasParentClass(StaticSSSNodes.software.getPURI())){
				
				//place on right page depending on type
				if (sssNode.isOrHasParentClass(StaticSSSNodes.ability.getPURI())){
					
					addAbilityObjectFile(sssNode);
										
					abil++;
				} else {
					addOtherObjectFile(sssNode);
					data++;
				}
				
			}


			if (sssNode.isOrHasParentClass(StaticSSSNodes.messages.getPURI())){


				Gdx.app.log(logstag,"Adding email:"+sssNode.getPURI());
				
				//First we check if its got a language specified 
				
				//note still using this messy method
				//SSS really needs a neater way to get a nodes property rather then just its classes
				SSSNode writtenIn =null;
				
				HashSet<SSSNodesWithCommonProperty> propertysOfEmail = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(sssNode.getPURI());

				Gdx.app.log(logstag,"number of email props:"+propertysOfEmail.size());
				for (SSSNodesWithCommonProperty ep : propertysOfEmail) {
					if (ep.getCommonPrec() == StaticSSSNodes.writtenin){

						Gdx.app.log(logstag,"detected language spec");
						 writtenIn = ep.getCommonValue();

							Gdx.app.log(logstag,"detected language written in:"+writtenIn.getPLabel());
					}					
				}
				
				
				
				addEmailLocation(sssNode,writtenIn); //note; order cant be guaranteed yet
				
				
				emails++;
			}



		}



	}
	
	/**
	 * Layers all the known linked features out and draws lines from this hub to them
	 */
	private void layoutContents() {
		float total = HubsFeatures.size();
		float angleDistance = 90; //scale to total after testing

		
		Vector3 center = this.transState.position.cpy();
		Vector3 offset = new Vector3(0,300,0);	
		
		for (MeshIcon feature : HubsFeatures.values()) {
			
			offset.rotate(Vector3.Z, angleDistance);
			Vector3 newPosition =  new Vector3(center);	//the new position is the old one	
			newPosition.add(offset);
			Gdx.app.log(logstag,"adding feature at:"+newPosition);
			
			
			feature.setToPosition(newPosition);
			ModelManagment.addmodel(feature,ModelManagment.RenderOrder.zdecides);
			
			//link it to us
			this.addLineTo(feature);
			
			
		}
		
	}
	private void addEmailLocation(SSSNode sssNode, SSSNode writtenIn) {
		//new email location not implemented yet
		
	}

	private void addOtherObjectFile(SSSNode sssNode) {
	
		
		if (linkedConceptDataStore==null){
			Gdx.app.log(logstag,"making linkedConceptDataStore");
			
			linkedConceptDataStore = new ConceptStoreObject(this); //create a new data store object linked to this location
			MeshIcon newIcon = new MeshIcon(IconType.ConceptStore,this.parentLocation, linkedConceptDataStore);
			
			HubsFeatures.put(linkedConceptDataStore,newIcon);

			Gdx.app.log(logstag,"HubsFeatures:"+HubsFeatures.values());
		}
		
		
		//add the object to the store
		ConceptObject newConceptObject = new ConceptObject(sssNode);
		linkedConceptDataStore.addConceptObject(newConceptObject);
		
	}

	
	private void addAbilityObjectFile(SSSNode sssNode) {

		if (linkedAbilityDataStore==null){
			Gdx.app.log(logstag,"making linkedAbilityDataStore");
			
			linkedAbilityDataStore = new AbilityStoreObject(this); //create a new data store object linked to this location
			final MeshIcon newIcon = new MeshIcon(IconType.AbilityStore, parentLocation, linkedAbilityDataStore);
			
			
			linkedAbilityDataStore.addOnSizeChangeHandler(new Runnable(){
				@Override
				public void run() {
					newIcon.refreshAssociatedFeature();
				}
			});
			
			
			HubsFeatures.put(linkedAbilityDataStore,newIcon);

			Gdx.app.log(logstag,"HubsFeatures:"+HubsFeatures.values());
		}
		
		
		//add the object to the store
		ConceptObject newConceptObject = new ConceptObject(sssNode);
		linkedAbilityDataStore.addConceptObject(newConceptObject);
	}
	

	/** gets the content of the supplied location **/
	public void getContentOfMachine(SSSNode tothisnode){

		Gdx.app.log(logstag,"populate contents of:"+tothisnode);
		Gdx.app.log(logstag,"SSSNodesWithCommonProperty with: "+StaticSSSNodes.isOn.PURI+","+tothisnode.PURI);

		SSSNodesWithCommonProperty contentOfMACHINE =  SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.isOn, tothisnode); //.getAllNodesInSet(callback);

		//Query realQuery = new Query(" ison=alicespc ");

		DoSomethingWithNodesRunnable callback2 = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> contents, boolean invert) {

				if (contents.size()>0){

					//remove duplicates by using an HashSet
					// add elements to al, including duplicates
					HashSet<SSSNode> hs = new HashSet<SSSNode>();
					hs.addAll(contents);
					contents.clear();
					contents.addAll(hs);

					Gdx.app.log(logstag,"generating contents");
					populateContents(contents);
					//trigger layout
					layoutContents();


				}

			}

		

		};

		//  QueryEngine.processQuery(realQuery, false, null, callback2);

		if (contentOfMACHINE!=null){
			Gdx.app.log(logstag,"getting contents:"+contentOfMACHINE.isLoaded);
			Gdx.app.log(logstag,"getting contents:"+contentOfMACHINE.getCommonPrec()+":"+contentOfMACHINE.getCommonValue());
			Gdx.app.log(logstag,"getting contents:"+contentOfMACHINE.getSourceFiles().toString());
			Gdx.app.log(logstag,"getting contents:"+contentOfMACHINE.getLefttoLoad() );

			contentOfMACHINE.getAllNodesInSet(callback2);
		}


	}
	
	
	public void setBackgroundColour(Color col) {
		
		if (col==null){
			col=Color.CLEAR; //default
		}
				
		Gdx.app.log(logstag,"setting back color to:"+col.toString());
			
		this.setBackgroundColour(col);
		
		
        
	}

	@Override
	public void open() {
		super.open();
		
		//first time we open we generate our contents if we are unlocked and it hasnt been done yet
		if (HubsFeatures.size()==0){
			this.generateLocationContents();
		}
		
	}
	

}
