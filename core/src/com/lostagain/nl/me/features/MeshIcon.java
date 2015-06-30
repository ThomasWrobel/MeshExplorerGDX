package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.features.GenericMeshFeature.FeatureState;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;

/** 
 * Will become a 3d icon that can be clicked to turn into a interactive meshFeature.
 * 
 *  Mesh icons are square, with a somewhat customizable border and background style.
 *  They have a short bit of text in the middle, maybe in future an icon representing their content type.
 *  
 *  They extend AnimatableModelInstance so we can animated them latter if we wish 
 * **/
public class MeshIcon extends AnimatableModelInstance  implements hitable {
	final static String logstag = "ME.MeshIcon";

	//generic icon stuff
	public enum IconType {
		Email,
		Software,
		Links,
		Abilitys,
		Info,
		OTHER; //used as a catch all for unique features.
	}
	
	static final float iconWidth  = 100f; //standard width and height of all icons
	static final float iconHeight = 100f;
	
	private Label MeshIconsLabel;
	
	
	//this icons stuff
	IconType thisIconsType = null;
	Location parentLocation = null;
	
	GenericMeshFeature assocatiedFeature = null;
	
	float lastHitDistance =  -1f; //no hit by default//hitables need this
	/** 
	 * Creates a icon of the specified type.
	 * The idea is this is placed relative to the parent location (or rather the parent locations infoIcon which should always be at the center)
	 *  
	 * **/
	public MeshIcon(IconType type,Location parentLocation,GenericMeshFeature assocatiedfeature) {		
		super(generateBackgroundModel());
		
		thisIconsType = type;
		this.parentLocation = parentLocation;
		this.assocatiedFeature = assocatiedfeature;
		
		//set the associated feature to know this is its associated icon
		this.assocatiedFeature.setAssociatedIcon(this);
		
		//this icon will also position the feature so its attached at the center
		Vector3 featureCenter = this.assocatiedFeature.getCenter();
		super.attachThis(this.assocatiedFeature, new PosRotScale(featureCenter.x,featureCenter.y,featureCenter.z));
		
		
		//Now create a new label and attach it too ourselves

		String name = type.name();
		MeshIconsLabel = new Label(name);
		MeshIconsLabel.setLabelBackColor(Color.CLEAR);
		
		Vector3 labelCenter = MeshIconsLabel.getModel().getCenter();
		AnimatableModelInstance internalModel = MeshIconsLabel.getModel();
		
		ModelManagment.addHitable(this);
		
		
		super.attachThis(internalModel, new PosRotScale(-labelCenter.x,-labelCenter.y,5f));
		
		
		
	}

	/** 
	 * Generates the generic icon model 
	 * 
	 * Currently just a colored rectangle. 
	 * 
	 * **/
	static private Model generateBackgroundModel(){
		
				
		//make its material (this will change in future to something more pretty)
		//currently we have are square backing model, created below
		//A label is attached elsewhere as a separate object
		
		Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
		
		
		
		//for now, we just use a simple texture
        Material material = new Material(
        		ColorAttribute.createDiffuse(DefaultColour), 
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));
		
		//work out half widths and heights
		float hw =  iconWidth/2;
		float hh = iconHeight/2;
		
		//we create the rectangle at negative half the width and height to positive half the width and height.
		//this ensures its center point is at 0,0
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, material);
				
		
		return model;		
	}
	
	/** triggers the icon to open showing its contents (assocatiedFeature) **/
	public void open(){
		
	}
	
	/** triggers the icon to close hiding its contents (assocatiedFeature) **/
	public void close(){
		
	}
	
	/** runs the close animation.
	 * Should only be called by the associated GenericMeshFeature.
	 * Do not run from elsewhere **/
	public void animateClose(){
		
	}
	
	/** runs the open animation.
	 * Should only be called by itself, hence private **/
	private void animateOpen(){
		
	}

	//@Override
	//public int getRadius() {
		// TODO Auto-generated method stub
	//	return 0;
	//}

	@Override
	public PosRotScale getTransform() {
		return this.getTransform();
	}

	@Override
	public void fireTouchDown() {
		MeshIconsLabel.getModel().hide();
		Gdx.app.log(logstag,"_mesh icon clicked on_");
		
		if (assocatiedFeature.currentState == FeatureState.hidden){
			Gdx.app.log(logstag,"opening mesh feature");
			assocatiedFeature.open();
		} else {
			Gdx.app.log(logstag,"mesh feature state is:"+assocatiedFeature.currentState);
		}
		
	}

	@Override
	public void fireTouchUp() {
		MeshIconsLabel.getModel().show();
		Gdx.app.log(logstag,"_-fireTouchUp-_");
	}

	@Override
	public void setLastHitsRange(float range) {

		//Gdx.app.log(logstag,"setting hittable hit range to:"+range);
		lastHitDistance = range;
	}

	@Override
	public float getLastHitsRange() {
		
		return lastHitDistance;
	}

	@Override
	public boolean isBlocker() {
		return true;
	}

	@Override
	public boolean rayHits(Ray ray) {
		boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
		Gdx.app.log(logstag,"testing for hit on meshicon:"+hit);
		return hit;
	}

	
}
