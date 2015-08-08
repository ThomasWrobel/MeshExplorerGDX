package com.lostagain.nl;

import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSNodesWithCommonProperty;

/** A collection of static nodes that represent concepts in the game **/
public class StaticSSSNodes {
	//----	
	public static final SSSNode CONNECTEDTO = SSSNode.createSSSNode(ME.INTERNALNS+"connectedto", ME.INTERNALNS);
	public static final SSSNode EVERYONE =  SSSNode.createSSSNode(ME.INTERNALNS+"everyone", ME.INTERNALNS);
	
	public static final SSSNode Computer =  SSSNode.createSSSNode(ME.INTERNALNS+"computer", ME.INTERNALNS);
	public static final SSSNode SecuredBy =  SSSNode.createSSSNode(ME.INTERNALNS+"SecuredBy", ME.INTERNALNS);
	public static final SSSNode UnlockedBy =  SSSNode.createSSSNode(ME.INTERNALNS+"UnlockedBy", ME.INTERNALNS);
	public static final SSSNode DescriptionOf =  SSSNode.createSSSNode(ME.INTERNALNS+"DescriptionOf", ME.INTERNALNS);
		
	public static final SSSNode Security =  SSSNode.createSSSNode(ME.INTERNALNS+"Security", ME.INTERNALNS);
	//public static final SSSNode visibleTo =  SSSNode.createSSSNode(ME.INTERNALNS+"visibleTo", ME.INTERNALNS);	
	public static final SSSNode queryPass =  SSSNode.createSSSNode(ME.INTERNALNS+"queryPass", ME.INTERNALNS);
	public static final SSSNode clueText =  SSSNode.createSSSNode(ME.INTERNALNS+"clueText", ME.INTERNALNS);
	
	//public static final SSSNode textPass =  SSSNode.createSSSNode(ME.INTERNALNS+"textPass", ME.INTERNALNS);
    public static final SSSNode ReqNum =  SSSNode.createSSSNode(ME.INTERNALNS+"ReqNum", ME.INTERNALNS);
	
	public static final SSSNode isOn =  SSSNode.createSSSNode(ME.INTERNALNS+"isOn", ME.INTERNALNS);
	/**
	 * Indicates a bit of software is running on the specified value
	 */
	public static final SSSNode isRunningOn =  SSSNode.createSSSNode(ME.INTERNALNS+"isRunningOn", ME.INTERNALNS);
	
	
	//location content types
	public static final SSSNode software= SSSNode.createSSSNode(ME.INTERNALNS+"software", ME.INTERNALNS);
	public static final SSSNode messages= SSSNode.createSSSNode(ME.INTERNALNS+"messages", ME.INTERNALNS);
	
	//Language
	public static final SSSNode language =  SSSNode.createSSSNode(ME.INTERNALNS+"language", ME.INTERNALNS);
	public static final SSSNode writtenin =  SSSNode.createSSSNode(ME.INTERNALNS+"writtenin", ME.INTERNALNS);
	public static final SSSNode knows =  SSSNode.createSSSNode(ME.INTERNALNS+"knows", ME.INTERNALNS);
	
	//default language
	public static final SSSNode stdascii =  SSSNode.createSSSNode("Stdascii",ME.INTERNALNS+"stdascii", ME.INTERNALNS,new SSSNode[]{language});

	//other languages
	public static final SSSNode scram1 =  SSSNode.createSSSNode("Scram1",ME.INTERNALNS+"scram1", ME.INTERNALNS,new SSSNode[]{language});

	//nodes used in ability's
	public static final SSSNode Capacity = SSSNode.createSSSNode("Capacity", ME.INTERNALNS+"Capacity", ME.INTERNALNS);
	public static final SSSNode Integer = SSSNode.createSSSNode("Capacity", ME.INTERNALNS+"Capacity", ME.INTERNALNS);
	
	public static final SSSNode Seven = SSSNode.createSSSNode("7", ME.INTERNALNS+"Seven", ME.INTERNALNS,new SSSNode[]{Integer});
	public static final SSSNode  Nine = SSSNode.createSSSNode("9", ME.INTERNALNS+"Nine", ME.INTERNALNS,new SSSNode[]{Integer});

	
	//Ability's
	public static final SSSNode ability     = SSSNode.createSSSNode("Ability", ME.INTERNALNS+"ability", ME.INTERNALNS,new SSSNode[]{software});	
	
	public static final SSSNode decoder     = SSSNode.createSSSNode("Decoder", ME.INTERNALNS+"decoder", ME.INTERNALNS,new SSSNode[]{ability});
	public static final SSSNode conceptgun  = SSSNode.createSSSNode("ConceptGun", ME.INTERNALNS+"ConceptGun", ME.INTERNALNS,new SSSNode[]{ability});
	public static final SSSNode gui         = SSSNode.createSSSNode("gui", ME.INTERNALNS+"gui", ME.INTERNALNS,new SSSNode[]{ability});
	
	//define gui
	public static final SSSNode standardgui = SSSNode.createSSSNode("standardgui", ME.INTERNALNS+"standardgui", ME.INTERNALNS,new SSSNode[]{gui});
	
	
	
	//define decoders
	public static final SSSNode asciidecoder= SSSNode.createSSSNode ("Asciidecoder" , ME.INTERNALNS+"asciidecoder",  ME.INTERNALNS,new SSSNode[]{decoder});
	public static final SSSNodesWithCommonProperty knowsAscii = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(knows, stdascii, new SSSNode[]{asciidecoder});

