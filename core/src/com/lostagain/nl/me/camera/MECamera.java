package com.lostagain.nl.me.camera;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.gui.ScreenUtils;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.GWTishModelManagement;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NewMoveTo;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Home to all game camera control functions
 * 
 * Eventually all camera movement should be handled here, as well as changes to the overlay effect to correspond with environment
 * 
 *  * 
 * 
 * @author Tom
 *
 */
public class MECamera extends AnimatablePerspectiveCamera {
	final static String logstag = "ME.MECamera";
	
	public static EffectOverlay mainOverlay= new EffectOverlay();
	public static CameraBackground background= new CameraBackground();
	
	public static CameraVisualiser cameraVisualiserCube = new CameraVisualiser(); 
	
	/** point the lazer shots from **/
	public static  AnimatableModelInstance FirePoint = new AnimatableModelInstance(ModelMaker.createSphereModel(10));
	
	
	
	//temp
	//public static CameraVisualiser angleTest = new CameraVisualiser(); 
	
	//AnimatableModelInstance test attachment;
	
	Matrix4 startingLocation = new Matrix4();

	static int defaultFieldOfView = 60;
	static int defaultViewportWidth = 640;
	static int defaultViewportHeight = 480;

	//standard camera height is now taken from screen size
	//public static float standardCameraHeightAboveLocations=444;
	
	
	
	PerspectiveCamera dummycam;
	
	/**
	 * Handles the cameras motions
	 * Note; Not all movement types support full 3d yet - specifically rotations are 2d.
	 * Translations should be ok, and thats all thats needed for the camera right now.
	 */
	NewMovementController movement;


	public MECamera() {
		super(defaultFieldOfView, defaultViewportWidth, defaultViewportHeight);
		near=0.5f;
		far=1900.0f;
		movement = new NewMovementController(new PosRotScale(this.view));
		
		setupDummyCam();
        addDefaultCameraAttachments();
		
	}


	/** Just a test for now **/
	private void addDefaultCameraAttachments() {
		
		//overlay and background
		super.attachThis(mainOverlay, new PosRotScale(0f, 0f, -115f));
		super.attachThis(background,  new PosRotScale(0f, 0f, -80f)); //note; thanks to draw order shenanigans, the background will be drawn behind everything regardless of distance.
		
		background.hide(); //disable background for now
		mainOverlay.hide(); //disable overlay for now
		
		//add the default visualizer to help show where the camera is
		GWTishModelManagement.addmodel(cameraVisualiserCube,GWTishModelManagement.RenderOrder.OVERLAY);		
		
		PosRotScale camVisPlacement = new PosRotScale(0f,0f,0f);
		camVisPlacement.setToRotation(1, 0, 0, 180); //rotate it so one axis points forward
		super.attachThis(cameraVisualiserCube, camVisPlacement);
				
		//hide visualizer by default
		hideCameraVisualizer();
		
		//fire point in future should be setup by the concept gun
		super.attachThis(FirePoint, new PosRotScale(0f, 100f, -115f));
		GWTishModelManagement.addmodel(FirePoint,GWTishModelManagement.RenderOrder.OVERLAY);
		
		
		
		
		//
		//ModelManagment.addmodel(angleTest,ModelManagment.RenderOrder.infrontStage);		
		//super.attachThis(angleTest, new PosRotScale(0f,0f,0f));
		
		/// X/Y/Z marker to help debug
     //   Material mat = new Material(
     //   		ColorAttribute.createDiffuse(Color.RED), 
	//			ColorAttribute.createSpecular(Color.WHITE),
	///			new BlendingAttribute(1f), 
	//			FloatAttribute.createShininess(16f));
        
    //	ModelBuilder modelBuilder = new ModelBuilder();
		//note; maybe these things could be pre-created and stored rather then a new one each time?
		//Model model =   //modelBuilder.createSphere(55f, 55f, 25f, 20, 20,
			//	mat,Usage.Position | Usage.Normal | Usage.TextureCoordinates );
		
			//	Model model = 	modelBuilder.createXYZCoordinates(35f, mat, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	
		
		//testattachment = new AnimatableModelInstance(model);
		//ModelManagment.addmodel(testattachment,ModelManagment.RenderOrder.infrontStage);
		
		//super.attachThis(testattachment, new PosRotScale(0f,0f,-100f));
		
	}

