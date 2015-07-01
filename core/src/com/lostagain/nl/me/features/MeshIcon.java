package com.lostagain.nl.me.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.GlowingSquareShader;

/** 
 * Will become a 3d icon that can be clicked to turn into a interactive meshFeature.
 * 
 *  Mesh icons are square, with a somewhat customizable border and background style.
 *  They have a short bit of text in the middle, maybe in future an icon representing their content type.
 *  
 *  They extend AnimatableModelInstance so we can animated them latter if we wish 
 * **/
public class MeshIcon extends AnimatableModelInstance  implements hitable, Animating {
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
	
	

	//various things to handle animation of appearing/disaspering
	enum FeatureState {
		appearing,disapearing,normal,hidden;
	}
	FeatureState currentState = FeatureState.hidden;
	protected float Opacity = 0f;
	float fadeDuration = 0.500f;
	float timeIntoFade = 0.0f;
	Runnable runAfterFadeIn = null;
	Runnable runAfterFadeOut = null;
	//The default mesh vertexs of this icon
	float[] IconsDefaultVertexs = null; //this is set on creation and lets us return to the correct geometry after resizing
	//The  mesh vertexes of this icon when its enlarged
	float[] IconsEnlargedVertexs = null; //this is set on creation and lets us return to the correct geometry after resizing
	
	
	
	//----------------------------------------------------
	
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
				
		//We also store the meshs vertexes after creation. This lets us animate between this and its enlarged form
		Mesh thisMesh = model.meshes.get(0);
		IconsDefaultVertexs = new float[thisMesh.getNumVertices() * thisMesh.getVertexSize()];
		model.meshes.get(0).getVertices(IconsDefaultVertexs);
		
