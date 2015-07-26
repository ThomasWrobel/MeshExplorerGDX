package com.lostagain.nl.me.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.lostagain.nl.me.locationFeatures.LocationsHub;
import com.lostagain.nl.me.newmovements.AnimatableModelInstance;

public class ModelMaker {

	final static String logstag = "ME.ModelMaker";
	static public ModelInstance createRectangleAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(0, 0, w,h, 0,mat ));

		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	
	static public ModelInstance createRectangleEndCenteredAt(int x, int y,int z, int w, int h,Color MColor,Material mat) {
		return createRectangleEndCenteredAt( x,  y, z,  w,  h, MColor, mat,0,0);
	}
	
	static public ModelInstance createRectangleEndCenteredAt(int x, int y,int z, int w, int h,Color MColor,Material mat,float disX,float disY) {

		ModelInstance newmodel  = new ModelInstance(createRectangle(-(w/2)+disX, disY, (w/2)+disX,h+disY, 0, mat));

		newmodel.transform.setToTranslation(x,y,z);
		
		return newmodel;
	}
	/**
	 * Creates a model rectangle. At points x1/y1 to x2/y2 at height z.
	 * If material is null it uses a default one
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param z
	 * @param mat
	 * @return
	 */
	
	static public Model createRectangle(float x1,float y1,float x2,float y2,float z,Material mat ) {

		
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x1,y2,z);	
	
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;

		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		if (mat!=null){
			meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);
		} else {
			
			Material defaultmaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE), 
					ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(1f), 
					FloatAttribute.createShininess(16f));
			
			meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, defaultmaterial);
					
		}
		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, Color.WHITE, new Vector2(1f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, Color.WHITE, new Vector2(0f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		Model model = modelBuilder.end();
		

		return model;
	}

	public static ModelInstance createLineBetween(float fromX, float fromY,int width, float tooX, float tooY, int atZ, Color col,Material mat, int lengthMultiplayer) {
		
		float halfwidth = (width/2);
		
		Vector2 fromPoint = new Vector2(fromX,fromY); //we subtrack half the width to make the line center aligned
		Vector2 tooPoint  = new Vector2(tooX,tooY); 

		fromPoint.sub(tooPoint);      

		//Log.info("angle="+corner2.angle());
		//Log.info("length="+corner2.len());

		float ang = fromPoint.angle()+90;

		//Gdx.app.log(logstag, " creating from point:"+fromPoint.x+","+fromPoint.y+" ---- "+tooPoint.x+","+tooPoint.y+"  ang="+ang);
		//ang=ang+45;
		
		float displacementDownY = -60;
		float displacementDownX = 0;
		ModelInstance newline = createRectangleEndCenteredAt((int)(fromX),(int)(fromY),(int)atZ,(int)width,(int)((fromPoint.len()*lengthMultiplayer)), col, mat, displacementDownX,displacementDownY);
		                                       
		//newline.materials.get(0).set(new BlendingAttribute(0.25f));

		Matrix4 newmatrix = new Matrix4();
		newmatrix.setToRotation(0, 0, 1, ang);

		newline.transform.mul(newmatrix);
		//newlinetop.transform.mul(newmatrix);

		Gdx.app.log(logstag,"x="+fromX+"y="+fromY);
		
		return newline;
	}

	static public ModelInstance createSphere(float radius){		
		return new ModelInstance(createSphereModel(radius));
		
	}
	
	public static Model createSphereModel(float radius) {
		
		ModelBuilder modelBuilder = new ModelBuilder();

        Material blob = new Material(
        		ColorAttribute.createDiffuse(Color.PINK), 
				ColorAttribute.createSpecular(Color.WHITE),
				new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));

		//modelBuilder.begin();
		Model model =  modelBuilder.createSphere(radius, radius, radius, 20, 20,
						blob,Usage.Position | Usage.Normal | Usage.TextureCoordinates );
		
		
		return model;
	}
	/**
	 * This object is intended to be a centerpiece of the co-ordinate system.
	 * Useful for debugging, but should not be removed totally as ModelManagement demands a single defaultshaded objected created before everything
	 * else. It uses one of these.
	 * 
	 * @param material
	 * @return
	 */
	public static ModelInstance createCenterPoint(Material material) {

		ModelBuilder modelBuilder = new ModelBuilder();
		//note; maybe these things could be pre-created and stored rather then a new one each time?
		Model model =  modelBuilder.createXYZCoordinates(25f, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
		
		
		
		
		return new ModelInstance(model);
	}



	public static AnimatableModelInstance addConnectingLine(AnimatableModelInstance From,AnimatableModelInstance To){
		float width = 30;
		
		Vector3 from = From.transState.position.cpy();
		Vector3 to   = To.transState.position.cpy();
		
		float distance = from.dst(to);
				
		//we create a rectangle centered at the middle of its base, and the distance we need as its height
		
		//we use the glowing line maker for now but this might be replaced in future for a true shader based one.
		//in which case we will just use a rectangle as follows
		//Model linemodel = ModelMaker.createRectangle(0-hw, 0, hw, distance, 0, defaultmaterial);

		
		Model linemodel = MessyModelMaker.createGlowingRectangle(0, distance, width,0 ,0,Color.RED, true,true );
		
		
		AnimatableModelInstance lineinstance = new AnimatableModelInstance(linemodel);
		
		//position it 
		lineinstance.setToPosition(from);
		
		//angle it
		lineinstance.lookAt(To,Vector3.Y);
		

		Gdx.app.log(logstag,"created line at:"+from);
		
		return lineinstance;
		
	}
	
}
