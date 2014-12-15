package com.lostagain.nl.me.LocationGUI;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;
















import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.TouchableAction;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.darkflame.client.query.Query;
import com.darkflame.client.semantic.QueryEngine;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.darkflame.client.semantic.QueryEngine.DoSomethingWithNodesRunnable;
import com.lostagain.nl.DefaultStyles;
import com.lostagain.nl.ME;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.models.BackgroundManager;
import com.lostagain.nl.uti.MeshWorld;

public class LocationsHub extends Table {


	static Logger Log = Logger.getLogger("LocationsHub");



	ArrayList<LocationScreen> AllPages = new ArrayList<LocationScreen>(); 

	//page information	
	public SSSNode LocationsNode;

	//this location
	LocationsHub thisLocation = this;
	//Locations name and URI cropped to fit the window
	String displayLocation = "";
	String displayURI = "";


	//pages
	Table lowersplit = new Table();
	Stack mainPages = new Stack();

	EmailScreen emailPage;	
	RepairScreen repairPage;	
	ContentsScreen contentsPage;
	ContentsScreen abilityPage;
	LinksScreen linksPage;

	//page currently open
	LocationScreen CurrentlyOpenPage = emailPage;
	LocationMenuBar menucontainer;


	public Boolean closed = true; //while its locked you can only access the security page

	Color backcolour; 
	
	private ModelInstance backgroundObject;


	public LocationsHub(SSSNode LocationsNode) {
		super();
		// add a test label
		super.setSize(450,400);

		this.LocationsNode=LocationsNode;

		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		if (LocationsNode == PlayersData.computersuri){

			closed = false;
			repairPage = new RepairScreen(this,null); 

		} else {

			//load the security, if any  
			repairPage = getSecurity(LocationsNode);


			/*

			//we check if its got security associated with it
			//we could optimise to skip this check if  we already know its secured
			//incoming links will know that already
			ArrayList<SSSNode> allSecuredPCs = SSSNodesWithCommonProperty.getAllNodesWithPredicate(StaticSSSNodes.SecuredBy);

			//if so, then lock it.
			if (allSecuredPCs.contains(locationsNode)){
				locked = true;
			} else {
				locked = false;
			}
			 */

		}
		
		


		HorizontalGroup nameinfo = new HorizontalGroup();		
		nameinfo.fill();

		displayLocation = LocationsNode.getPLabel();
		displayURI = LocationsNode.getPURI();


		if (displayLocation.length()>40){
			displayLocation=displayLocation.substring(0, 40);
		}

		if (displayURI.length()>40){
			displayURI=" ..."+displayURI.substring(displayURI.length()-40);
		}


		Label nameLabel = new Label(displayLocation,skin);
		Label addressLabel = new Label("Location URI:"+displayURI,skin);
		nameLabel.setAlignment(Align.center);
		addressLabel.setAlignment(Align.center);
		nameLabel.setFontScale(1.3f);
		addressLabel.setFontScale(0.9f);

		super.center();
		super.debug();
		super.top();

		Label refresh = new Label("(R)",skin);
		
		Table titlebar = new Table();
		titlebar.setHeight(50f);
		refresh.setWidth(135f);
		
		titlebar.add(nameLabel).expandX() .fillX().fillY();
		titlebar.add(refresh).align(Align.center).fillY().fillX();
		
		titlebar.validate();		
		titlebar.setDebug(true,true);
		
		refresh.addListener(new InputListener() {
		 	@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		 		Gdx.app.log("LocationContainer", "refresh clicked");
				if (!closed){
					//only refresh if locatiion not closed
					refreshContents();
			 		
				}
				validateAllPages();
				
		 		return false;
		 	}
		 });
		
		//refresh.setAlignment(Align.right);
		
		//refresh.setFillParent(true);
		//nameLabel.setFillParent(true);
		
		super.add(titlebar).fillX().expandX().expandY();
		
		
		super.row();
		super.add(addressLabel).fillX().expandX();
		super.row();

		super.left();
		super.top();
		super.add(lowersplit).expand().fill(); //.top()
		//	lowersplit.fill();
		menucontainer = new LocationMenuBar(this, skin);
		menucontainer.setlocked(closed);

		lowersplit.debug();
		
		lowersplit.add(menucontainer).top().left().fillY().expandY();

		//	mainPages.setFillParent(true);
	//	super.setDebug(true,true);