		//Now the more tricky job, we need to work out the size of the associatedFeature, and use that to create new mesh vertex co-ordinates
		//for us to transform into when opening
		//We start by getting its minimum and maximum local co-ordinates
		Vector3 minXYZ = assocatiedFeature.getLocalBoundingBox().min;
		Vector3 maxXYZ = assocatiedFeature.getLocalBoundingBox().max;
		//We then use the X/Y to form a new set of co-ordinates
		IconsEnlargedVertexs = new float[] { 
											 minXYZ.x,minXYZ.y,0,
											 maxXYZ.x,minXYZ.y,0,
											 maxXYZ.x,maxXYZ.y,0,
											 minXYZ.x,maxXYZ.y,0,
										
										    };
		
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[0]+","+IconsEnlargedVertexs[1]+","+IconsEnlargedVertexs[2]);
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[3]+","+IconsEnlargedVertexs[4]+","+IconsEnlargedVertexs[5]);
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[6]+","+IconsEnlargedVertexs[7]+","+IconsEnlargedVertexs[8]);
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[9]+","+IconsEnlargedVertexs[10]+","+IconsEnlargedVertexs[11]);
		
		
		
		
		
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
        Material material = new Material("IconMaterial",
        		ColorAttribute.createDiffuse(DefaultColour), 
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f),
				new GlowingSquareShader.GlowingSquareAttribute(3f,Color.BLUE,DefaultColour,Color.WHITE));
        
		
		//work out half widths and heights
		float hw =  iconWidth/2;
		float hh = iconHeight/2;
		
		//we create the rectangle at negative half the width and height to positive half the width and height.
		//this ensures its center point is at 0,0
		Model model = ModelMaker.createRectangle(-hw, -hh, hw, hh, 0, material);
				
		
				
		return model;		
	}
	
	/**
	 * Sets the opacity of this icon (and its label)
	 * used currently as par tof its open/close animation
	 * 
	 * @param opacity
	 */
	public void setOpacity(float opacity){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("IconMaterial");
		((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;
		MeshIconsLabel.setOpacity(opacity);
		
		
	}
	
	
	/** triggers the icon to open showing its contents (assocatiedFeature) **/
	public void open(){
		
		if (currentState == FeatureState.hidden){
			Gdx.app.log(logstag,"opening mesh feature");
			animateOpen();
		} else {
			Gdx.app.log(logstag,"mesh feature state is:"+currentState);
		}
		
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
		startOpen(this.fadeDuration, runAfterFadeIn);
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
		Gdx.app.log(logstag,"_mesh icon clicked on_");
		open();
		
		
	}

	@Override
	public void fireTouchUp() {
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

	
	///-------------------------------------

	void startOpen(float duration, Runnable runAfterFadeIn) {
		currentState = FeatureState.appearing;
		Opacity = 0f;
		ModelManagment.addmodel(this, RenderOrder.zdecides);
		
		ModelManagment.addAnimating(this);
		this.runAfterFadeIn= runAfterFadeIn;
		
		this.assocatiedFeature.show();
	}


	void startClose(float duration, Runnable runAfterFadeOut) {
		currentState = FeatureState.disapearing;
		Opacity = 1f;
		ModelManagment.addAnimating(this);
		this.runAfterFadeOut= runAfterFadeOut;
		
		this.show();
	}


	/**
	 * This controls the appearing/disappearing of both the MeshIcon and its associated GenericMeshFeature
	 * We control both here so they can be perfectly sycned, and the feature has to worry about less stuff itself
	 * 
	 * @param delta
	 */
	void updateOpenCloseAnimation(float delta) {
		
		timeIntoFade = timeIntoFade+delta;
		float ratio  = timeIntoFade/fadeDuration;	
				
		//Remember the state is of the associated feature, not the icon itself.
		//The icon should be visible when the associated feature is hidden and visa-versa
		
		switch (currentState) {
		case appearing:
			Opacity = ratio;
			if (ratio>1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.normal;
				if (runAfterFadeIn!=null){
					runAfterFadeIn.run();
				}
				
			}
			break;
		case disapearing:
			Opacity = 1-ratio;
			if (ratio>1){
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.hidden;
				if (runAfterFadeOut!=null){
					
					runAfterFadeOut.run();
				}
			}
			break;
		case hidden:
			
			Opacity = 0f;
			this.assocatiedFeature.hide();
			this.show();
			ModelManagment.removeAnimating(this);
			
			return;
		case normal:
			
			Opacity = 1f;
			ModelManagment.removeAnimating(this);
			
			break;
		
		}
		
		//update associated icon
		this.assocatiedFeature.updateApperance(Opacity,currentState);
		
		//and our own appearance
		updateApperance(Opacity,currentState);
		
	}
	
	
	/**
	 * During the opening and closing animations this updates the appearance as it animations 
	 * 
	 * The current concept is it opens up to become the background of the assocatedFeature 
	 * 
	 * @param alpha - 0 = closed state 1 = open state
	 * @param currentState - specifies the direction of the animation right now
	 */
	private void updateApperance(float alpha,FeatureState currentState){
		/*
		
		

		Gdx.app.log(logstag,"nummeshs::"+nummeshs);

		

		
	//	int vertextsnum = this.model.meshes.items[0].getNumVertices();
		
		


		Mesh IconsMesh = this.model.meshes.get(0);
		int vertextsnum = IconsMesh.getNumVertices();

		Gdx.app.log(logstag,"vertextsnum::"+vertextsnum);

		int verteexsize = IconsMesh.getVertexSize();
		Gdx.app.log(logstag,"verteexsize::"+verteexsize);

		int indices = IconsMesh.getNumIndices();
		Gdx.app.log(logstag,"getNumIndices::"+indices);
		
		float[] vertexArray= new float[19];
		
		
		Gdx.app.log(logstag,"vertexs::"+vertexArray);
		
		IconsMesh.getVertices(vertexArray);
		
		Gdx.app.log(logstag,"vertexs x::"+vertexArray[0]);
		Gdx.app.log(logstag,"vertexs y::"+vertexArray[1]);
		Gdx.app.log(logstag,"vertexs z::"+vertexArray[2]);
		
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[3]);		
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[4]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[5]);
		
		Gdx.app.log(logstag,"vertexs::"+vertexArray[6]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[7]);	
		
		Gdx.app.log(logstag,"vertexs x::"+vertexArray[8]);		
		Gdx.app.log(logstag,"vertexs y::"+vertexArray[9]);		
		Gdx.app.log(logstag,"vertexs z::"+vertexArray[10]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[11]);
		
		Gdx.app.log(logstag,"vertexs::"+vertexArray[12]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[13]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[14]);
		
		Gdx.app.log(logstag,"vertexs::"+vertexArray[15]);
		Gdx.app.log(logstag,"vertexs ::"+vertexArray[16]);
		Gdx.app.log(logstag,"vertexs -:"+vertexArray[17]);		
		Gdx.app.log(logstag,"vertexs -:"+vertexArray[18]);
		*/
		int nummeshs = this.model.meshes.size;
		Mesh IconsMesh = this.model.meshes.get(0);
		
		final VertexAttribute posAttr = IconsMesh.getVertexAttribute(Usage.Position);
		final int offset = posAttr.offset / 4;
		final int numComponents = posAttr.numComponents;
		final int numVertices = IconsMesh.getNumVertices();
		final int vertexSize = IconsMesh.getVertexSize() / 4;

		final float[] vertices = new float[numVertices * vertexSize];
		IconsMesh.getVertices(vertices);
		int idx = offset;
		
		//float scale = (1.5f*alpha)+1.0f;

		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[0]+","+IconsEnlargedVertexs[1]+","+IconsEnlargedVertexs[2]);
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[3]+","+IconsEnlargedVertexs[4]+","+IconsEnlargedVertexs[5]);
	//	Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[6]+","+IconsEnlargedVertexs[7]+","+IconsEnlargedVertexs[8]);
		//Gdx.app.log(logstag,"Enl:"+IconsEnlargedVertexs[9]+","+IconsEnlargedVertexs[10]+","+IconsEnlargedVertexs[11]);
		
		//25-100
		
		//((75*alpha)+25
		
		//can be optimized latter by pre-calcing the size ratio and just multiply
		for (int i = 0; i < 12; i=i+3) {
			
			float IconX   = IconsDefaultVertexs[idx    ];
			float IconY   = IconsDefaultVertexs[idx + 1];
			float IconZ   = IconsDefaultVertexs[idx + 2];
			
			float TargetX = IconsEnlargedVertexs[i  ];
			float TargetY = IconsEnlargedVertexs[i+1];
			float TargetZ = IconsEnlargedVertexs[i+2];

			Gdx.app.log(logstag," Default ::"+IconX  +","+IconY  +","+IconZ        );
			Gdx.app.log(logstag," Target  ::"+TargetX+","+TargetY+","+TargetZ);
			
			float comboX = ((TargetX-IconX)*alpha)+IconX;
			float comboY = ((TargetY-IconY)*alpha)+IconY;
			float comboZ = ((TargetZ-IconZ)*alpha)+IconZ;
			
			Gdx.app.log(logstag," new::"+comboX+","+comboY+","+comboZ);
			
			//currently just scale up a bit
			vertices[idx    ] = comboX;
			vertices[idx + 1] = comboY;
			vertices[idx + 2] = comboZ;
			
			idx += vertexSize;
		}
			
		

		IconsMesh.setVertices(vertices);
		
		//fade label
		MeshIconsLabel.setOpacity(1-alpha);
		
		
		//start position of vertexs
		
		//end position of vertexs;
		
		//alpha between the two as the target;
		
		
		
		//update this icon		
		//this.setOpacity(1-Opacity); //we fade out as the other fades in!
		
		
	}


	public void setFadeDuration(float fadeDuration) {
		this.fadeDuration = fadeDuration;
	}
	
	
	public void updateAnimationFrame(float delta){
		updateOpenCloseAnimation(delta);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
