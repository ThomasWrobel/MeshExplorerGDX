package com.lostagain.nl.me.features;

import java.util.ArrayList;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;
import com.lostagain.nl.StaticSSSNodes;
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

		for (int j = 0; j < queryArray.length; j++) {
			
			String securedByQuery = queryArray[j];
			int objectsRequired = NumberOfObjectNeededList.get(j);
			
			
			DataRequestScreen newScreen = new DataRequestScreen(this,securedByQuery,objectsRequired,SecurityDiscription,null,null,null);
		
			//temp layout
			Vector3 newpos  = new Vector3(homepos.x-(newScreen.getWidth()/2),homepos.y-200,homepos.z);
			newScreen.setToPosition(newpos);
			ModelManagment.addmodel(newScreen,ModelManagment.RenderOrder.zdecides);
			
			//add to our child array ready to lay out
			
		}

		//layout
		

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
