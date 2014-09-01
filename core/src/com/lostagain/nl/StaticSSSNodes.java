package com.lostagain.nl;

import com.darkflame.client.semantic.SSSNode;

/** A collection of static nodes that represent concepts in the game **/
public class StaticSSSNodes {
	//----
	public static final SSSNode VISIBLETO = SSSNode.createSSSNode(ME.INTERNALNS+"visibleto", ME.INTERNALNS);
	public static final SSSNode EVERYONE =  SSSNode.createSSSNode(ME.INTERNALNS+"everyone", ME.INTERNALNS);
	public static final SSSNode Computer =  SSSNode.createSSSNode(ME.INTERNALNS+"computer", ME.INTERNALNS);
	public static final SSSNode SecuredBy =  SSSNode.createSSSNode(ME.INTERNALNS+"SecuredBy", ME.INTERNALNS);
	public static final SSSNode UnlockedBy =  SSSNode.createSSSNode(ME.INTERNALNS+"UnlockedBy", ME.INTERNALNS);
	
	public static final SSSNode Security =  SSSNode.createSSSNode(ME.INTERNALNS+"Security", ME.INTERNALNS);
	public static final SSSNode queryPass =  SSSNode.createSSSNode(ME.INTERNALNS+"queryPass", ME.INTERNALNS);
	public static final SSSNode clueText =  SSSNode.createSSSNode(ME.INTERNALNS+"clueText", ME.INTERNALNS);
	//public static final SSSNode textPass =  SSSNode.createSSSNode(ME.INTERNALNS+"textPass", ME.INTERNALNS);
    public static final SSSNode ReqNum =  SSSNode.createSSSNode(ME.INTERNALNS+"ReqNum", ME.INTERNALNS);
	
	public static final SSSNode isOn =  SSSNode.createSSSNode(ME.INTERNALNS+"isOn", ME.INTERNALNS);
	
	//location content types
	public static final SSSNode software= SSSNode.createSSSNode(ME.INTERNALNS+"software", ME.INTERNALNS);
	public static final SSSNode messages= SSSNode.createSSSNode(ME.INTERNALNS+"messages", ME.INTERNALNS);
	
	

}
