package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.me.features.MeshIcon.IconType;
import com.lostagain.nl.me.models.ModelManagment;

/**
 * Manages the data request screens for a single location
 *  Effectively it turns the "firewall" information in the semantic files into the screens needed to get past them
 *  (it has no visual itself)
 *  
 * @author Tom
 **/
public class DataRequestManager {
	final static String logstag = "ME.DataRequestManager";
	
	SSSNode firewallsNode;
	LocationHub parentsLocation;
	boolean needslayout=false;
	
	/**
	 * The request screens needed to unlock the hub, in the order they should appear
	 */
	ArrayList<DataRequestScreen> requestScreens = new ArrayList<DataRequestScreen>();
	
	/**
	 * All the requestscreens and their icons
	 */
	HashMap<DataRequestScreen,MeshIcon> requestScreensAndIcons = new HashMap<DataRequestScreen,MeshIcon>(); 
	
	
	//data used in construction'
	/** Number of objects needed for query request **/
	private ArrayList<Integer> NumberOfObjectNeededList = new ArrayList<Integer>()
			{{
				add(1);
			}};
			
			
	
	public DataRequestManager(SSSNode firewallsNode, LocationHub parentsLocation) {
		super();
		
		this.firewallsNode = firewallsNode;
		this.parentsLocation = parentsLocation;
		
		generateScreens();
	}
	
	
	
	private void generateScreens(){
		
		Gdx.app.log(logstag,"__________________getting protection string_________________________");

		//get protection string
		HashSet<SSSNodesWithCommonProperty> securitysPropertys = SSSNodesWithCommonProperty.getCommonPropertySetsContaining(firewallsNode.PURI);

		Gdx.app.log(logstag,"__________________details of security: "+firewallsNode.PURI+" = "+securitysPropertys.size()+" _________________________");

		String QueryPass = "";
		String SecurityDiscription = "";
		String NumberOfObjectNeededForEachQuery = "";
		
		for (SSSNodesWithCommonProperty propertset : securitysPropertys) {

			Gdx.app.log(logstag,"_____prop:"+propertset.getCommonPrec()+" = "+propertset.getCommonValue());

			if (propertset.getCommonPrec()==StaticSSSNodes.queryPass){
				QueryPass = propertset.getCommonValue().getPLabel();

			}


			//get the description, if any.			
			if (propertset.getCommonPrec()==StaticSSSNodes.clueText){
				SecurityDiscription = propertset.getCommonValue().getPLabel();				
			}

			//get the number of items that match we need (default 1)			
			if (propertset.getCommonPrec()==StaticSSSNodes.ReqNum){

				NumberOfObjectNeededForEachQuery = propertset.getCommonValue().getPLabel();
				NumberOfObjectNeededList = getArrayListOfNumberOfObjectNeededForEachQuery(NumberOfObjectNeededForEachQuery);
				
			}

		}

		//this string contains all the querys that "lock" this location
		//each query specifies what type of answers will unlock that slot
		String protectionString = QueryPass;

		//the query pass is a comma separated and quoted list of queries
		//in most cases it will be just one
		// "color=green fruit"
		//but it can also be a few
		// "color=green fruit","color=red fruit"
		//which should make two slots and answer sets, one for green one for red

		Gdx.app.log(logstag,"_______protectionString ="+ protectionString);
		
		//so first we split be commas
		String queryArray[]           = protectionString.split(","); //in future we should support quotes in quotes?
		String defaultclue            = ""; //should be SecurityDiscription split then sycned
		String securityDiscriptions[] = null;
		if (!SecurityDiscription.isEmpty()){
			securityDiscriptions = SecurityDiscription.split(",");
		}
		
		Gdx.app.log(logstag,"_______generating "+queryArray.length+" request screens");
		//first we generate all the request screens
		for (int j = 0; j < queryArray.length; j++) {
			
			Gdx.app.log(logstag,"_______generating'screen:"+j);
			String securedByQuery = queryArray[j]; //the query that determines what objects are accepted.
			
			int  objectsRequired = 1; //default to oneobject fitting the criteria required

			if (NumberOfObjectNeededList.size()>=(j+1)){
				objectsRequired = NumberOfObjectNeededList.get(j); //but more might have been specified
			} 
						
			
			String currentDiscription = securedByQuery;//default the description to the query
			
			if (securityDiscriptions!=null && securityDiscriptions.length>j){
				currentDiscription = securityDiscriptions[j];
			}
			Gdx.app.log(logstag,"_______currentDiscription ="+ currentDiscription);
			
		
			
			DataRequestScreen newScreen = new DataRequestScreen(this,securedByQuery,objectsRequired,currentDiscription,null,null,null);
			MeshIcon requestScreensIcon = null;
			
			//associate an icon for it, or use the locationhub if its the last one in the chain
			if (j<(queryArray.length-1)){				
				requestScreensIcon = new MeshIcon(IconType.RequestScreen,parentsLocation.parentLocation,newScreen);
			} else {
				Gdx.app.log(logstag,"________last security needed, so associating with LocationHub instead);");
				requestScreensIcon = parentsLocation;
			}
			
			if (j>0){
				//if we arnt the first screen, then we link the previous screen to this new one
				//this means when the previous one is unlocked the new one will be shown
				//if there is no new screen, the locationhub is set instead to be shown the same way
				requestScreens.get(j-1).itemToConnectToIfUnlocked = requestScreensIcon;
			}
		
			//add to our list of screens (this is the order theuy should be unlocked in)
			requestScreens.add(newScreen);
			
			//add to our child array ready to lay out
			requestScreensAndIcons.put(newScreen,requestScreensIcon);
			
		}

		//layout
		
		//if theres just one request screen, we put it as the associated object at the locationhub we are locking		
//		if (requestScreens.keySet().size()==1){
//			
//			//add to parentsLocation
//			DataRequestScreen lockscreen = requestScreens.keySet().iterator().next();	
//			lockscreen.setRunThisWhenUnlocked(new Runnable() {
//			
//				@Override
//				public void run() {
//					Gdx.app.log(logstag,"___setting location as unlocked_____");
//					parentsLocation.setAsUnLocked();
//				}
//			});
//			
//			Gdx.app.log(logstag,"__________________adding requestscreens to parents location________________________");
//			parentsLocation.setAsLocked(lockscreen);
//			
//			
//		} else {
			Gdx.app.log(logstag,"__________________drawing various requestscreens______________________");


			Vector3 homepos = parentsLocation.getCenterOnStage();
			Gdx.app.log(logstag,"__________________homepos="+homepos.toString());
			int i = 1;
			
			//if theres more then 1 they are a succession of MeshIcon based locks leading up to that location hub.
			for (DataRequestScreen screen : requestScreens) {
				
				MeshIcon icon = requestScreensAndIcons.get(screen);
				
				if (requestScreens.indexOf(screen)==(requestScreens.size()-1)) { //only the last screen should be set onto the location
					
					//then we associate with the hub directly as its the last lock needed
					//add to parentsLocation
					DataRequestScreen lockscreen = screen;	
					
					lockscreen.setRunThisWhenUnlocked(new Runnable() {					
						@Override
						public void run() {
							Gdx.app.log(logstag,"___setting location as unlocked_____");
							parentsLocation.setAsUnLocked();
						}
					});
					
					Gdx.app.log(logstag,"__________________adding requestscreens to parents location________________________");
					parentsLocation.setAsLocked(lockscreen);
					
				} else {
					//we add the icon to the stage and make it a new link in the chain of locks
					//temp layout
					
					Gdx.app.log(logstag,"__________________adding lock screen icon:"+i);
					int disY = (requestScreens.size()-i)*130;
					Vector3 newpos  = new Vector3(homepos.x,homepos.y-disY,homepos.z); //NOTE: negative Y is temp while we are establishing new gui system
					icon.setToPosition(newpos);
					ModelManagment.addmodel(icon,ModelManagment.RenderOrder.zdecides);

					//hide by default unless we are the first in the chain
					if (i>1){				
						Gdx.app.log(logstag,"__________________hidding lock icon:"+i);
					//	icon.hide();
					} else {
						Gdx.app.log(logstag,"__________________showing lock icon:"+i);
						//first in chain is visible
						icon.show();
					}
					i++;
					
				}
				
			
			}
			
			
		//}
		
		
		
		

		/*
		if (SecurityDiscription==""){
			RequirementsText = addText(defaultclue, 10,super.getHeight()-90);
		} else {
			RequirementsText = addText(SecurityDiscription, 10,super.getHeight()-90); ;

		}*/
		
		//addAnswerDropTargets(); 

		needslayout=true;
		
		
	}

