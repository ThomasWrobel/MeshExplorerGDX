package com.lostagain.nl.me.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
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
 * The debug camera is for debugging the draw order, it is not attached to anything and allows you to see where the normal camera is
 * vs everything else 
 * 
 * @author Tom
 *
 */
public class DebugCamera extends AnimatablePerspectiveCamera {
	final static String logstag = "ME.DebugCamera";

	Matrix4 startingLocation = new Matrix4();

	static int defaultFieldOfView = 60;
	static int defaultViewportWidth = 640;
	static int defaultViewportHeight = 480;


	public boolean active=false;
	
	public void setActive(boolean active) {
		this.active = active;
		
		//hide or show the main cameras visualizer based on if this DebugCamera is active or not
		if (active){
			MainExplorationView.camera.showCameraVisualizer();
		} else {
			MainExplorationView.camera.hideCameraVisualizer();
		}
	}


	PerspectiveCamera dummycam;

	/**
	 * Handles the cameras motions
	 * Note; Not all movement types support full 3d yet - specifically rotations are 2d.
	 * Translations should be ok, and thats all thats needed for the camera right now.
	 */
	NewMovementController movement;

	public DebugCamera() {
		super(defaultFieldOfView, defaultViewportWidth, defaultViewportHeight);
		near=0.3f;
		far=1900.0f;
		movement = new NewMovementController(new PosRotScale(this.view));

	}





	public DebugCamera(int i, int width, int height) {
		super(i, width, height);
		near=0.3f;
		far=1900.0f;
		movement = new NewMovementController(new PosRotScale(this.view));


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

		}

	}



	public void setTargetPosition(Vector3 newposition) {

		if (newposition==null){
			return;
		}


		PosRotScale currentLoc = new PosRotScale().setToPosition(transState.position);


		Gdx.app.log(logstag,"moving to: "+newposition);

		movement.setMovement(currentLoc, false,  NewMoveTo.create(currentLoc,newposition.x,newposition.y,newposition.z,4000));


	}

	/**
	 * Rotates both the camera and the overlay effect by the angle specified along the axis specified
	 */
	@Override
	public void rotate (Vector3 axis, float angle) {		
		super.rotate( axis,  angle);
	}

	
	public void handleInput(){
		
		if (Gdx.input.isKeyPressed(Keys.P))
		{
			this.rotate(new Vector3(0, 1,0),3);       	
		}
		
		if (Gdx.input.isKeyPressed(Keys.L))
		{
			this.rotate(new Vector3(0, 1,0),-3);       	
		}
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			transState.position.x = transState.position.x-(200* Gdx.graphics.getDeltaTime());  
		}

		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{        	
			transState.position.x = transState.position.x+(200* Gdx.graphics.getDeltaTime());
		}

		if (Gdx.input.isKeyPressed(Keys.UP))
		{			
		//	camera.setTargetPosition(currentPos);
			transState.position.y = transState.position.y+(200* Gdx.graphics.getDeltaTime());
		}

		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{        	
			transState.position.y = transState.position.y-(200* Gdx.graphics.getDeltaTime());
		}
		if (Gdx.input.isKeyPressed(Keys.Z))
		{
			transState.position.z = transState.position.z+(150* Gdx.graphics.getDeltaTime()); 
			/*
			if ( currentmode == cammode.ortha){
			//	CurrentZoom = CurrentZoom +(2* Gdx.graphics.getDeltaTime()); 

				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formula works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				
				//Gdx.app.log(logstag,currentPos.z+","+CurrentZoom);
			}*/
		}

		if (Gdx.input.isKeyPressed(Keys.A) && transState.position.z>-0.5)
		{        	
			transState.position.z = transState.position.z-(150* Gdx.graphics.getDeltaTime());
/*
			if ( currentmode == cammode.ortha){
				
				float newzoom = (0.0133333315344f*currentPos.z)-4.9199985961765f;	//the formula works out a ratio between zoom and z position			
				CurrentZoom = newzoom;
				
			}*/
		}

	//	MainExplorationView.infoPopUp.displayMessage("DEBUG CAMERAupdated");
		
		this.sycnTransform();
	}

}