	/**
	 * removes all the attachments from the camera and scene
	 * @return 
	 */
	public void removeAllDefaultAttachments(){
						
		GWTishModelManagement.removeModel(mainOverlay);
		GWTishModelManagement.removeModel(background);
		GWTishModelManagement.removeModel(cameraVisualiserCube);
		
	}

	public MECamera(int i, int width, int height) {
		super(i, width, height);
		near=0.5f;
		far=1900.0f;
		movement = new NewMovementController(new PosRotScale(this.view));
		
		setupDummyCam();
        addDefaultCameraAttachments();
        

	}
	
	/**
	 * scale will be meaningless and rotation not checked yet
	 * @return
	 */
	public PosRotScale getCamPosRotScale(){
		return this.transState.copy();
	}

	/**
	 * Updates the camera position based on time
	 * 
	 * @param delta
	 */
	public void updatePosition(float delta)
	{
		if (movement.currentMovement!=null){
			PosRotScale newlocation = movement.getUpdate(delta);
			
			super.setTransform(newlocation);			
			
			MainExplorationView.currentPos.set(transState.position);
			
		} else {
			
			//if we arnt running an animation we just directly update

			super.setToPosition(MainExplorationView.currentPos);
			
		//	transState.position.set(MainExplorationView.currentPos);

		//	super.position.set(transState.position.cpy());
			//Gdx.app.log(logstag, "______________________position="+position.x+","+position.y+","+position.z+")");
		}

		
		
		//mainOverlay.transform.setTranslation(transState.position);
		
		//We update the opacity of the effect overlay based on height relative to the standard height
		//float standardHeight = ScreenUtils.getSuitableDefaultCameraHeight()-50; //the -50 ensures it has a little noise even at standard height
		//float heightbasedopacity = (transState.position.z-(standardHeight))/1000.0f; //	380.0f
		//MECamera.mainOverlay.setEffectOpacity(heightbasedopacity);
		
		//we will in future also update based on proximity 
		//to sources of corruption (ie, infovour creatures)
		//We can do this by getting the distance to a location, then checking its population density
		//the population density determines noise when directly over it
		//this falls off proportionately with distance
		
		
	}

	


	public void setTargetPosition(Vector3 newposition) {
		setTargetPosition(newposition,4000);
	}
	public void setTargetPosition(Vector3 newposition,int speed) {
		
		if (newposition==null){
			return;
		}


		//Gdx.app.log(logstag, "______________________current position="+transState.position.x+","+transState.position.y+","+transState.position.z+")");
	//	Gdx.app.log(logstag, "______________________new     position="+newposition.x+","+newposition.y+","+newposition.z+")");
		
		
		PosRotScale currentLoc = new PosRotScale().setToPosition(transState.position);
		


		Gdx.app.log(logstag,"moving to: "+newposition);
		
		movement.setMovement(currentLoc, false,  NewMoveTo.create(currentLoc,newposition.x,newposition.y,newposition.z,speed));
		
		
	}

	/**
	 * Rotates both the camera and the overlay effect by the angle specified along the axis specified
	 */
	@Override
	public void rotate (Vector3 axis, float angle) {
		
		super.rotate( axis,  angle);
		//MECamera.mainOverlay.transform.rotate( axis,  angle);
	
		
		///MECamera.mainOverlay.transform.setToRotation(super.direction,super.up); //rotate( axis,  angle);
		
		//Gdx.app.log(logstag, "______________________current axis="+super.direction+","+super.up);
		
	}


	public void updateAtachment(AnimatableModelInstance object,PosRotScale objectdisplacement) {

		super.updateAtachment(object, objectdisplacement);
		
	}

	
	/** the dummy cam is used to get rays from a clone of this camera at zero,zero,zero looking down.
	 * This is usefull for selectiving things positioned relative to this cameras real position**/
	private void setupDummyCam(){
		
		 dummycam = new PerspectiveCamera(fieldOfView,viewportWidth,viewportHeight);
		
		
	}

