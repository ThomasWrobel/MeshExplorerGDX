package com.lostagain.nl.me.features;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
import com.lostagain.nl.ME;
import com.lostagain.nl.GWTish.Label;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.locationFeatures.Location;
import com.lostagain.nl.me.models.Animating;
import com.lostagain.nl.me.models.MessyModelMaker;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.models.Moving;
import com.lostagain.nl.me.models.hitable;
import com.lostagain.nl.me.models.objectType;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NewForward;
import com.lostagain.nl.me.newmovements.NewMovement;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.GwtishWidgetBackgroundAttribute;
import com.lostagain.nl.shaders.MySorter;
import com.lostagain.nl.shaders.MySorter.ZIndexAttribute;

/** 
 * Will become a 3d icon that can be clicked to turn into a interactive meshFeature.
 * 
 *  Mesh icons are square, with a somewhat customizable border and background style.
 *  They have a short bit of text in the middle, maybe in future an icon representing their content type.
 *  
 *  They extend AnimatableModelInstance so we can animated them latter if we wish 
 * **/
public class MeshIcon extends AnimatableModelInstance  implements  Animating,Moving {

	private static final String ICON_MATERIAL = "IconMaterial";

	final static String logstag = "ME.MeshIcon";

	static MeshIcon currentlyOpen = null;

	//generic icon stuff
	public enum IconType {
		Email("Email",new Color(0.1f,0.1f,0.6f,0.9f)),
		EmailHub("Email\nHub",new Color(0.11f,0.11f,0.7f,0.88f)),
		ConceptStore("Concepts",Color.GREEN),
		AbilityStore("Abilities",new Color(1.0f,0.3f,0.1f,0.7f)),
		AbilityInstaller("Ability\nInstaller",new Color(1.0f,0.5f,0.1f,0.7f)),
		LinkStore("Links",Color.PURPLE),
		Ability(new Color(1.0f,0.3f,0.1f,0.7f)),
		Info,
		Concept, //Used as a generic concept object (Note this might change when first opened and its discovered to be a email, software etc inside?)
		LocationHub("Location\nHub"), //note the new line
		RequestScreen("Sealed"),
		OTHER;  //used as a catch all for unique features.


		String labelName = "";
		Color iconColor;
		IconType(){
			this("");
		}
		IconType(String label){
			labelName=label;
		}
		IconType(String label,Color col){
			labelName=label;
			iconColor = col;
		}
		IconType(Color col){
			iconColor = col;
		}
		public String getLabelName() {
			if (labelName.isEmpty()){
				return this.name(); //default is name of enum;
			}
			return labelName;
		}
		public Color getIconColour() {
			return iconColor;
		}
	}


	public enum OpenMode {
		SingleClick,DoubleClick
	}

	/** if on doubleclick mode this is the time since the first click **/
	protected long timeOfFirstClick=0;
	private static long MAXDOUBLECLICKTIME = 300; //ms


	/** Do you need to single click or double click to open this icon?**/
	OpenMode iconsOpenMode = OpenMode.SingleClick;


	static final float defaultIconWidth  = 100f; //standard width and height of all icons
	static final float defaultIconHeight = 100f;
	private static final float LabelMargin = 12f; //we need this as the text hovers above the icon ad thus the paralax can move iut outside the edge if we dont have plent of padding

	private final Runnable SizeChangeHandler = new Runnable(){
		@Override
		public void run() {

			Gdx.app.log(logstag,"refreshing icon mesh due to size change");
			refreshAssociatedFeature();
		}
	};


	public Label MeshIconsLabel;


	

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


	/**
	 * Controls the movements of this icon (default null, its created when needed then kept)
	 */
	NewMovementController movementController;

	/**
	 * Stores the native position of this icon, so when it moves it can return to its native pos
	 **/
	Vector3 nativePosition = new Vector3();

	/**
	 * when opened, how high does this thing raise up?
	 */
	float OpenHeightDisplacement = 15f;


