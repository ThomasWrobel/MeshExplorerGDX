package com.lostagain.nl.me.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.models.ModelMaker;
import com.lostagain.nl.me.models.ModelManagment;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.NewMoveTo;
import com.lostagain.nl.me.newmovements.NewMovementController;
import com.lostagain.nl.me.newmovements.PosRotScale;

/**
 * Home to all game camera control functions
 * 
 * Eventually all camera movement should be handled here, as well as changes to the overlay effect to corispond with enviroment
 * 
 * 
 * @author Tom
 *
 */
public class MECamera extends AnimatablePerspectiveCamera {
	final static String logstag = "ME.MECamera";
	
	public static EffectOverlay mainOverlay= new EffectOverlay();
	public static CameraBackground background= new CameraBackground();
	AnimatableModelInstance testattachment;
	
	Matrix4 startingLocation = new Matrix4();

	static int defaultFieldOfView = 60;
	static int defaultViewportWidth = 640;
	static int defaultViewportHeight = 480;
	
	
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
		
		
		//---------
		
		this.setToPosition(MainExplorationView.currentPos);
		
		setTargetPosition(MainExplorationView.zoomToAtStartPos);
	}


	//just a test for now
	private void addDefaultCameraAttachments() {
		
		//overlay and background
		super.attachThis(mainOverlay, new PosRotScale(0f, 0f, -5f));
		super.attachThis(background, new PosRotScale(0f, 0f, -600f));
		
		
		/// X/Y/Z marker to help debug
        Material mat = new Material(
        		ColorAttribute.createDiffuse(Color.RED), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));
        
    	ModelBuilder modelBuilder = new ModelBuilder();
		//note; maybe these things could be pre-created and stored rather then a new one each time?
		//Model model =   //modelBuilder.createSphere(55f, 55f, 25f, 20, 20,
			//	mat,Usage.Position | Usage.Normal | Usage.TextureCoordinates );
		
				Model model = 	modelBuilder.createXYZCoordinates(35f, mat, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
	
		
		testattachment = new AnimatableModelInstance(model);
		ModelManagment.addmodel(testattachment,ModelManagment.RenderOrder.infrontStage);
		
		super.attachThis(testattachment, new PosRotScale(0f,0f,-100f));
		
	}

	/**
	 * removes all the attachments from the camera and scene
	 * @return 
	 */
	public void removeAllDefaultAttachments(){
						
		ModelManagment.removeModel(testattachment);
		
	}

	public MECamera(int i, int width, int height) {
		super(i, width, height);
		near=0.5f;
		far=1900.0f;
		movement = new NewMovementController(new PosRotScale(this.view));
		
		setupDummyCam();
        addDefaultCameraAttachments();
        
		this.setToPosition(MainExplorationView.currentPos);

		setTargetPosition(MainExplorationView.zoomToAtStartPos);

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
			
			//transState.setTo(newlocation);;
			
			
			
			//this.setTransform(newlocation);
			//super.transState.position = newlocation.position;
			
			
			//super.position.set(transState.position.cpy());
		
			//newlocation.getTranslation(transState.position);
			
		
			MainExplorationView.currentPos.set(transState.position);
		} else {
			
			//if we arnt running an animation we just directly update

			super.setToPosition(MainExplorationView.currentPos);
			
		//	transState.position.set(MainExplorationView.currentPos);

		//	super.position.set(transState.position.cpy());
			//Gdx.app.log(logstag, "______________________position="+position.x+","+position.y+","+position.z+")");
		}

		
		//mainOverlay.transform.setTranslation(transState.position);
		float heightbasedopacity = (transState.position.z-380.0f)/1000.0f; //(600 - 1000)/1000 		
		MECamera.mainOverlay.setEffectOpacity(heightbasedopacity);
		
		//mainOverlay.transform.setTranslation(transState.position);
	//	background.transform.setTranslation(transState.position).mul(new Matrix4().setToTranslation(0, 0, -600));
		
	}

	

	public void setTargetPosition(Vector3 newposition) {
		
		if (newposition==null){
			return;
		}


		//Gdx.app.log(logstag, "______________________current position="+transState.position.x+","+transState.position.y+","+transState.position.z+")");
	//	Gdx.app.log(logstag, "______________________new     position="+newposition.x+","+newposition.y+","+newposition.z+")");
		
		
		PosRotScale currentLoc = new PosRotScale().setToPosition(transState.position);
		


		
		movement.setMovement(currentLoc, false,  NewMoveTo.create(currentLoc,newposition.x,newposition.y,newposition.z,4000));
		
		
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


	public void updateAtachment(AnimatableModelInstance lazer,
			PosRotScale lazerbeamdisplacement) {

		super.updateAtachment(lazer, lazerbeamdisplacement);
		
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
}