		//setting up the security requires getting its security node
		//thus its set up here in a function for neatness
		//	securityPage = getSecurity(LocationsNode); 
		//securityPage = new SecurityScreen(this,LocationsNode);
		contentsPage = new ContentsScreen(this,LocationsNode, "Data:");
		abilityPage = new ContentsScreen(this,LocationsNode, "Abilitys:");
		emailPage = new EmailScreen(this,LocationsNode);
		linksPage =  new LinksScreen(this,LocationsNode);


		AllPages.add(repairPage);		
		AllPages.add(contentsPage);
		AllPages.add(abilityPage);
		AllPages.add(emailPage);		
		AllPages.add(linksPage);
		
		mainPages.add(repairPage);
		mainPages.add(contentsPage);
		mainPages.add(abilityPage);
		mainPages.add(emailPage);
		mainPages.add(linksPage);

		//lowersplit.addActor(mainPages);

		lowersplit.add(mainPages).expand().fill();

		if (closed) {
			gotoSecplace();
		} else {

			if (PlayersData.hasVisited(LocationsNode) || emailPage.numOfEmails>0 ){

				gotoLinks();

			} else {

				gotoEmail();
				PlayersData.addToVisitedLocations(LocationsNode);


			}
		}


		loadNodesData(LocationsNode);
		//ensure they are all sized correctly
		validateAllPages();

	//	AllLocationContainers.put(LocationsNode,this);
	
		
		//any page that might have a scroll should disable the drag		
		emailPage.addListener(new InputListener() {
		 	@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		 		//Gdx.app.log("Example", "touch started at (" + x + ", " + y + ")");
				// Log.info("clicked "+thisLocation.LocationsNode.getPLabel());			
				 MainExplorationView.disableDrag();
				 
		 		return false;
		 	}
		 });
		
		
		contentsPage.addListener(new InputListener() {
		 	@Override
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
		 		MainExplorationView.disableDrag();				 
		 		return false;
		 	}
		 });
		 

