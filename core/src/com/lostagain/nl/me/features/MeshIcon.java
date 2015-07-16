package com.lostagain.nl.me.features;

import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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
import com.badlogic.gdx.utils.TimeUtils;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.GlowingSquareShader;
import com.lostagain.nl.shaders.GlowingSquareShader.GlowingSquareAttribute;

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
		ConceptStore,
		AbilityStore,
		Links,
		Abilitys,
		Info,
		Concept, //Used as a generic concept object (Note this might change when first opened and its discovered to be a email, software etc inside?)
		LocationHub,
		OTHER;  //used as a catch all for unique features.
	}
	
	
	public enum OpenMode {
		SingleClick,DoubleClick
	}
	
	/** if on doubleclick mode this is the time since the first click **/
	protected long timeOfFirstClick=0;
	private static long MAXDOUBLECLICKTIME = 300; //ms
	
	
	/** Do you need to single click or double click to open this icon?**/
	OpenMode iconsOpenMode = OpenMode.DoubleClick;
	
	
	static final float iconWidth  = 100f; //standard width and height of all icons
	static final float iconHeight = 100f;
	
	protected Label MeshIconsLabel;
	
	
	
	//this icons stuff
	IconType thisIconsType = null;
	Location parentLocation = null;
	
	GenericMeshFeature assocatiedFeature = null;
	
	float lastHitDistance =  -1f; //no hit by default//hitables need this
	
	

	//various things to handle animation of appearing/disaspering
	enum FeatureState {
		appearing,disapearing,FeatureOpen,FeatureClosed;
	}
	FeatureState currentState = FeatureState.FeatureClosed;
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
	public MeshIcon(IconType type,Location parentLocation,GenericMeshFeature assocatiedfeature) {	
		this(type,null,iconWidth,iconHeight,parentLocation,assocatiedfeature);
	}
	/** 
	 * Creates a icon of the specified type.
	 * The idea is this is placed relative to the parent location (or rather the parent locations infoIcon which should always be at the center)
	 *  
	 * **/
	public MeshIcon(IconType type,String specificName,float w,float h,Location parentLocation,GenericMeshFeature assocatiedfeature) {		
		super(generateBackgroundModel(w,h));
		
		thisIconsType = type;
		this.parentLocation = parentLocation;
		this.assocatiedFeature = assocatiedfeature;
		
		setupAssociatedFeature();
		
		
		
		//We also store the meshs vertexes after creation. This lets us animate between this and its enlarged form
		Mesh thisMesh = model.meshes.get(0);
		IconsDefaultVertexs = new float[thisMesh.getNumVertices() * thisMesh.getVertexSize()];
		model.meshes.get(0).getVertices(IconsDefaultVertexs);
		
	
	
		
		
		
		//Now create a new label and attach it too ourselves
		String name = "";
		if (specificName==null){
			name = type.name();
		} else {
			name=specificName;
		}
		MeshIconsLabel = new Label(name);
		MeshIconsLabel.setLabelBackColor(Color.CLEAR);
		
		//we also need to scale the label to fit as it might be too long
		if (MeshIconsLabel.getWidth()>w){
			//10/5
			float ratio = MeshIconsLabel.getWidth()/w;
			float newWidth = w;
			float newHeight = MeshIconsLabel.getHeight() / ratio;
			MeshIconsLabel.setSizeAs(newWidth, newHeight);
			
		}
		
		
		Vector3 labelCenter = MeshIconsLabel.getCenter();
		//AnimatableModelInstance internalModel = MeshIconsLabel.getModel();
		
		ModelManagment.addHitable(this);
		
		
		super.attachThis(MeshIconsLabel, new PosRotScale(-labelCenter.x,-labelCenter.y,5f));
		

		
	}
	private void setupAssociatedFeature() {
		//associated features should be hidden by default
		assocatiedFeature.hide();
				
		//set the associated feature to know this is its associated icon
		//this.assocatiedFeature.setAssociatedIcon(this);
		
		//this icon will also position the feature so its attached at the center
		Vector3 featureCenter = this.assocatiedFeature.getCenter(); //5,5
		
		/** objects are attached slightly above the icon, as this helps with blending issues**/
		float vertDisplacement = 7f;
				
		//attach (our middle point is 0,0,0 but we don't know where the features middle point is, so we subtrack is center value
		//from the location we are attaching it too.
		//This means it should look centralized.
		//ie. If it has its center at 5,5 we position it at -5,-5 so that the "center" 5,5 point is in the middle
		super.attachThis(this.assocatiedFeature.getAnimatableModelInstance(), new PosRotScale(-featureCenter.x,-featureCenter.y,-featureCenter.z+vertDisplacement));
				
		//We need to work out the size of the associatedFeature, and use that to create new mesh vertex co-ordinates
		//for us to transform into when opening
		//We start by getting its minimum and maximum local co-ordinates
		cacheAssociatedFeaturesSize();
	}
	
	
	
	private void cacheAssociatedFeaturesSize() {
		//we need to work out the size of the associatedFeature, and use that to create new mesh vertex co-ordinates
		//for us to transform into when opening
		//We start by getting its minimum and maximum local co-ordinates
		
		Vector3 minXYZ    = assocatiedFeature.getLocalBoundingBox().min;
		Vector3 maxXYZ    = assocatiedFeature.getLocalBoundingBox().max;
		Vector3 centerXYZ = assocatiedFeature.getCenter();
		
		//We then use the X/Y to form a new set of co-ordinates, normalised around the center point of the associatedfeature
		float ox = centerXYZ.x;
		float oy = centerXYZ.y;
		
	
		IconsEnlargedVertexs = new float[] { 
											 minXYZ.x-ox,minXYZ.y-oy,0,
											 maxXYZ.x-ox,minXYZ.y-oy,0,
											 maxXYZ.x-ox,maxXYZ.y-oy,0,
											 minXYZ.x-ox,maxXYZ.y-oy,0,
										
										    };
	}

	/** 
	 * Generates the generic icon model 
	 * 
	 * Currently just a colored rectangle. 
	 * @param iconheight2 
	 * @param iconwidth2 
	 * 
	 * **/
	static private Model generateBackgroundModel(float iconwidth, float iconheight){
		
		
				
		//make its material (this will change in future to something more pretty)
		//currently we have are square backing model, created below
		//A label is attached elsewhere as a separate object
		
		Color DefaultColour = new Color(0.3f,0.3f,1f,0.5f);
		
		//for now, we just use a simple texture
        Material material = new Material("IconMaterial",
				new GlowingSquareShader.GlowingSquareAttribute(3f,Color.BLUE,DefaultColour,Color.WHITE),
				new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,1.0f));
        
		
		//work out half widths and heights
		float hw =  iconwidth/2;
		float hh =  iconheight/2;
		
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
	
	/**
	 * Sets the background of this icon (and its label)
	 * used currently as par tof its open/close animation
	 * 
	 * @param opacity
	 */
	public void setBackgroundColor(Color bak){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial("IconMaterial");
		GlowingSquareAttribute attribute = ((GlowingSquareShader.GlowingSquareAttribute)infoBoxsMaterial.get( GlowingSquareShader.GlowingSquareAttribute.ID));
		attribute.backColor = bak;
		
		
	}
	
	/** triggers the icon to open showing its contents (assocatiedFeature) **/
	public void open(){
		
		if (currentState == FeatureState.FeatureClosed){
			Gdx.app.log(logstag,"opening mesh feature");
			animateOpen();
		} else {
			Gdx.app.log(logstag,"mesh feature state is:"+currentState);
		}
		
	}
	
	/** triggers the icon to close hiding its contents (assocatiedFeature) **/
	public void close(){
		
		if (currentState == FeatureState.FeatureOpen){
			Gdx.app.log(logstag,"closing mesh feature");
			animateClose();
		} else {
			Gdx.app.log(logstag,"mesh feature state is:"+currentState);
		}
	}
	
	/** runs the close animation.
	 * This can be called by MeshIcon only if the associated feature is not a clickblocker and allows interaction 
	 * with the meshicon behind it. Else the associated feature will need to call this itself.
	 * Do not run from elsewhere **/
	public void animateClose(){
		startClose(this.fadeDuration, runAfterFadeOut);
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
		return this.getTransform(); //hu? error!
	}

	/*****/
	@Override
	public void fireTouchDown() {
		Gdx.app.log(logstag,"_mesh icon clicked on_");
		
		if (iconsOpenMode==OpenMode.SingleClick){
			open();
		}
		
		if (iconsOpenMode==OpenMode.DoubleClick){
			
			if (timeOfFirstClick==0){
				
				//if timeOfFirstClick is not set yet, we set it to the current time
				timeOfFirstClick = TimeUtils.millis();
				Gdx.app.log(logstag,"mesh icon clicked on at:"+timeOfFirstClick);
				
			} else {
				long currentTime = TimeUtils.millis();
				long elipsedTime = currentTime -timeOfFirstClick;
				Gdx.app.log(logstag,"mesh icon clicked on at:"+currentTime+"( last was "+timeOfFirstClick);
				Gdx.app.log(logstag,"elipsedTime:"+elipsedTime);
				
				//else we look at the time difference and if its less then the double click max gap we trigger the open command
				if (elipsedTime < MAXDOUBLECLICKTIME){
					timeOfFirstClick=0;
					if (currentState == FeatureState.FeatureClosed){
						open();
					} else if (currentState == FeatureState.FeatureOpen){
						
						close();
					}
					
				} else {
					timeOfFirstClick=currentTime;
				}
				
			}
		}
		
		
		
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
		timeIntoFade=0f;
		ModelManagment.addmodel(this, RenderOrder.zdecides);
		
		ModelManagment.addAnimating(this);
		this.runAfterFadeIn= runAfterFadeIn;
		
		this.assocatiedFeature.show();
	}


	void startClose(float duration, Runnable runAfterFadeOut) {
		currentState = FeatureState.disapearing;
		Opacity = 1f;
		timeIntoFade=0f;
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
				Opacity = 1;
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.FeatureOpen;
				if (runAfterFadeIn!=null){
					runAfterFadeIn.run();
				}
				
			}
			break;
		case disapearing:
			Opacity = 1-ratio;
			if (ratio>1){
				Opacity = 0;
				ModelManagment.removeAnimating(this);
				currentState = FeatureState.FeatureClosed;
				this.assocatiedFeature.hide();
				this.show();
				if (runAfterFadeOut!=null){					
					runAfterFadeOut.run();
				}
			}
		break;
		case FeatureClosed:
			
			Opacity = 0f;
			this.assocatiedFeature.hide();
			this.show();
			ModelManagment.removeAnimating(this);
			
			return;
		case FeatureOpen:
			
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
	@Override
	public void fireDragStart() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/**
	 * Lets you change the feature associated with this icon when its double clicked
	 */
	protected void setAssociatedFeature(GenericMeshFeature newfeature){
		//remove old one
		this.removeAttachment(assocatiedFeature.getAnimatableModelInstance());
		//setup new one
		this.assocatiedFeature = newfeature;		
		setupAssociatedFeature();
		
	}
	
	public void refreshAssociatedFeature() {
		setupAssociatedFeature();
	}
	
	/**
	 * Icons linked to this one by lines
	 */
	HashMap<MeshIcon,AnimatableModelInstance> linkedIcons = new HashMap<MeshIcon,AnimatableModelInstance>();
	
	/**
	 * Sets a glowing line between this icon and another
	 */
	public void addLineTo(MeshIcon target){
		

		Gdx.app.log(logstag,"Adding new connecting line");
		AnimatableModelInstance Linksline = ModelMaker.addConnectingLine(this, target);
		linkedIcons.put(target,Linksline);
		Linksline.setInheritedRotation(false);
		
		this.attachThis(Linksline, new PosRotScale(0,0,-10f)); //a little behind this icon
		
		
		ModelManagment.addmodel(Linksline,ModelManagment.RenderOrder.zdecides);
		
		
		
		
	}
	
	/**
	 * update line lengths. 
	 * Not used yet, when this object moves, or things it links to moves, the lengths of the connecting lines will need to be recalculated
	 **/
	private void updateLineLengths(){
		//Note; No need to update position or rotation as thats handled automatically by it being attached + set to look at
		
	}
	
	/**
	 * Sets a glowing line between this icon and another
	 */
	public void removeLineTo(MeshIcon target){
		
		this.removeAttachment(linkedIcons.get(target));
		
		linkedIcons.remove(target);
	}
	
	
	
	
	
	
}
