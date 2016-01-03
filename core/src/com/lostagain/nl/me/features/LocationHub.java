package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.GWTish.VerticalPanel;
import com.lostagain.nl.me.locationFeatures.EmailScreen;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.uti.HSLColor;

/**
 * A location hub is the center point of any location
 * 
 * It provides a way to unlock the location (if its locked)
 * It then generates other features for the locations as spokes around it when opened.
 * The center then acts as a infobox like feature
 * 
 * The location also provides a means to refresh its children, as its more efficient to refresh them all at once.
 * 
 * @author Tom
 *
 */
public class LocationHub extends MeshIcon {
	final static String logstag = "ME.LocationHub";
	
	public SSSNode LocationsNode;

	//All features of this location stored in this array
	HashMap<GenericMeshFeature,MeshIcon> HubsFeatures = new HashMap<GenericMeshFeature,MeshIcon>();

	
	//All the individual features we can have (stored above)
	private ConceptStoreObject linkedConceptDataStore;
	private AbilityStoreObject linkedAbilityDataStore;
	private EmailHub           linkedEmailHub;
	private LinkStoreObject    linkedLinkStore;
	//just for player
	private AbilityInstaller linkedAbilityInstaller;
	
	
	//if we should refresh the contents next time we are opened
	boolean refreshOnOpen = true;

	/**
	 * Are we currently locked
	 **/
	boolean locked=false;
	
	/**	 
	 * @param locationsNode
	 * @param location
	 **/	
	public LocationHub(SSSNode locationsNode,Location location) {

		super(IconType.LocationHub,getHubTitle(locationsNode), defaultIconWidth, defaultIconHeight, location, createDefaultFeature(locationsNode));
		//Color basicS = new Color(0.0f,0.3f,0.0f, 0.7f);	
		
	//	HSLColor darkgreen = new HSLColor(0.33f,1.0f,0.1f,0.7f);
		
		//basicS.a = 0.5f;		
		//super.setBackgroundColour(darkgreen.toRGB()); //temp
		//Color basicBorder = new Color(0.3f,1.0f,0.3f, 1.0f);

		HSLColor basicBorder = new HSLColor(0.33f,1.0f,0.6f,1.0f);
		
		super.getStyle().setBorderColor(basicBorder.toRGB());
		super.getStyle().setBorderWidth(0.5f);
		
		LocationsNode = locationsNode;
		super.setZIndex(150,super.getUniqueName()); 
		
		

	}

	private static String getHubTitle(SSSNode node) {

		return node.getPLabel();
	}

	/** returns either a InfoBox or a Lockscreen depending on if this location is locked or not **/
	private static GenericMeshFeature createDefaultFeature(SSSNode locationsNode) {

		//detect if a lock is present

		//if so make sure it isn't already unlocked

		//if unlocked we return a InfoBox for display

		SSSNodesWithCommonProperty DiscriptionSet = SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.DescriptionOf,locationsNode);
		Gdx.app.log(logstag,"Getting discription for location:"+locationsNode.getPLabel());

		String Discription = "(No Details)";
		if (DiscriptionSet!=null)
		{
			Discription = DiscriptionSet.get(0).getPLabel();
		}


		String  title = locationsNode.getPLabel();
		String  uri  = "URI:"+locationsNode.getPURI();

		if (uri.length()>40){
			uri=" ..."+uri.substring(uri.length()-40);
		}


		if (title.length()>40){
			title=title.substring(0, 40);
		}

		InfoBox locationCenterInformation = new InfoBox(title,uri,Discription);