	/**
	 * 
	 * @param type
	 * @param parentLocation
	 * @param assocatiedfeature
	 */



	//----------------------------------------------------
	public MeshIcon(IconType type,Location parentLocation,GenericMeshFeature assocatiedfeature) {	
		this(type,null,defaultIconWidth,defaultIconHeight,parentLocation,assocatiedfeature);
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
		//store the currentAssociatedFeature as the default as well.
		//if a feature is added and removed, this one will be displayed as the default
		defaultAssociatedFeature = this.assocatiedFeature;

		setupAssociatedFeature();



		//We also store the meshs vertexes after creation. This lets us animate between this and its enlarged form
		Mesh thisMesh = model.meshes.get(0);
		IconsDefaultVertexs = new float[thisMesh.getNumVertices() * thisMesh.getVertexSize()];
		model.meshes.get(0).getVertices(IconsDefaultVertexs);


		//set the icon color if not default
		if (type.getIconColour()!=null){
			setBackgroundColour(type.getIconColour());
		}




		//Now create a new label and attach it too ourselves
		String name = "";
		if (specificName==null){
			name = type.getLabelName();
		} else {
			name=specificName;
		}


		MeshIconsLabel = new Label(name);
		MeshIconsLabel.setLabelBackColor(Color.CLEAR);

		//we also need to scale the label to fit as it might be too long
		if ((MeshIconsLabel.getWidth()+(LabelMargin*2))>w){
			//10/5
			//float ratio = (MeshIconsLabel.getWidth()-(LabelMargin*2)) / w;
			float oldWidth = MeshIconsLabel.getWidth();
			float newWidth = w-(LabelMargin*2);
			float ratio = oldWidth/newWidth;
			
			float newHeight = MeshIconsLabel.getHeight() / ratio;
			MeshIconsLabel.setSizeAs(newWidth, newHeight);

		}


		Vector3 labelCenter = MeshIconsLabel.getPivotsDisplacementFromCenterOfBoundingBox();
		//AnimatableModelInstance internalModel = MeshIconsLabel.getModel();
		super.setAsHitable(true);

		//Note; we dont need to take margin into account as we are position center relative to center
		super.attachThis(MeshIconsLabel, new PosRotScale(-labelCenter.x,-labelCenter.y,5f)); //hover above a bit



	}


	/**
	 * sets the background color to the default for this icon type
	 */
	public void setToDefaultBackColour() {
		setBackgroundColour(this.thisIconsType.getIconColour());
	}