/*
		super.addListener(new ClickListener () {			
			@Override
			public void clicked(InputEvent ev, float x , float y){
				
				 if (thisLocation!=MainExplorationView.LastLocation.peekLast()){
					 
					 Log.info("adding "+thisLocation.LocationsNode.getPLabel());					 
					 MainExplorationView.LastLocation.add(thisLocation);
				}
				 
			}

		});*/
		
		
	}




	public void gotoLinks() {
		
		hideAllPages();
		linksPage.setVisible(true);
		menucontainer.setLinkButtonUp();
		
	}

	public void gotoSecplace(){
		hideAllPages();
		repairPage.setVisible(true);
		menucontainer.setinfoButtonUp();

	}
	
	public void gotoContents(){
		hideAllPages();
		contentsPage.setVisible(true);
		menucontainer.setDataButtonUp();

	}
	public void gotoAbilitys(){
		hideAllPages();
		abilityPage.setVisible(true);
		menucontainer.setAbilityButtonUp();

	}
	public void gotoEmail(){
		hideAllPages();
		emailPage.setVisible(true);
		menucontainer.setEmailButtonUp();

	}

	public void validateAllPages(){

		super.validate();
		lowersplit.validate();
		mainPages.validate();

		//Log.info(" lowersplit "+lowersplit.getHeight());
		//Log.info(" super "+super.getHeight());
		//
		//Log.info("main page height="+mainPages.getHeight());

		for (LocationScreen page : AllPages) {
			page.validate();
		}

		

	}

	public void hideAllPages(){

		for (LocationScreen page : AllPages) {
			page.setVisible(false);			
		}


	}

	private void loadNodesData(SSSNode mycomputerdata) {
		System.out.print("loading node:"+mycomputerdata);

		// get the data for this node
		
		//get style data
		ArrayList<Color> backcolours = DefaultStyles.getColorsFromNode(mycomputerdata);				
		if (backcolours!=null){
			Log.info("setting backcolor to first in :"+backcolours.toString());
			setBackgroundColour(backcolours.get(0));		
		}
		
		

		//first load the links visible to this one
		getVisibleMachines(mycomputerdata);

		//then contents
		getContentOfMachine(mycomputerdata);


	}

	public void refreshContents(){

		getContentOfMachine(LocationsNode);
		
		//recheck if we need to draw new link links
		//(for example, if new links have been unlocked since the last check)
		if (MainExplorationView.gameStage.getActors().contains(this, true))			
		{
			Log.info("rechecking link lines");
			linksPage.recheckLinkLines();
		}
	}


	private RepairScreen getSecurity(SSSNode mycomputerdata) {

		closed = false;
		SSSNode securedBy = null;

		Log.info("getting security for:"+mycomputerdata.PURI);


		HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(mycomputerdata.getPURI());


		Log.info("sets:"+sets.size());


		for (SSSNodesWithCommonProperty sssNodesWithCommonProperty : sets) {

			if (sssNodesWithCommonProperty.getCommonPrec()==StaticSSSNodes.SecuredBy){

				securedBy = sssNodesWithCommonProperty.getCommonValue();
				Log.info("security found:"+securedBy.getPURI());

				closed = true;
				break;
			}

		}

		return new RepairScreen(this,securedBy); 

		//find this machines security



	}


	protected void populateVisibleComputers(ArrayList<SSSNode> testresult) {
		Log.info("computers visible to this = "+testresult.size());
		linksPage.clearLinks();

		for (SSSNode sssNode : testresult) {

			//add to link list if its not the current pc
			if (sssNode!=this.LocationsNode){			
				linksPage.addLink(sssNode);
			}

		}


	}

	/** populates the contents on this machine from the supplied nodes **/
	protected void populateContents(ArrayList<SSSNode> testresult) {


		Log.info("populateContents for "+LocationsNode);
		
		
		//clear existing lists in case they have changed
		Log.info("removing contents ");
		contentsPage.removeAllContents();

		Log.info("removing emails ");
		emailPage.removeAllMessages();

		int emails=0;
		int data=0;
		int abil=0;
		Log.info("_____________contents:  "+testresult.size());

		for (SSSNode sssNode : testresult) {

			Log.info("Adding :"+sssNode.getPLabel());
			
			if (sssNode.isOrHasParentClass(StaticSSSNodes.software.getPURI())){

				
				//place on right page depending on type
				if (sssNode.isOrHasParentClass(StaticSSSNodes.ability.getPURI())){
					abilityPage.addObjectFile(sssNode);
					abil++;
				} else {
					contentsPage.addObjectFile(sssNode);
					data++;
				}
				
			}


			if (sssNode.isOrHasParentClass(StaticSSSNodes.messages.getPURI())){

				Log.info(" Adding email");
				
				//First we check if its got a language specified 
				
				//note still using this messy method
				//SSS really needs a neater way to get a nodes property rather then just its classes
				SSSNode writtenIn =null;
				HashSet<SSSNodesWithCommonProperty> propertysOfEmail = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(sssNode.getPURI());
				for (SSSNodesWithCommonProperty ep : propertysOfEmail) {
					if (ep.getCommonPrec() == StaticSSSNodes.writtenin){
						Log.info("detected language spec");
						 writtenIn = ep.getCommonValue();
						Log.info("detected language written in:"+writtenIn.getPLabel());
					}					
				}
				
				
				
				emailPage.addEmailLocation(sssNode,writtenIn); //note; order cant be gaurentied yet
				
				
				emails++;
			}



		}

		//hmz..need to way to ensure layout when all emails are loaded


		menucontainer.setNumberOfDataObjects(data);	
		menucontainer.setNumberOfAbilityObjects(abil);
		
		menucontainer.setNumberOfMessages(emails);
		menucontainer.validate();


	}


	/** gets the content of the supplied location **/
	public void getContentOfMachine(SSSNode tothisnode){

		Log.warning("populate contents of:"+tothisnode);

		Log.warning("SSSNodesWithCommonProperty with: "+StaticSSSNodes.isOn.PURI+","+tothisnode.PURI);

		SSSNodesWithCommonProperty contentOfMACHINE =  SSSNodesWithCommonProperty.getSetFor(StaticSSSNodes.isOn, tothisnode); //.getAllNodesInSet(callback);

		//Query realQuery = new Query(" ison=alicespc ");

		DoSomethingWithNodesRunnable callback2 = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> testresult, boolean invert) {

				if (testresult.size()>0){

					//remove duplicates by using an HashSet
					// add elements to al, including duplicates
					HashSet hs = new HashSet();
					hs.addAll(testresult);
					testresult.clear();
					testresult.addAll(hs);

					Log.warning("populate contents");
					populateContents(testresult);


				}

			}

		};

		//  QueryEngine.processQuery(realQuery, false, null, callback2);

		if (contentOfMACHINE!=null){

			Log.warning("getting contents:"+contentOfMACHINE.isLoaded);
			Log.warning("getting contents:"+contentOfMACHINE.getCommonPrec()+":"+contentOfMACHINE.getCommonValue());
			Log.warning("getting contents:"+contentOfMACHINE.getSourceFiles().toString());
			Log.warning("getting contents:"+contentOfMACHINE.getLefttoLoad() );

			contentOfMACHINE.getAllNodesInSet(callback2);
		}


	}

	/** gets the locations visible to the supplied location **/
	private void getVisibleMachines(SSSNode tothisnode){

		//String allNodes = SSSNode.getAllKnownNodes().toString();

		//Log.info("all nodes"+allNodes.toString());


		Log.info("---------------------------------------------------------------===================----------------");
		//SSSNodesWithCommonProperty VisibleMachines =  SSSNodesWithCommonProperty.getSetFor(visibletest, everyonetest); //.getAllNodesInSet(callback);

		String thisPURI = tothisnode.getPURI(); ///"C:\\TomsProjects\\MeshExplorer\\bin/semantics/DefaultOntology.n3#bobspc";


		Query realQuery = new Query("(me:connectedto=me:everyone)||(me:connectedto="+thisPURI+")");

		//populate when its retrieved
		DoSomethingWithNodesRunnable callback = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> testresult, boolean invert) {



				Log.warning("populate connectedto Computers");
				populateVisibleComputers(testresult);


			}

		};

		QueryEngine.processQuery(realQuery, false, null, callback);

		//VisibleMachines.getAllNodesInSet(callback);


	}



	

	public void unlockComputer() {

		Log.info("unlocking location");

		closed = false;


		if (menucontainer!=null){
			menucontainer.setlocked(closed);
		}

		if (!(LocationsNode==null)){
			PlayersData.addUnlockedLink(LocationsNode);
		}
		
		BackgroundManager.removeModelInstance(backgroundObject);

	}

	@Override
	public void layout(){
		super.layout();
				
		//ensure background is positioned
		if (backgroundObject!=null){

			backgroundObject.transform.setToTranslation((int)this.getX(),(int)this.getY(),-110);
		}
		
	}


	public void addBackground() {

		backgroundObject = BackgroundManager.addRectangle((int)this.getX(),(int)this.getY(),-110,(int)this.getWidth(),(int)this.getHeight(),new Material());
		
		
	}
	
	
	public void setBackgroundColour( Color col) {
		backcolour = col;
		if (backgroundObject==null){
			addBackground(); 
		}
		
		
		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);
		
		backgroundObject.materials.get(0).clear();
		backgroundObject.materials.get(0).set(ColorAttribute.createDiffuse(col));
		backgroundObject.materials.get(0).set(blendingAttribute2);
        
	}
	
	
	public void setClosedBackground() {
		
		if (backgroundObject==null){
			addBackground(); 
		}
		
		//
		//Material mat = BackgroundManager.createNoiseMaterial();	

		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);

	    Texture texture = new Texture(BackgroundManager.createNoiseImage(300,300));
	    
		backgroundObject.materials.get(0).clear();
		backgroundObject.materials.get(0).set(ColorAttribute.createDiffuse(Color.WHITE));
		backgroundObject.materials.get(0).set(ColorAttribute.createSpecular(Color.WHITE));
		backgroundObject.materials.get(0).set(FloatAttribute.createShininess(16f));
		backgroundObject.materials.get(0).set(TextureAttribute.createDiffuse(texture));
		backgroundObject.materials.get(0).set(blendingAttribute2);
		
		
		
		//BackgroundManager.addNoiseRectangle((int)this.getX(),(int)this.getY(),(int)this.getWidth(),(int)this.getHeight());	
		
		//backgroundObject = BackgroundManager.addRectangle((int)this.getX(),(int)this.getY(),-110,(int)this.getWidth(),(int)this.getHeight(),mat);

		
	      //  mat.remove(TextureAttribute.Diffuse);
		
	 //   int allMats = backgroundObject.materials.size;
	   // for (int i = 0; i < allMats-1; i++) {
	   // 	backgroundObject.materials.set(i, mat2);
		//}
		
		
		
		
		BackgroundManager.giveAnimatedNoiseTextureToRectangle(backgroundObject);
		
		
	}

}
