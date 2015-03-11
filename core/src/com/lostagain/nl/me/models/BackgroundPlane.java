package com.lostagain.nl.me.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.lostagain.nl.me.models.ModelManagment.RenderOrder;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;
import com.lostagain.nl.me.newmovements.PosRotScale;
import com.lostagain.nl.shaders.MyShaderProvider;

/**
 * A colored plane that blocks the clicks hitting stuff under it
 * 
 * @author Tom
 *
 */
public class BackgroundPlane extends AnimatableModelInstance implements hitable {

	final static String logstag = "ME.BackgroundPlane";
	BoundingBox collisionBox = new BoundingBox();
	
	float lastHitDistance = -1f; //no hit by default
	
	/**
	 * Creates a plane at the specified co-ordinates with the specified color
	 */
	public static BackgroundPlane createBackgroundPlane(int x,int y,int z,int sizeX, int sizeY, Color MColor)
	
	{				
		//make the material (flat, with color specified)
		//This will be manipulatable latter so as to set noise on/off

        Material mat = new Material
        		(
        		ColorAttribute.createDiffuse(MColor), 
				new BlendingAttribute(1f)
        		);

				
		Model model = ModelMaker.createRectangle((-sizeX/2), (-sizeY/2), (sizeX/2), (sizeY/2), z, mat);
				
		return new BackgroundPlane(model,x+(sizeX/2),+(sizeY/2),z);
	}
	
	private BackgroundPlane(Model model,int x,int y,int z) {
		
		super(model);				
		super.setToPosition(new Vector3(x,y,z));
		
		//calculate its bounding box
		super.calculateBoundingBox(collisionBox);
		
		ModelManagment.addmodel(this, RenderOrder.behindStage);
		
	}

	@Override
	public Vector3 getCenter() {
		return super.transState.position.cpy();
	}

	@Override
	/** returns -1. Rectangles are not  known for there radius's. ***/
	public int getRadius() {		
		return -1;
	}

	@Override
	public PosRotScale getTransform() {
		return super.transState;
	}

	@Override
	public void fireTouchDown() {

	}

	@Override
	public void fireTouchUp() {

	}

	@Override
	public void setLastHitsRange(float range) {
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
		
		return Intersector.intersectRayBoundsFast(ray, collisionBox);
	}

	public void setColour(Color col) {

		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);
		
		super.materials.get(0).clear();
		super.materials.get(0).set(ColorAttribute.createDiffuse(col));
		super.materials.get(0).set(blendingAttribute2);

		Gdx.app.log(logstag,"setting shader to normal:");
		super.userData=MyShaderProvider.shadertypes.standardlibgdx;
		
	}

}
