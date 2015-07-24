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
	 * All the requestscreens made by this manager
	 */
	HashMap<MeshIcon,DataRequestScreen> requestScreens = new HashMap<MeshIcon,DataRequestScreen>(); 
	
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

		//so first we split be commas
		String[] queryArray  = protectionString.split(","); //in future we should support quotes in quotes?
		String   defaultclue = ""; //should be SecurityDiscription split then sycned
		

		Vector3 homepos = parentsLocation.getCenterOnStage();
		
		
		//first we generate all the request screens
		for (int j = 0; j < queryArray.length; j++) {
			
			String securedByQuery = queryArray[j];
			int objectsRequired = NumberOfObjectNeededList.get(j);
			
			
			DataRequestScreen newScreen = new DataRequestScreen(this,securedByQuery,objectsRequired,SecurityDiscription,null,null,null);
			//its icon (might not be needed if its the first one
			//we could optimise this later to not bother creating one?)
			MeshIcon requestScreensIcon = new MeshIcon(IconType.RequestScreen,parentsLocation.parentLocation,newScreen);
			
		
			
			//add to our child array ready to lay out
			requestScreens.put(requestScreensIcon,newScreen);
			
		}

		//layout
		
		//if theres just one request screen, we put it as the associated object at the locationhub we are locking		
		if (requestScreens.keySet().size()==1){
			
			//add to parentsLocation
			DataRequestScreen lockscreen = requestScreens.values().iterator().next();	
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
			Gdx.app.log(logstag,"__________________drawing various requestscreens under parents location________________________");

			int i = 1;
			
			//if theres more then 1 they are a succession of MeshIcon based locks leading up to that location hub.
			for (MeshIcon icon : requestScreens.keySet()) {
				
				//temp layout
				Vector3 newpos  = new Vector3(homepos.x-(icon.getWidth()/2),homepos.y-(i*100),homepos.z);
				icon.setToPosition(newpos);
				ModelManagment.addmodel(icon,ModelManagment.RenderOrder.zdecides);
				i++;
			}
			
			
		}
		
		
		
		

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
	
}