		return locationCenterInformation;
	}

	
	void setAsLocked(DataRequestScreen lockscreen){
		locked = true;
	
				
		//set new one
		this.setAssociatedFeature(lockscreen);
		super.setBackgroundColour(Color.RED);
		
		
		
		
		
	}
	
	void setAsUnLocked(){
		
		locked = false;
		super.setBackgroundColour(Color.GREEN);
		
		//close and change contents after waiting a short delay
		Timer.schedule(new Task() {			
			@Override
			public void run() {
				
				LocationHub.this.close();
				
				//swap for default feature
				LocationHub.this.setAssociatedFeatureToDefault();

			}
		}, 1.500f); //put nice unlocking animation in future
		
	}

	public void reGenerateLocationContents(){
		Gdx.app.log(logstag,"updating home location");
		generateLocationContents(); //needs testing if this is all we need to do
	}
	
	
	private void generateLocationContents(){

		//first set our back color-------------------------------------------------
		ArrayList<Color> backcolours = DefaultStyles.getColorsFromNode(LocationsNode);				
		if (backcolours!=null){

			Gdx.app.log(logstag,"setting backcolor to first in :"+backcolours.toString());
			setBackgroundColour(backcolours.get(0));		
		}
		
		//if we are on the players location add the installer screen
		if (parentLocation==PlayersData.homeLoc){
			this.addAbilityInstaller();
		}
		
		//-----------------------------------------------------------------------
		getContentOfMachine(LocationsNode); //objects,ability's,emails

		getVisibleMachines(LocationsNode); //links
				
		
		layoutContents();

		//store as updated
		refreshOnOpen=false;


	}

	/** gets the locations visible to the supplied location **/
	private void getVisibleMachines(SSSNode tothisnode){

		//String allNodes = SSSNode.getAllKnownNodes().toString();



		Gdx.app.log(logstag,"-------------------------------X--------------------------------===================----------------");
		//SSSNodesWithCommonProperty VisibleMachines =  SSSNodesWithCommonProperty.getSetFor(visibletest, everyonetest); //.getAllNodesInSet(callback);

		String thisPURI = tothisnode.getPURI(); ///"C:\\TomsProjects\\MeshExplorer\\bin/semantics/DefaultOntology.n3#bobspc";

		Query realQuery = new Query("(me:connectedto=me:everyone)||(me:connectedto=\""+thisPURI+"\")");
		//E:\Game projects\MeshExplorerGDX\desktop\semantics\TomsNetwork.ntlist#BobsOutpost

		//	Gdx.app.log(logstag,"----prefix tests:"+RawQueryUtilities.getPrefixs());
		//	Gdx.app.log(logstag,"----all nodes:"+SSSNode.getAllKnownNodes());

		SSSNode.setExtendedDebug(true);

		Gdx.app.log(logstag,"----me:connectedto test:"+SSSNode.getNodeByUri("me:connectedto").toString());
		Gdx.app.log(logstag,"----me:everyone test:"   +SSSNode.getNodeByUri("me:everyone").toString());
		Gdx.app.log(logstag,"----thisPURI  test:"+thisPURI);	

		Gdx.app.log(logstag,"-------"+realQuery.allUsedNodes());


		//populate when its retrieved
		DoSomethingWithNodesRunnable callback = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> computerNodesFound, boolean invert) {



				Gdx.app.log(logstag,"populate connectedto Computers");
				populateVisibleComputers(computerNodesFound);


			}

		};

		QueryEngine.processQuery(realQuery, false, null, callback);



	}

	protected void populateVisibleComputers(ArrayList<SSSNode> computerNodes) {
		if (linkedLinkStore==null){
			Gdx.app.log(logstag,"making linkedLinkStore");

			linkedLinkStore = new LinkStoreObject(this); //create a new data store object linked to this location
			final MeshIcon newIcon = new MeshIcon(IconType.LinkStore,this.parentLocation, linkedLinkStore);
			//linkedLinkStore.addOnSizeChangeHandler(new Runnable(){
			//	@Override
			//	public void run() {
			//		newIcon.refreshAssociatedFeature();
			//	}
			//});

			HubsFeatures.put(linkedLinkStore,newIcon);
			newIcon.setZIndex(150,super.getUniqueName()); 
		} else {
			linkedLinkStore.clearLinks();
		}

		Gdx.app.log(logstag,"computers visible to this = "+computerNodes.size());


		for (SSSNode sssNode : computerNodes) {

			//add to link list if its not the current pc
			if (sssNode!=this.LocationsNode){			
				linkedLinkStore.addLink(sssNode);
			}

		}


	}

	/** creates all the contents for this location but does not yet lay them out **/
	protected void populateContents(ArrayList<SSSNode> contentResults) {

		Gdx.app.log(logstag,"populateContents for "+LocationsNode);

		//clear existing features contents (dont do this anymore as features should be able to update the changes, not clear and recreate)
		//for (GenericMeshFeature feature : HubsFeatures.keySet()) {
		//	feature.clear();
		//}

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



				addEmailSource(sssNode,writtenIn); //note; order cant be guaranteed yet


				emails++;
			}



		}



	}

	/**
	 * Layers all the known linked features out and draws lines from this hub to them
	 */
	private void layoutContents() {
		clearAllLinkLines(); //clears existing link lines

		float total = HubsFeatures.size();
		float angleDistance = 360/total; //scale to total after testing


		Vector3 center = this.transState.position.cpy();
		Vector3 offset = new Vector3(0,300,0);	
		
		Gdx.app.log(logstag,"HubsFeatures total= "+HubsFeatures.size());
		
		for (MeshIcon feature : HubsFeatures.values()) {

			offset.rotate(Vector3.Z, angleDistance);
			Vector3 newPosition =  new Vector3(center);	//the new position is the old one	
			newPosition.add(offset);
			Gdx.app.log(logstag,"Adding "+feature.thisIconsType+" feature at:"+newPosition);
		

			feature.setToPosition(newPosition);
			GWTishModelManagement.addmodel(feature);//,GWTishModelManagement.RenderOrder.zdecides); //should only add if not already on scene
			
			//link it to us
			this.addLineTo(feature);			

		}


		////if there is emails fire a layout on it as it will need to lay out its subcontent
		//if (linkedEmailHub!=null){
		//	linkedEmailHub.layout();
		//	
		//}

	}
	private void addEmailSource(SSSNode sssNode, SSSNode writtenIn) {
		//new email location not implemented yet
		if (linkedEmailHub==null){
			Gdx.app.log(logstag,"making emailhub");

			linkedEmailHub = new EmailHub(this); //create a new data store object linked to this location

			HubsFeatures.put(linkedEmailHub.assocatiedFeature,linkedEmailHub);

			Gdx.app.log(logstag,"HubsFeatures:"+HubsFeatures.values());


		}

		linkedEmailHub.addEmailSource(sssNode,writtenIn);

	}

	private void addOtherObjectFile(SSSNode sssNode) {


		if (linkedConceptDataStore==null){
			Gdx.app.log(logstag,"making linkedConceptDataStore");

			linkedConceptDataStore = new ConceptStoreObject(this); //create a new data store object linked to this location
			MeshIcon newIcon = new MeshIcon(IconType.ConceptStore,this.parentLocation, linkedConceptDataStore);

			HubsFeatures.put(linkedConceptDataStore,newIcon);

			newIcon.setZIndex(150,super.getUniqueName()); 
			
			Gdx.app.log(logstag,"HubsFeatures:"+HubsFeatures.values());
		}


		//add the object to the store
		ConceptObject newConceptObject = new ConceptObject(sssNode);
		linkedConceptDataStore.addConceptObject(newConceptObject);

	}

	/**
	 * currently only the players own location has one of these.
	 * new ability's can be dragged onto it to upgrade/install them
	 * 
	 */
	private void addAbilityInstaller(){
		
		// 
		if (linkedAbilityInstaller==null){
			Gdx.app.log(logstag,"making linkedAbilityInstaller");

			linkedAbilityInstaller = new AbilityInstaller(this); //create a new data store object linked to this location
			final MeshIcon newIcon = new MeshIcon(IconType.AbilityInstaller, parentLocation, linkedAbilityInstaller);
			
			HubsFeatures.put(linkedAbilityInstaller,newIcon);
			
			newIcon.setZIndex(150,super.getUniqueName()); 

			Gdx.app.log(logstag,"HubsFeatures:"+HubsFeatures.values());
		}

	}

	private void addAbilityObjectFile(SSSNode sssNode) {

		if (linkedAbilityDataStore==null){
			Gdx.app.log(logstag,"making linkedAbilityDataStore");

			linkedAbilityDataStore = new AbilityStoreObject(this); //create a new data store object linked to this location
			final MeshIcon newIcon = new MeshIcon(IconType.AbilityStore, parentLocation, linkedAbilityDataStore);

			HubsFeatures.put(linkedAbilityDataStore,newIcon);
			
			newIcon.setZIndex(150,super.getUniqueName()); 
			
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

		super.setBackgroundColour(col);



	}

	@Override
	public void open() {
		super.open();

		//first time we open we generate our contents if we are unlocked and it hasn't been done yet
		if (!locked){
			if (HubsFeatures.size()==0){
				generateLocationContents();
			} 

			//we also refresh if needed
			if (refreshOnOpen){
				Gdx.app.log(logstag,"regenerating contents");
				generateLocationContents(); //needs to be checked

			}
		}
	}


}
