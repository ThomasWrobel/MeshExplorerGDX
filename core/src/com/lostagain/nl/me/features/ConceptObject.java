package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.darkflame.client.semantic.SSSNode;
import com.lostagain.nl.ME;
import com.lostagain.nl.StaticSSSNodes;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.features.ConceptObjectSlot.SlotMode;
import com.lostagain.nl.me.gui.DataObjectDropTarget;
import com.lostagain.nl.me.gui.STMemory;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.shaders.DistanceFieldShader.DistanceFieldAttribute;
import com.lostagain.nl.shaders.MySorter;

/**
 * Concept objects will eventually replace data objects.
 * 
 * They are the visual representation of a concept. They can be dragged about, placed into the inventory, or used on other objects.
 * Enemys can drop them.
 * Some of them can even be opened up to reveal new functions and features - such as emails, links, locked data etc.
 * 
 * Essentially they are the heart of the game.
 * 
 * @author Tom
 *
 */
public class ConceptObject extends MeshIcon {

	final static String logstag = "ME.ConceptObject";
	final static float StandardConceptWidth  = 150;
	final static float StandardConceptHeight = 40;
	
	/** The semantic node this concept represents. 
	 * Thanks to the semantic database this is itself what stores all the information regarding what the object 
	 * can do. Everything else is just how to visually represent and move it about.
	 * **/
	
	public SSSNode itemsnode;

	/** if this object is stored in another object, this provides a link to it**/
	private ConceptObjectSlot storedin;
	
	/** if the mouse has gone down on this object, but we dont yet know if its going to pick it up (by dragging) or merely double clicking to itneract with it!**/
	boolean mouseDownOn = false;
	
	enum ConceptObjectStatus {

		
		/** if the user is currently holding this object (ie, dragging it) **/
		Holding,
		/** if this object is stored in another **/
		InObject,
		/** if this object is just on the environment, minding its own business **/
		OnEnviroment,
		/** if this object doesn't exist anywhere in the game world yet.**/
		NotUsed,
	}
	ConceptObjectStatus objectsStatus = ConceptObjectStatus.NotUsed;
	
	
	
	public ConceptObject(SSSNode conceptsNode) {

		//concepts are not squire, but instead have a more rectangular shape hence the size specified
		super(getType(conceptsNode),getTitle(conceptsNode),StandardConceptWidth,StandardConceptHeight, null, createFeatureForNode(conceptsNode)); //Note; ConceptObjects dont have a parent location as they can be moved about
		//ensure its on doubleclick to open mode, as single click is for dragging
		//(we can of course look for mouse down/up separately to also seperate single clicks and drags, but for now I think double click to open is more intuative anyway)
		super.iconsOpenMode = OpenMode.SingleClick;
		
		itemsnode = conceptsNode;
		
		
		//test the zindex
		super.setZIndex(150);
		
		
	}




	private static IconType getType(SSSNode conceptsNode) {
		//normally just a concept, but some special concepts (like abilitys) will have their own type and thus style.
		if (conceptsNode.isOrHasParentClass(StaticSSSNodes.ability.PURI)){
			return IconType.Ability;
		}
		
		
		return IconType.Concept;
	}



	
	private static String getTitle(SSSNode conceptsNode) {
		String croppedTitle = conceptsNode.getPLabel();
		if (croppedTitle.length()>15){
			croppedTitle = croppedTitle.substring(0, 15);
		}
		
		return croppedTitle; //in future check length and suitability for using as a title
	}




	/**
	 * generates a feature for this node.
	 * For most concepts this is just basic label with the nodes full URI.
	 * For other some concepts though it might be more elaborate things like a email box.
	 * @param conceptsNode
	 * @return
	 */
	private static GenericMeshFeature createFeatureForNode(SSSNode conceptsNode) {
		 String URIName = conceptsNode.getPLabel(); //just a basic label for now
		 String URI     = conceptsNode.getPURI();
		
		  
		return new InfoBox(URIName,URI,"");
		
	}



	@Override
	public void fireClick() {
		if (this.objectsStatus != ConceptObjectStatus.InObject){
			super.fireClick();
		}
	}


	@Override
	public void fireTouchDown() {		
		super.fireTouchDown();
		
		//If we are in a object right now we reset timeOfFirstClick==0 as we dont want double click events to fire
		if (this.objectsStatus == ConceptObjectStatus.InObject){
			 timeOfFirstClick=0; 
		}
		
		mouseDownOn=true;
		
	}
	
	@Override
	public void fireDragStart(){
		
		//only drag if not open
		if (currentState==FeatureState.FeatureClosed){
			triggerPickedUp(); 	
		}
	}

	@Override
	public void fireTouchUp() {		
		Gdx.app.log(logstag,"_-fireTouchUp on concept-_");	
		if (objectsStatus == ConceptObjectStatus.Holding){
			triggerPutDown();
		} else {
			super.fireTouchUp();
		}
	}

	public void triggerPutDown() {
		Gdx.app.log(logstag,"____dataobject put down ");
		 STMemory.dropHeldItem(true);
		
		 
	//	 ModelManagment.mysorter.testSort(); //temp test of sort (remove)
	}
	
	
	

	public void triggerPickedUp() {
		Gdx.app.log(logstag,"____dataobject picked up ");
		
				
		if (storedin!=null){	
			
			boolean success = storedin.onDrag();
			if (!success){
				return; //we wernt allowed to remove it
			}
			//cancel if locked			
			//storedin.onDrag(this); //needs updating
			storedin=null;
		}
		
		
		//tell the game we are dragging this item atm.
		
		this.objectsStatus = ConceptObjectStatus.Holding;
		
		STMemory.holdItem(this);
		//this.hide();
		///ensure the scene drag is disabled while we are dragging a object
		ME.disableDrag();
	}




	/**
	 * Returns the objects texture.
	 * Likely fairly useless unless the same shader is used to render it elsewhere.
	 * This function is just a temp really. Probably switch the helditem function to really move the object, rather then using a cursor that looks like it**/
	public Texture getObjectsTexture() {

		Material infoBoxsMaterial = MeshIconsLabel.getMaterial("LabelMaterial");
		
		TextureAttribute style = ((TextureAttribute)infoBoxsMaterial.get(TextureAttribute.Diffuse));
		
		
		
		
		
		return style.textureDescription.texture;
	}


	public void setAsAttachedToObject(ConceptObjectSlot slot) {
		this.storedin = slot;
		this.objectsStatus=ConceptObjectStatus.InObject;
		
		
	}

	public void setAsDropped() {
		storedin=null;
		this.objectsStatus=ConceptObjectStatus.OnEnviroment;
		
	}





	
}
