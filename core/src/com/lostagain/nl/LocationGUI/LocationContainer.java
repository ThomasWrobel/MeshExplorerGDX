package com.lostagain.nl.LocationGUI;

import java.util.ArrayList;


import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;






import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.PlayersData;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.uti.MeshWorld;

public class LocationContainer extends Table {

	private static final HashMap<SSSNode,LocationContainer> AllLocationContainers = new HashMap<SSSNode,LocationContainer>();


	static Logger Log = Logger.getLogger("LocationContainer");



	ArrayList<LocationScreen> AllPages = new ArrayList<LocationScreen>(); 

	//page information
	SSSNode LocationsNode;

	//Locations name and URI cropped to fit the window
	String displayLocation = "";
	String displayURI = "";


	//pages
	Table lowersplit = new Table();
	Stack mainPages = new Stack();

	EmailScreen emailPage;	
	SecurityScreen securityPage;	
	ContentsScreen contentsPage;
	LinksScreen linksPage;


	//page currently open
	LocationScreen CurrentlyOpenPage = emailPage;
	LocationMenuBar menucontainer;


	public Boolean locked = true; //while its locked you can only access the security page


	public LocationContainer(SSSNode LocationsNode) {
		super();
		// add a test label
		super.setSize(450,400);

		this.LocationsNode=LocationsNode;

		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		if (LocationsNode == PlayersData.computersuri){

			locked = false;
			securityPage = new SecurityScreen(this,null); 

		} else {

			//load the security, if any  
			securityPage = getSecurity(LocationsNode);


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
		super.add(nameLabel).fillX().expandX();
		super.row();
		super.add(addressLabel).fillX().expandX();
		super.row();

		super.left();
		super.top();
		super.add(lowersplit).expand().fill(); //.top()
		//	lowersplit.fill();
		menucontainer = new LocationMenuBar(this, skin);
		menucontainer.setlocked(locked);

		lowersplit.debug();
		
		lowersplit.add(menucontainer).top().left().fillY().expandY();

		//	mainPages.setFillParent(true);
	//	super.setDebug(true,true);

		//setting up the security requires getting its security node
		//thus its set up here in a function for neatness
		//	securityPage = getSecurity(LocationsNode); 
		//securityPage = new SecurityScreen(this,LocationsNode);
		contentsPage = new ContentsScreen(this,LocationsNode);
		emailPage = new EmailScreen(this,LocationsNode);
		linksPage =  new LinksScreen(this,LocationsNode);


		AllPages.add(securityPage);		
		AllPages.add(contentsPage);
		AllPages.add(emailPage);		
		AllPages.add(linksPage);

		mainPages.add(securityPage);
		mainPages.add(contentsPage);
		mainPages.add(emailPage);
		mainPages.add(linksPage);

		//lowersplit.addActor(mainPages);

		lowersplit.add(mainPages).expand().fill();

		if (locked) {
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

		AllLocationContainers.put(LocationsNode,this);



	}




	public void gotoLinks() {
		
		hideAllPages();
		linksPage.setVisible(true);
		menucontainer.setLinkButtonUp();
		
	}

	public void gotoSecplace(){
		hideAllPages();
		securityPage.setVisible(true);
		menucontainer.setinfoButtonUp();

	}
	public void gotoContents(){
		hideAllPages();
		contentsPage.setVisible(true);
		menucontainer.setDataButtonUp();

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

		//first load the links visible to this one
		getVisibleMachines(mycomputerdata);

		//then contents
		getContentOfMachine(mycomputerdata);


	}

	public void refreshContents(){

		getContentOfMachine(LocationsNode);
	}


	private SecurityScreen getSecurity(SSSNode mycomputerdata) {

		locked = false;
		SSSNode securedBy = null;

		Log.info("getting security for:"+mycomputerdata.PURI);


		HashSet<SSSNodesWithCommonProperty> sets = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(mycomputerdata.getPURI());


		Log.info("sets:"+sets.size());


		for (SSSNodesWithCommonProperty sssNodesWithCommonProperty : sets) {

			if (sssNodesWithCommonProperty.getCommonPrec()==StaticSSSNodes.SecuredBy){

				securedBy = sssNodesWithCommonProperty.getCommonValue();
				Log.info("security found:"+securedBy.getPURI());

				locked = true;
				break;
			}

		}

		return new SecurityScreen(this,securedBy); 

		//find this machines security



	}


	protected void populateVisibleComputers(ArrayList<SSSNode> testresult) {
		Log.info("computers visible to this = "+testresult.size());
		linksPage.clearLinks();

		for (SSSNode sssNode : testresult) {

			linksPage.addLink(sssNode);


		}


	}

	/** populates the contents on this machine from the supplied nodes **/
	protected void populateContents(ArrayList<SSSNode> testresult) {

		//clear existing lists in case they have changed
		contentsPage.removeAllContents();


		Log.info("removing emails ");
		emailPage.removeAllMessages();

		int emails=0;
		int data=0;

		Log.info("_____________contents:  "+testresult.size());

		for (SSSNode sssNode : testresult) {

			if (sssNode.isOrHasParentClass(StaticSSSNodes.software.getPURI())){

				contentsPage.addObjectFile(sssNode);
				data++;
			}


			if (sssNode.isOrHasParentClass(StaticSSSNodes.messages.getPURI())){

				Log.info(" adding email");
				emailPage.addEmailLocation(sssNode);

				emails++;
			}



		}

		//hmz..need to way to ensure layout when all emails are loaded


		menucontainer.setNumberOfDataObjects(data);		
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


		Query realQuery = new Query("(me:visibleto=me:everyone)||(me:visibleto="+thisPURI+")");

		//populate when its retrieved
		DoSomethingWithNodesRunnable callback = new DoSomethingWithNodesRunnable(){

			@Override
			public void run(ArrayList<SSSNode> testresult, boolean invert) {



				Log.warning("populateVisibleComputers");
				populateVisibleComputers(testresult);


			}

		};

		QueryEngine.processQuery(realQuery, false, null, callback);

		//VisibleMachines.getAllNodesInSet(callback);


	}

	public static LocationContainer getLocation(SSSNode linksToThisPC) {

		if (AllLocationContainers.containsKey(linksToThisPC)){
			return AllLocationContainers.get(linksToThisPC);
		} else {

			//creating new location
			LocationContainer newloc = new LocationContainer(linksToThisPC);

			String domain = linksToThisPC.getPURI();
			Log.info("domain is="+domain);

			Vector2 loc =  MeshWorld.locationFromDomain(domain);




			int X = (int) loc.x;  //(int) (Math.random()*2500);
			int Y = (int) loc.y; //500;

			Log.info("getting unused location. Testing:"+X+","+Y);

			X = getNextUnusedLocation(X,Y);


			MainExplorationView.addnewlocation(newloc,X, Y);



			return newloc;

		}

	}

	private static int getNextUnusedLocation(int x, int y) {

		Boolean xchanged = true;

		int CONHEIGHT = 500;
		int CONWIDTH = 500;

		while (xchanged)
		{

			xchanged = false;


			Log.info("getting unused location. Testing:"+x);

			//loop over all locations, displace X if its overlaps
			for (LocationContainer con : AllLocationContainers.values()) {

				float miny = con.getY();
				float maxy = con.getY() + con.getHeight();

				float minx = con.getX();
				float maxx = con.getX() + con.getWidth();

				Log.info("loc="+con.displayLocation);
				Log.info("miny="+miny+" maxy="+maxy+" y="+y);
				Log.info("minx="+minx+" maxx="+maxx+" x="+x);
				//if within y with margin
				if (((y+CONHEIGHT)>miny-5) && (y<maxy+5))
				{
					Log.info("within y");
					if (((x+CONWIDTH)>minx-5) && (x<maxx+5)){

						Log.info("within x");

						x=(int) (x+con.getWidth());

						Log.info("new x ="+x);
						xchanged=true;

						continue;

					}

				}



			}

		}

		return x;
	}

	public void unlockComputer() {

		Log.info("unlocking location");

		locked = false;


		if (menucontainer!=null){
			menucontainer.setlocked(locked);
		}

		if (!(LocationsNode==null)){
			PlayersData.addUnlockedLink(LocationsNode);
		}

	}

}