	/**
	 * wow this is a long function name.
	 * 
	 * ok
	 * 
	 * Splits a comma separated list of numbers into a arraylist
	 * eg
	 * a string of 1,4,6,2
	 * would be made into a arraylist with each number a different part
	 * 
	 * each number represents the number of answers for each query being asked to be forfilled.
	 * The total number of numbers should match the total number of queries (so the size of acceptableAnswers)
	 * 
	 * @param numberOfObjectNeededForEachQuery as a string
	 * @return an ArrayList<Interger) where the ints are quanity of each ans type being requested
	 */
	private ArrayList<Integer> getArrayListOfNumberOfObjectNeededForEachQuery(
			String numberOfObjectNeededForEachQuery) {

		ArrayList<Integer> newsetofnumbs = new ArrayList<Integer>();

		//split by comas
		String[] numStrings = numberOfObjectNeededForEachQuery.split(",");

		for (String numStr : numStrings) {

			newsetofnumbs.add(Integer.parseInt(numStr));

		}



		return newsetofnumbs;
	}
	
	public int numberOfLocks(){
		return this.requestScreens.size();
	}


/**
 * The first icon is used for incoming links to connect to, so we have a method to return it
 * @return
 */
	public MeshIcon getFirstLockIcon() {
		
		DataRequestScreen lastScreen = requestScreens.get(0);
		
		return lastScreen.parentIcon;
	}
	
}
