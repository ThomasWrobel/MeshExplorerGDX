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
	public static final SSSNode visibleTo =  SSSNode.createSSSNode(ME.INTERNALNS+"visibleTo", ME.INTERNALNS);	
	public static final SSSNode queryPass =  SSSNode.createSSSNode(ME.INTERNALNS+"queryPass", ME.INTERNALNS);
	public static final SSSNode clueText =  SSSNode.createSSSNode(ME.INTERNALNS+"clueText", ME.INTERNALNS);
	
	//public static final SSSNode textPass =  SSSNode.createSSSNode(ME.INTERNALNS+"textPass", ME.INTERNALNS);
    public static final SSSNode ReqNum =  SSSNode.createSSSNode(ME.INTERNALNS+"ReqNum", ME.INTERNALNS);
	
	public static final SSSNode isOn =  SSSNode.createSSSNode(ME.INTERNALNS+"isOn", ME.INTERNALNS);
	
	//location content types
	public static final SSSNode software= SSSNode.createSSSNode(ME.INTERNALNS+"software", ME.INTERNALNS);
	public static final SSSNode messages= SSSNode.createSSSNode(ME.INTERNALNS+"messages", ME.INTERNALNS);
	
	//Language
	public static final SSSNode language =  SSSNode.createSSSNode(ME.INTERNALNS+"language", ME.INTERNALNS);
	public static final SSSNode writtenin =  SSSNode.createSSSNode(ME.INTERNALNS+"writtenin", ME.INTERNALNS);
	public static final SSSNode knows =  SSSNode.createSSSNode(ME.INTERNALNS+"knows", ME.INTERNALNS);
	
	//default language
	public static final SSSNode stdascii =  SSSNode.createSSSNode(ME.INTERNALNS+"stdascii",ME.INTERNALNS+"stdascii", ME.INTERNALNS,new SSSNode[]{language});

	//other languages
	public static final SSSNode scram1 =  SSSNode.createSSSNode(ME.INTERNALNS+"scram1",ME.INTERNALNS+"scram1", ME.INTERNALNS,new SSSNode[]{language});

	//Ability's
	public static final SSSNode ability= SSSNode.createSSSNode(ME.INTERNALNS+"ability", ME.INTERNALNS);
	
	public static final SSSNode decoder= SSSNode.createSSSNode(ME.INTERNALNS+"decoder", ME.INTERNALNS+"decoder", ME.INTERNALNS,new SSSNode[]{ability});
	
	//define decoders
	public static final SSSNode asciidecoder= SSSNode.createSSSNode ("Asciidecoder" , ME.INTERNALNS+"asciidecoder",  ME.INTERNALNS,new SSSNode[]{decoder});
	public static final SSSNodesWithCommonProperty knowsAscii = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(knows, stdascii, new SSSNode[]{asciidecoder});
			
	
	public static final SSSNode scram1decoder= SSSNode.createSSSNode("Scram1decoder", ME.INTERNALNS+"scram1decoder", ME.INTERNALNS,new SSSNode[]{decoder});
	public static final SSSNodesWithCommonProperty knowsScram1 = SSSNodesWithCommonProperty.createSSSNodesWithCommonProperty(knows, scram1, new SSSNode[]{scram1decoder});
	
	
	
	
	public void setup(){
		
		//at the moment SSSNodes can only be created as having a class or a bunch.
		//we cant yet put them in lists statically
		
		
	}
	

}
