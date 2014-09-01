package com.lostagain.nl;

import java.util.logging.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.LocationGUI.LocationContainer;

public class BackgroundManager {

	static Logger Log = Logger.getLogger("ME.BackgroundManager");

	//3d bits
	public Model model;
	public ModelInstance instance;
	public ModelBatch modelBatch;

	Array<ModelInstance> instances = new Array<ModelInstance>();
	ModelBuilder modelBuilder = new ModelBuilder();

	Array<ModelInstance> lines = new Array<ModelInstance>();

	public void setup(){

		modelBatch = new ModelBatch();


		Model  model1 = modelBuilder.createBox(150f, 15f, 15f, 
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);


		Model  model2 = modelBuilder.createBox(15f, 150f, 15f, 
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);


		//  Model model3 = createRectangle(-10f,500f,10f,-500f,0f);


		//  ModelInstance model4 = createRectangleAt(50f,50f,10f,200f,0f);


		instance = new ModelInstance(model1);        
		instances.add(instance);

		ModelInstance instance2 = new ModelInstance(model2);        
		instances.add(instance2);

		/*
        ModelInstance instance3 = new ModelInstance(model3);        
        instances.add(instance3);

        model4.transform.setToRotation(0, 0, -1, 50);

        instances.add(model4);
		 */
		//instance.transform.setToTranslation(200,500,10);

		//  environment = new Environment();
		//  environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		// environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

	}


	public ModelInstance addConnectingLine(LocationContainer From,LocationContainer Too){

		float x = From.getCenterX();
		float y = From.getCenterY();

		float width = 10;
		float height = 500;


		//get angle
		float x2 = Too.getCenterX();
		float y2 = Too.getCenterY();

		Vector2 corner2 = new Vector2(x,y);
		Vector2 corner3 = new Vector2(x2,y2);

		corner2.sub(corner3);      

		//Log.info("angle="+corner2.angle());
		// Log.info("length="+corner2.len());

		float ang = corner2.angle()+90;

		ModelInstance newline = createRectangleAt(x,y,width,corner2.len(),-20);
		newline.materials.get(0).set(new BlendingAttribute(0.25f));
		

		Matrix4 newmatrix = new Matrix4();
		newmatrix.setToRotation(0, 0, 1, ang);

		newline.transform.mul(newmatrix);


		lines.add(newline);
		instances.add(newline);

		Log.info("x="+x+"y="+y);




		return newline;
	}



	private ModelInstance createRectangleAt(float x,float y,float width,float height,float z) {

		Color MColor = Color.RED;
		Model newractangle =  createRectangle(0,height,width,0,0,MColor);

		ModelInstance newinstance = new ModelInstance(newractangle); 

		newinstance.transform.setToTranslation(x,y,z);


		return newinstance;
	}


	private Model createRectangle(float x1,float y1,float x2,float y2,float z,Color MColor) {
		//(-10f,500f,10f,-500f,0f);
		//(x,,,y                      )
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x1,y2,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x2,y1,z);	


		Model  model3 =  createRectangle(corner1, corner2, corner3, corner4,MColor);


		return model3;
	}

	private Model createRectangle(Vector3 corner1,
			Vector3 corner2, Vector3 corner3, Vector3 corner4,Color MColor ) {


		Material rectmaterial = new Material(ColorAttribute.createDiffuse(MColor), 
				ColorAttribute.createSpecular(Color.RED),new BlendingAttribute(0.3f), 
				FloatAttribute.createShininess(16f));

		return modelBuilder.createRect(
				corner1.x,
				corner1.y, 
				corner1.z,

				corner2.x,
				corner2.y, 
				corner2.z,

				corner3.x,
				corner3.y, 
				corner3.z,

				corner4.x,
				corner4.y, 
				corner4.z,   		   			

				0,
				1,
				0,
				rectmaterial, 
				Usage.Position | Usage.Normal);
	}


	public void dispose() {


		modelBatch.dispose();
		model.dispose();

	}

}
