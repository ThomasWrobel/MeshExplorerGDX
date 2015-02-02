package com.lostagain.nl.me.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.lostagain.nl.me.movements.MovementController;

/**
 * Home to all game camera controll functions
 * 
 * Eventually all camera movement should be handled here, as well as changes to the overlay effect to corispond with enviroment
 * 
 * 
 * @author Tom
 *
 */
public class MECamera extends PerspectiveCamera {
	
	public static EffectOverlay mainOverlay= new EffectOverlay();

	
	static int defaultFieldOfView = 60;
	static int defaultViewportWidth = 640;
	static int defaultViewportHeight = 480;
	
	/**
	 * Handles the cameras motions
	 * Note; Not all movement types support full 3d yet - specifically rotations are 2d.
	 * Translations should be ok, and thats all thats needed for the camera right now.
	 */
	MovementController movement = new MovementController();

	public MECamera() {
		super(defaultFieldOfView, defaultViewportWidth, defaultViewportHeight);
		near=0.5f;
		far=1900.0f;
	}


	public MECamera(int i, int width, int height) {
		super(i, width, height);
		near=0.5f;
		far=1900.0f;
	}

	/**
	 * Updates the camera position based on time
	 * 
	 * @param delta
	 */
	public void updatePosition(float delta)
	{
		
	}

	
	
}