	public static final SSSNode scram1decoder= SSSNode.createSSSNode("Scram1decoder", ME.INTERNALNS+"scram1decoder", ME.INTERNALNS,new SSSNode[]{decoder});
	public static final SSSNodesWithCommonProperty knowsScram1 = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(knows, scram1, new SSSNode[]{scram1decoder});
	
	//scanner (scans links and retrieves downloads)
	public static final SSSNode scanner = SSSNode.createSSSNode("scanner", ME.INTERNALNS+"scanner", ME.INTERNALNS,new SSSNode[]{ability});
	public static final SSSNode prototype_scanner = SSSNode.createSSSNode("PrototypeScanner", ME.INTERNALNS+"PrototypeScanner", ME.INTERNALNS,new SSSNode[]{scanner});
	
	
	//give it base abilities
	public static final SSSNode multitasking= SSSNode.createSSSNode(ME.INTERNALNS+"multitasking", ME.INTERNALNS);
	public static final SSSNode speed = SSSNode.createSSSNode(ME.INTERNALNS+"speed", ME.INTERNALNS);	
	public static final SSSNode level1 = SSSNode.createSSSNode(ME.INTERNALNS+"level1", ME.INTERNALNS);	
	public static final SSSNodesWithCommonProperty level1multitasking = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(speed, level1, new SSSNode[]{prototype_scanner});
	public static final SSSNodesWithCommonProperty level1downloadspeed = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(multitasking, level1, new SSSNode[]{prototype_scanner});

	//memory ability (that is, the players inventory)	
	public static final SSSNode STMemoryAbility   = SSSNode.createSSSNode("STMemoryAbility", ME.INTERNALNS+"STMemoryAbility"    , ME.INTERNALNS,new SSSNode[]{ability});
	public static final SSSNode BasicInventory    = SSSNode.createSSSNode("BasicInventory", ME.INTERNALNS+"BasicInventory"      , ME.INTERNALNS,new SSSNode[]{STMemoryAbility});
	public static final SSSNode ExpandedInventory = SSSNode.createSSSNode("ExpandedInventory", ME.INTERNALNS+"ExpandedInventory", ME.INTERNALNS,new SSSNode[]{STMemoryAbility});
	//note (these lists arnt needed, its just a method to give basicinventory and expanded inventory their propertys)
	
	public static final SSSNodesWithCommonProperty CapcitySeven = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(Capacity, Seven, new SSSNode[]{BasicInventory});
	 public static final SSSNodesWithCommonProperty CapcityNine = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(Capacity, Nine, new SSSNode[]{ExpandedInventory});
	
	
	//concept gun ability	
	public static final SSSNode PrototypeConceptGun = SSSNode.createSSSNode("PrototypeConceptGun", ME.INTERNALNS+"PrototypeConceptGun", ME.INTERNALNS,new SSSNode[]{conceptgun});
		
	
	
	//populations
	//Populations define a group of creatures
	public static final SSSNode populatedBy =  SSSNode.createSSSNode(ME.INTERNALNS+"populatedBy", ME.INTERNALNS);
	
	/*** example of population definition from ntlist;
	 * smallInfovorPopulation rdfs:subClassOf me:population.
	smallInfovorPopulation me:quantity 10.
	smallInfovorPopulation me:anydrop dropdata1.
	smallInfovorPopulation me:exactdrop dropdata2.
	smallInfovorPopulation me:anydrop dropdata3.
	smallInfovorPopulation me:killedon "fruit"
	 * 
	 * **/
	public static final SSSNode quantity =  SSSNode.createSSSNode(ME.INTERNALNS+"quantity", ME.INTERNALNS);
	
	/**picks a random subclass of this to drop. Drops will be randomly distributed over the population instance.
	 * all drops are thus guaranteed to be getable **/
	public static final SSSNode anydrop =  SSSNode.createSSSNode(ME.INTERNALNS+"anydrop", ME.INTERNALNS);
	
	/** represents an exact drop. Will not randomly pick a subclass**/
	public static final SSSNode exactdrop =  SSSNode.createSSSNode(ME.INTERNALNS+"exactdrop", ME.INTERNALNS);
	
	/**query that needs to be forfilled to kill this.If not specified empty clicks work**/
	public static final SSSNode killedon =  SSSNode.createSSSNode(ME.INTERNALNS+"killedon", ME.INTERNALNS);
	
	/** number of times a query (or click) needs to be past in order to be destroyed**/
	public static final SSSNode hitPoints =  SSSNode.createSSSNode(ME.INTERNALNS+"hitPoints", ME.INTERNALNS);

	/** from and too radius for layout of creatures **/
	public static final SSSNode fromRadius =  SSSNode.createSSSNode(ME.INTERNALNS+"fromRadius", ME.INTERNALNS);
	public static final SSSNode toRadius =  SSSNode.createSSSNode(ME.INTERNALNS+"toRadius", ME.INTERNALNS);
	
	/** Determains the z position **/
	public static final SSSNode atHeight =  SSSNode.createSSSNode(ME.INTERNALNS+"atHeight", ME.INTERNALNS);
	
	//DBPedia stuff
		public static final SSSNode DBPediaColour =  SSSNode.createSSSNode("http://dbpedia.org/ontology/colour","http://dbpedia.org/ontology");
	
	
	public void setup(){
		
		//not needed? or do we need a refresh?
		
		
	}
	

}
