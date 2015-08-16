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
import com.lostagain.nl.shaders.NoiseShader;

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
	 * Creates a plane at the specified center co-ordinates with the specified color
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

		return new BackgroundPlane(model,x,y,z);
	}

	private BackgroundPlane(Model model,int x,int y,int z) {

		super(model);				
		super.setToPosition(new Vector3(x,y,z));

		//calculate its bounding box
		recalculateBoundingBox();
		
		ModelManagment.addmodel(this, RenderOrder.behindStage);
	//	ModelManagment.addHitable(this);
		this.setAsHitable(true);
		

	}
	
	@Override	
	public void setToPosition(Vector3 vector3) {		
		super.setToPosition(vector3);
		

		Gdx.app.log(logstag,"transform now="+super.getMatrixTransform());
		//update the bounding boxes position
		recalculateBoundingBox();
	}
//TODO: We can remove the bounding box stuff as the superclass now does it
	
	private void recalculateBoundingBox() {
		super.calculateBoundingBox(collisionBox);
		collisionBox.mul(super.getMatrixTransform());

		Gdx.app.log(logstag,"collision box="+super.getMatrixTransform());
		Gdx.app.log(logstag,"collision box="+collisionBox);
	}
	
	@Override
	public Vector3 getCenterOfBoundingBox() {
		return super.transState.position.cpy();
	}

	//@Override
	///** returns -1. Rectangles are not  known for their radius's. ***/
	//public int getRadius() {		
	//	return -1;
	//}

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

		Gdx.app.log(logstag,"setting hittable hit range to:"+range);
		lastHitDistance = range;

	}

	@Override
	public float getLastHitsRange() {
		return lastHitDistance;
	}
	/**
	 * does this object block whats behind it?
	 * @return
	 */
	@Override
	public objectType getInteractionType() {
		return objectType.Blocker;
	}

/*
	@Override
	public Vector3 rayHits(Ray ray) {
		//boolean hit = Intersector.intersectRayBoundsFast(ray, this.getLocalCollisionBox());
		//Gdx.app.log(logstag,"testing for hit on object:"+hit);
		
		
		//new more precise distance test
		Vector3 intersection = new Vector3();
		boolean hit = Intersector.intersectRayBounds(ray, this.getLocalCollisionBox(), intersection);
		Gdx.app.log(logstag,"testing for hit on object:"+hit);
		if (hit){
			return intersection;
		}
		return null;
	}
*/
	public void setToNoiseShader() {	

		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.6f);
		NoiseShader.NoiseShaderAttribute noiseAttribute = new NoiseShader.NoiseShaderAttribute(false, Color.WHITE,false);

		super.materials.get(0).clear();
		super.materials.get(0).set(blendingAttribute2);
		super.materials.get(0).set(noiseAttribute);


	}

	public void setColour(Color col) {

		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);

		super.materials.get(0).clear();
		super.materials.get(0).set(ColorAttribute.createDiffuse(col));
		super.materials.get(0).set(blendingAttribute2);

		//Gdx.app.log(logstag,"setting shader to normal:");
		//super.userData=MyShaderProvider.shadertypes.standardlibgdx;

	}

	@Override
	public void fireDragStart() {
		// TODO Auto-generated method stub
		
	}

}