	//NOTE: associated features should only be tied to one meshicon.
	//Assigning to multiple can mess things up, features being hidden at the wrong time etc.
	//Perhaps features need a optional "parentMeshIcon" interface?
	private void setupAssociatedFeature() {

		Gdx.app.log(logstag,"setting up associated feature. currentState="+currentState);

		//associated features should be hidden by default if we are closed
		if (currentState == FeatureState.FeatureClosed){

			Gdx.app.log(logstag,"hidding "+assocatiedFeature.getClass()+" feature");
			assocatiedFeature.hide();

		}

		//set the associated feature to know this is its associated icon
		//this.assocatiedFeature.setAssociatedIcon(this);

		assocatiedFeature.getAnimatableModelInstance().wasResized();

		//this icon will also position the feature so its attached at the center
		//Vector3 featureCenter = this.assocatiedFeature.getCenterOfBoundingBox(); 
		//Vector3 ourCenter = this.getCenterOfBoundingBox(); 


		/** objects are attached slightly above the icon, as this helps with blending issues**/
		float vertDisplacement = 15f;


		//We need to work out the size of the associatedFeature, and use that to create new mesh vertex co-ordinates
		//for us to transform into when opening
		//We start by getting its minimum and maximum local co-ordinates
		cacheAssociatedFeaturesSize();

		//if we are open we also have to update the current size to match that maximized size
		if (this.currentState==FeatureState.FeatureOpen){
			updateApperance(1,FeatureState.FeatureOpen);
		}

		//attach (our middle point is 0,0,0 but we don't know where the features middle point is, so we subtrack is center value
		//from the location we are attaching it too.
		//This means it should look centralized.
		//ie. If it has its center at 5,5 we position it at -5,-5 so that the "center" 5,5 point is in the middle

		//	Gdx.app.log(logstag,"featureCenter x:"+featureCenter.x+" (out of:"+assocatiedFeature.getWidth());
		//Gdx.app.log(logstag,"featureCenter y:"+featureCenter.y+" (out of:"+assocatiedFeature.getHeight());


		//	Gdx.app.log(logstag,"target pivot x:"+super.getTransform().position.x +" (w of:"+super.getWidth());
		//	super.wasResized();			
		//	Gdx.app.log(logstag,"target pivot x:"+super.getTransform().position.x +" (w of:"+super.getWidth());
		//	Gdx.app.log(logstag,"box:"+super.getLocalCollisionBox());

		//need to align assocatiedFeatures center to our center
		//to do thiss we need its pivots offset
		//	assocatiedFeature.getAnimatableModelInstance().wasResized();

		//Vector3 ourDisplacement =  getPivotsDisplacementFromCenterOfBoundingBox();		
		Vector3 pivotDisplacement =  assocatiedFeature.getAnimatableModelInstance().getPivotsDisplacementFromCenterOfBoundingBox();

		//Gdx.app.log(logstag,"pivotDisplacement:"+pivotDisplacement);
		//Gdx.app.log(logstag,"ourDisplacement:"+ourDisplacement);

		//Gdx.app.log(logstag,"ourCenter :"+new PosRotScale(-ourCenter.x,-ourCenter.y,-ourCenter.z+vertDisplacement));

		//	Vector3 diff = ourCenter.sub(featureCenter);
		//	Gdx.app.log(logstag,"target attach point:"+new PosRotScale(-diff.x,-diff.y,-diff.z+vertDisplacement));

		//	super.attachThis(assocatiedFeature.getAnimatableModelInstance(), new PosRotScale(-featureCenter.x,-featureCenter.y,-featureCenter.z+vertDisplacement));
		super.attachThis(assocatiedFeature.getAnimatableModelInstance(), new PosRotScale(-pivotDisplacement.x,-pivotDisplacement.y,-pivotDisplacement.z+vertDisplacement));

		//feature needs zindex attribute set if this icon has had one set
		if (this.baseZindex>-1){
			assocatiedFeature.setZIndex(baseZindex+1,Zindexgroup);
		}
		//we need to keep track of any size changes on the associated feature to recache the above if needed

		Gdx.app.log(logstag,"adding size change monitor");
		assocatiedFeature.addOnSizeChangeHandler(SizeChangeHandler);

		//ensure parent is set (should be already)
		assocatiedFeature.setParentMeshIcon(this);

	}