	/**
	 * You can use this ray, together with a plane at the right distance, to figure out the intersection pointbetween clicking and a relatively 
	 * positioned object attached to this camera
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Ray getRelativePickRay(float x, float y) {
		return dummycam.getPickRay(x,y);
	}
	
	/**
	 * Get camera displacement for screen relative co-ordinates.
	 * That is, if pinning something to the viewpoint, this translates screen relative top-left co-ordinates
	 * into the co-ordinates needed to be used in the .attachThis() function of this camera.
	 * 
	 * Note; This attachment wont auto-update if the screen viewport resizes
	 */
	public Vector3 getDisplacementForScreenCoOrdinates(float screenX,float screenY,float distanceFromCamera){
	
		Ray ray = getRelativePickRay(screenX, screenY);
		
		Plane testplane = new Plane(new Vector3(0f, 0f,-1f),-distanceFromCamera);
		
		Vector3 intersection = new Vector3();
		Intersector.intersectRayPlane(ray, testplane, intersection);
	
		//Gdx.app.log(logstag,"intersection:"+intersection.x+" , "+intersection.y);
		
		return intersection;
	}
	
	
	
	public void hideCameraVisualizer(){
		cameraVisualiserCube.hide();
	}
	
	public void showCameraVisualizer(){
		cameraVisualiserCube.show();
	}




	//we overall this method to ensure any screen-relative stuff is updated when the camera is
	//this is because screen relative stuff are view-port size dependant
	//@Override
	//public void update(boolean updateFrustum) {
	//	super.update(updateFrustum);	
	//	updateScreenRelativeAttachments();
	//}


	private HashMap<AnimatableModelInstance,Vector3> screenRelativeObjects = new HashMap<AnimatableModelInstance,Vector3>();
	
	
	
	public void updateScreenRelativeAttachments() {

		
		Gdx.app.log(logstag,"updating camera attachments positions");
		
		
		if (screenRelativeObjects==null){
			return;
		}
		
		//loop over and update all with their correct screen relative positions
		for (AnimatableModelInstance object : screenRelativeObjects.keySet()) {
			
			//existing displacement (we look at the existing one as it has the scale info we need to preserve)
			PosRotScale existing = getAttachmentsPoint(object);
			
			//update pos
			Vector3 screenDisplacement   = screenRelativeObjects.get(object);	

			Gdx.app.log(logstag,"screenDisplacement:"+screenDisplacement.x+" , "+screenDisplacement.y);
			
			Vector3 positionDisplacement = getDisplacementForScreenCoOrdinates(screenDisplacement.x,screenDisplacement.y,screenDisplacement.z);
			existing.setToPosition(positionDisplacement);
					

			
			//update attachment with new position
			this.updateAtachment(object, existing);
			
		}
	}


	public void attachThisRelativeToScreen(AnimatableModelInstance object,float screenX,float screenY,float distanceFromCamera) {

		Vector3 posDis = getDisplacementForScreenCoOrdinates(screenX,screenY,distanceFromCamera);
		
		
		PosRotScale displacement = new PosRotScale(posDis); //(new Vector3(intersection.x,intersection.y,-222f)); //(new Vector3(-165f,95f,-222f));
	
		displacement.setToScaling(object.getTransform().scale); //preserve scale
		
		Gdx.app.log(logstag,"attaching at:"+displacement.toString());
		
		//then attach it with that displacement
		MainExplorationView.camera.attachThis(object, displacement);
		
		//add to screen relative object store  (needed in case screen size changes)
		Vector3 rawScreenRelativeData = new Vector3(screenX,screenY,distanceFromCamera);
		screenRelativeObjects.put(object,rawScreenRelativeData);
		
		
	}


	public void updateDummyCam() {
	 //update to match this cams settings
		 
		 dummycam.fieldOfView    = fieldOfView;
		 dummycam.viewportWidth  = viewportWidth;
		 dummycam.viewportHeight = viewportHeight;
			 
		 dummycam.update(true);
		 
	}
	
	
	
}
