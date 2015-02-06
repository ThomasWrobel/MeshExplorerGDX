package com.lostagain.nl.me.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.MainExplorationView;
import com.lostagain.nl.me.movements.MoveTo;
import com.lostagain.nl.me.movements.MovementController;

/**
 * Home to all game camera control functions
 * 
 * Eventually all camera movement should be handled here, as well as changes to the overlay effect to corispond with enviroment
 * 
 * 
 * @author Tom
 *
 */
public class MECamera extends PerspectiveCamera {
	
	public static EffectOverlay mainOverlay= new EffectOverlay();
	Matrix4 startingLocation = new Matrix4();

	final static String logstag = "ME.MECamera";
	
	static int defaultFieldOfView = 60;
	static int defaultViewportWidth = 640;
	static int defaultViewportHeight = 480;
	
	/**
	 * Handles the cameras motions
	 * Note; Not all movement types support full 3d yet - specifically rotations are 2d.
	 * Translations should be ok, and thats all thats needed for the camera right now.
	 */
	MovementController movement;

	public MECamera() {
		super(defaultFieldOfView, defaultViewportWidth, defaultViewportHeight);
		near=0.5f;
		far=1900.0f;
		movement = new MovementController(this.view);
		
		this.position.set(MainExplorationView.currentPos);
		
	//	setTargetPosition(MainExplorationView.zoomToAtStartPos);
	}


	public MECamera(int i, int width, int height) {
		super(i, width, height);
		near=0.5f;
		far=1900.0f;
		movement = new MovementController(this.view);
		

		this.position.set(MainExplorationView.currentPos);
		
		//setTargetPosition(MainExplorationView.zoomToAtStartPos);

	}

	/**
	 * Updates the camera position based on time
	 * 
	 * @param delta
	 */
	public void updatePosition(float delta)
	{
		if (movement.currentMovement!=null){
			Matrix4 newlocation = movement.getUpdate(delta,startingLocation);
		
		
			newlocation.getTranslation(position);
		
			MainExplorationView.currentPos.set(position);
		} else {
			
			//if we arnt running an animation we just directly update
			position.set(MainExplorationView.currentPos);

			//Gdx.app.log(logstag, "______________________position="+position.x+","+position.y+","+position.z+")");
		}

		
		mainOverlay.transform.setTranslation(position);
		float heightbasedopacity = (position.z-380.0f)/1000.0f; //(600 - 1000)/1000 		
		MECamera.mainOverlay.setEffectOpacity(heightbasedopacity);
		
	}

	

	public void setTargetPosition(Vector3 newposition) {
		
		if (newposition==null){
			return;
		}


		Gdx.app.log(logstag, "______________________current position="+position.x+","+position.y+","+position.z+")");
		Gdx.app.log(logstag, "______________________new     position="+newposition.x+","+newposition.y+","+newposition.z+")");
		
		
		Matrix4 currentLoc = new Matrix4().setToTranslation(position);


		
		movement.setMovement(currentLoc, false,  MoveTo.create(currentLoc,newposition.x,newposition.y,newposition.z,4000));
		
		
	}

	/**
	 * Rotates both the camera and the overlay effect by the angle specified along the axis specified
	 */
	@Override
	public void rotate (Vector3 axis, float angle) {
		
		super.rotate( axis,  angle);
		MECamera.mainOverlay.transform.rotate( axis,  angle);
		///MECamera.mainOverlay.transform.setToRotation(super.direction,super.up); //rotate( axis,  angle);
		
		//Gdx.app.log(logstag, "______________________current axis="+super.direction+","+super.up);
		
	}

	
	
}