	//seems to fire twice sometimes, probably due to unneeded duplicae change handlers elsewhere?
	private void cacheAssociatedFeaturesSize() {

		Gdx.app.log(logstag,"cacheing mesh size:"+assocatiedFeature.getWidth()+","+assocatiedFeature.getHeight());

		//we need to work out the size of the associatedFeature, and use that to create new mesh vertex co-ordinates
		//for us to transform into when opening
		//We start by getting its minimum and maximum local co-ordinates

		Vector3 minXYZ    = assocatiedFeature.getLocalBoundingBox().min;
		Vector3 maxXYZ    = assocatiedFeature.getLocalBoundingBox().max; //what if its scaled?
		Vector3 centerXYZ = assocatiedFeature.getCenterOfBoundingBox(); //what if its scaled? use getCenterOfBoundingBoxScaled

		//We then use the X/Y to form a new set of co-ordinates, normalized around the center point of the associatedfeature
		float ox          = centerXYZ.x;
		float oy          = centerXYZ.y;


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

		//for now, we just use a simple texture //Color.BLUE,
		Material material = new Material(ICON_MATERIAL,
				new GwtishWidgetBackgroundAttribute(1f,DefaultColour,Color.WHITE,3f),
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
		Material infoBoxsMaterial = this.getMaterial(ICON_MATERIAL);
		((BlendingAttribute)infoBoxsMaterial.get(BlendingAttribute.Type)).opacity = opacity;

		MeshIconsLabel.setOpacity(opacity);


	}
	
	
	/**
	 * the base zindex
	 * sub objects should be higher then this (like anything attached)
	 */
	int baseZindex = -1; //not set
	String Zindexgroup = "";
	/**
	 * adds a z index override and sets it to the supplied values.
	 * It then should render "larger index ontop of smaller" within the same group
	 * Use "global" as the group to ignore grouping and ordering over every other index
	 *  
	 * use clear to revert to natural ordering
	 * @param opacity
	 */
	public void setZIndex(int index, String group){
		baseZindex  = index;
		Zindexgroup = group;
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(ICON_MATERIAL);		
		infoBoxsMaterial.set(new ZIndexAttribute(index,group));
		
		
		Material infoBoxsMaterial2 = MeshIconsLabel.getTextMaterial();
		infoBoxsMaterial2.set(new ZIndexAttribute(index+1,group));
		
		//if theres a attachment
		if (this.assocatiedFeature!=null){
			assocatiedFeature.setZIndex(baseZindex+1,Zindexgroup);
			
		}
	}
	
	
	static int uniqueNamesGeneratedCount = 0;
	String uniqueIconName = "";
	/**
	 * used to ID this mesh icon on the scene.
	 * In future this might be refractored elsewhere if more things need it.
	 * Name formula is;
	 * 
	 * IconType.labelName + _ + currentCountOfTimesThisFunctionCalled
	 * @return
	 */
	public String getUniqueName(){
		
		if (uniqueIconName.isEmpty()){
			//generate one
			uniqueNamesGeneratedCount ++;
			uniqueIconName = thisIconsType.getLabelName()+"_"+uniqueNamesGeneratedCount;
			return uniqueIconName;
		} else {
			return uniqueIconName;
		}
		
		
		
	}
	
	/**
	 * adds a z index override and sets it to the supplied value
	 * use clear to revert to natural ordering
	 * @param opacity
	 */
	public void clearZIndex(int index){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(ICON_MATERIAL);		
		infoBoxsMaterial.remove(ZIndexAttribute.ID);
		

		Material infoBoxsMaterial2 = MeshIconsLabel.getMaterial();	
		infoBoxsMaterial2.remove(ZIndexAttribute.ID);
	}
	
	/**
	 * Sets the background of this icon (and its label)
	 * used currently as par tof its open/close animation
	 * 
	 * @param opacity
	 */
	public void setBackgroundColour(Color bak){
		//get the material from the model
		Material infoBoxsMaterial = this.getMaterial(ICON_MATERIAL);
		GwtishWidgetBackgroundAttribute attribute = ((GwtishWidgetBackgroundAttribute)infoBoxsMaterial.get( GwtishWidgetBackgroundAttribute.ID));
		attribute.backColor = bak;


	}

	/** triggers the icon to open showing its contents (assocatiedFeature) **/
	public void open(){

		if (currentState == FeatureState.FeatureClosed){

			if (currentlyOpen!=null && currentlyOpen!=this){
				//if another was open close it,unless its the email hub and we are an email
				//as things shouldn't close their parents. (in future we might want a generic system to prevent objects from
				//closing their parents
				if (this.thisIconsType == IconType.Email && currentlyOpen.thisIconsType == IconType.EmailHub ){
					//do nothing
				} else {
					currentlyOpen.close();
				}
			}
			currentlyOpen=this;

			//Perhaps make optional?
			//Centralize camera on this icon
			//For LocationHubs we put the camera a bit higher up so all the spokes can be seen
			if (thisIconsType==MeshIcon.IconType.LocationHub){
				ME.centerViewOn(this,ScreenUtils.getSuitableDefaultCameraHeight()+170,1000); //location hub zooms out a bit
			} else {
				//	ME.centerViewOn(this,ScreenUtils.getSuitableDefaultCameraHeight(),1000);

				Vector3 targetPosition = assocatiedFeature.getDefaultCameraPosition();
				ME.centerViewOn(targetPosition,1000);
			}




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





	/*****/
	@Override
	public void fireClick() {

		Gdx.app.log(logstag,"__touchdown on mesh icon at position="+this.getLocalCollisionBox());

		if (iconsOpenMode==OpenMode.SingleClick){
			
			if (currentState == FeatureState.FeatureClosed){
				open();
			} else if (currentState == FeatureState.FeatureOpen){
				close();
			}
			
			return;
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
	public void fireTouchDown() {
		Gdx.app.log(logstag,"_-fireTouchUp-_");
	}



	/**
	 * does this object block whats behind it?
	 * @return
	 */
	@Override
	public objectType getInteractionType() {
		return objectType.Blocker;
	}

	//@Override
	//public boolean rayHits(Ray ray) {
	//	boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
	//	Gdx.app.log(logstag,"testing for hit on meshicon:"+hit);
	//	return hit;
	//}


	///-------------------------------------

	void startOpen(float duration, Runnable runAfterFadeIn) {

		currentState = FeatureState.appearing;
		Opacity = 0f;
		timeIntoFade=0f;

		//store our native position so we can return to it when closing
		nativePosition = super.transState.position.cpy();

		//ModelManagment.addmodel(this, RenderOrder.zdecides);
		this.show();
		ModelManagment.addAnimating(this);
		this.runAfterFadeIn= runAfterFadeIn;

		this.assocatiedFeature.show();
		Gdx.app.log(logstag,"startOpen. assocatiedFeature is vis "+assocatiedFeature.isVisible());

	}


	void startClose(float duration, Runnable runAfterFadeOut) {
		Gdx.app.log(logstag,"startClose");
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
				//recalc size (for bounding box)
				currentState = FeatureState.FeatureOpen;
				wasResized();

				Gdx.app.log(logstag,"currentState set to:"+currentState);
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
				//recalc size (for bounding box)				
				currentState = FeatureState.FeatureClosed;
				this.assocatiedFeature.hide();				
				this.show();
				wasResized();
				Gdx.app.log(logstag,"currentState set to:"+currentState);
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
			Gdx.app.log(logstag,"currentState is:"+currentState);
			return;
		case FeatureOpen:

			Opacity = 1f;
			ModelManagment.removeAnimating(this);
			Gdx.app.log(logstag,"currentState is :"+currentState);
			break;

		}

		//we also change our position slightly, moving this MeshIcon (and its attachments) upwards a bit so as to be infront of anything else thats on the 0 z position)
		//(ie, other icons)
		this.transState.position.z = nativePosition.z + (OpenHeightDisplacement*Opacity); //12 is how far we move
		this.sycnTransform();		//as we are only moving in Z changing the transtates position and calling sycn directly is more efficiant then using setToPosition


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
	 **/
	private void updateApperance(float alpha,FeatureState currentState){

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

			//Gdx.app.log(logstag," Default ::"+IconX  +","+IconY  +","+IconZ        );
			//	Gdx.app.log(logstag," Target  ::"+TargetX+","+TargetY+","+TargetZ);

			float comboX = ((TargetX-IconX)*alpha)+IconX;
			float comboY = ((TargetY-IconY)*alpha)+IconY;
			float comboZ = ((TargetZ-IconZ)*alpha)+IconZ;

			//Gdx.app.log(logstag," new::"+comboX+","+comboY+","+comboZ);

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


	/**
	 * Lets you change the feature associated with this icon when its double clicked
	 */
	protected void setAssociatedFeature(GenericMeshFeature newfeature){

		if (newfeature==assocatiedFeature){
			Gdx.app.log(logstag,"already associated with this feature");
			return;
		}

		//clear any other assocations with the new feature
		if (newfeature.getParentMeshIcon()!=null && newfeature.getParentMeshIcon()!=this){
			Gdx.app.log(logstag,"WARNING: iconing new feature already was associated with a mesh icon, removing that assocation");
			newfeature.getParentMeshIcon().setAssociatedFeatureToDefault();
			newfeature.setParentMeshIcon(null);
		}


		//remove old one
		Gdx.app.log(logstag,"removing old feature");

		this.removeAttachment(assocatiedFeature.getAnimatableModelInstance());

		//hide it too
		assocatiedFeature.hide();


		//setup new one
		Gdx.app.log(logstag,"setting new feature");

		this.assocatiedFeature = newfeature;	
		newfeature.setParentMeshIcon(this);
		setupAssociatedFeature();

	}

	void setAssociatedFeatureToDefault() {
		setAssociatedFeature(defaultAssociatedFeature);

	}

	public void refreshAssociatedFeature() {
		setupAssociatedFeature(); //this updates the size of this icon when maximized/open





	}

	/**
	 * Icons linked to this one by lines
	 */
	HashMap<MeshIcon,AnimatableModelInstance> linkedIcons = new HashMap<MeshIcon,AnimatableModelInstance>();

	protected GenericMeshFeature defaultAssociatedFeature;

	/**
	 * Sets a glowing line between this icon and another
	 */
	public void addLineTo(MeshIcon target){

		AnimatableModelInstance existing = linkedIcons.get(target); //is there already a existing connection? 

		if ( existing!=null ){

			//update the existing
			Gdx.app.log(logstag,"updating connecting line");
			existing.lookAt(target,Vector3.Y); //rotation
			//need to scale as well somehow? (that is size to the correct length between objects)


			return;

		} else {

			//else make a new one
			Gdx.app.log(logstag,"Adding new connecting line");
			AnimatableModelInstance Linksline = ModelMaker.addConnectingLine(this, target);

			linkedIcons.put(target,Linksline);

			Linksline.setInheritedRotation(false);

			attachThis(Linksline, new PosRotScale(0,0,-25f)); //a little behind this icon to allow movement a bit

			ModelManagment.addmodel(Linksline,ModelManagment.RenderOrder.zdecides);

		}



	}

	/**
	 * Deletes all lines linking this icon to others
	 * The lines remain hidden on the stage, so be careful if you still have referances as they will still exist
	 * Best to try to update line positions in future, rather then remove them and re-add.
	 */
	public void clearAllLinkLines(){

		Gdx.app.log(logstag,"removing all lines:"+linkedIcons.size());

		Iterator<MeshIcon> iconIt = linkedIcons.keySet().iterator();

		while (iconIt.hasNext()) {			
			MeshIcon meshIcon = (MeshIcon) iconIt.next();
			//this.removeLineTo(meshIcon); //can't use this due to linkedIcons being effected within it
			AnimatableModelInstance lineModel = linkedIcons.get(meshIcon);
			Gdx.app.log(logstag,"removing attachment");
			this.removeAttachment(lineModel);

			lineModel.hide(); //hide it too;

			iconIt.remove();			
		}


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


	public void setMovement(NewMovement... create){

		Gdx.app.log(logstag,"Starting movement lastloc="+this.getTransform()); //hmm never changes?

		if (movementController==null){

			movementController = new NewMovementController(this.getTransform(), create);

		} else {
			movementController.clearMovement();
			movementController.setMovement(this.getTransform(), false, create);			

		}

		ModelManagment.addMoving(this);

	}
	@Override
	public void updatePosition(float deltatime) {

		PosRotScale newPosition = movementController.getUpdate(deltatime);
		this.setTransform(newPosition);

		if (!movementController.isMoving()){

			ModelManagment.removeMoving(this);
		}

	}


}
