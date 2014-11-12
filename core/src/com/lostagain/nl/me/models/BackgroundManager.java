package com.lostagain.nl.me.models;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.lostagain.nl.me.LocationGUI.LocationsHub;

public class BackgroundManager {

//;	static Logger Log = Logger.getLogger("ME.BackgroundManager");

	final static String logstag = "ME.BackgroundManager";
	//3d bits
	public Model model;
	public ModelInstance instance;
	public ModelBatch modelBatch;

	//public static Array<ModelInstance> instances = new Array<ModelInstance>();
	
	ModelBuilder modelBuilder = new ModelBuilder();

	Array<ModelInstance> lines = new Array<ModelInstance>();

	 //Experiments
	
	static Pixmap pixmap;
	static ByteBuffer temp;
	
	static NoiseAnimation testNoise = new NoiseAnimation();
	
	private static ArrayList<ModelInstance> animatedbacks = new ArrayList<ModelInstance>();
	
	
	public void setup(){

		modelBatch = new ModelBatch();
		
		Gdx.app.log(logstag,"creating testbounc");
		testNoise.create();
		
		Model  model1 = modelBuilder.createBox(150f, 15f, 15f, 
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);


		Model  model2 = modelBuilder.createBox(15f, 150f, 15f, 
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);


		//  Model model3 = createRectangle(-10f,500f,10f,-500f,0f);


		//  ModelInstance model4 = createRectangleAt(50f,50f,10f,200f,0f);


		instance = new ModelInstance(model1);        
		ModelManagment.addmodel(instance);

		ModelInstance instance2 = new ModelInstance(model2);        
		ModelManagment.addmodel(instance2);
		
		
		
		Gdx.app.log(logstag,"texture test=");
		
	//	Node tshirtNode = instance3.nodes.get(0);
//	Material material = tshirtNode.parts.get(0).material;
		
		Gdx.app.log(logstag,"texture test=2");
		
		
		
		
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

	public void updateAnimatedBacks(float deltatime){
		
		TextureRegion currentimage = testNoise.getKeyFrame(deltatime);
		
		for (ModelInstance instance : animatedbacks) {
			
			TextureAttribute attribute = instance.materials.get(0).get(TextureAttribute.class, TextureAttribute.Diffuse);
			
			if (attribute!=null){			
				attribute.set(currentimage );		
			} else {
				Gdx.app.log(logstag,"________************________________attribute is null:");
			}
					
		}
		
		
	}
	
	public static ModelInstance addNoiseRectangle(int x, int y, int w, int h) {
		
		
		ModelInstance newmodel  = new ModelInstance(createNoiseRectangle(x, y, x+w,y+h, -110, Color.BLACK));
		ModelManagment.addmodel(newmodel);		
		
		animatedbacks.add(newmodel);
				
		return newmodel;
	}
	
	public static void removeModelInstance(ModelInstance model){
		
		
		ModelManagment.removeModel(model);
		
		animatedbacks.remove(model);
		
	}


	public ModelInstance addConnectingLine(LocationsHub From,LocationsHub To){

		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.getWidth());
		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.isVisible());
		Gdx.app.log(logstag,"___________AddConnectingLine:"+From.getX(Align.center)+","+From.getY(Align.center)+" to "+To.getX(Align.center)+","+To.getY(Align.center));
		Gdx.app.log(logstag,"___________AddConnectingLine From:"+From.LocationsNode.getPLabel());
		Gdx.app.log(logstag,"___________AddConnectingLine To:"+To.LocationsNode.getPLabel());
		
		float x = From.getX(Align.center);
		float y = From.getY(Align.center);
		float width = 30;
		//float height = 500;

		//get angle
		float x2 = To.getX(Align.center);
		float y2 = To.getY(Align.center);;

		ModelInstance newline = createLine(x, y, width, x2, y2,-20,Color.RED);


		lines.add(newline);
		//instances.add(newline);
		ModelManagment.addmodel(newline);
		
		//lines.add(newlinetop);
		//instances.add(newlinetop);
		


		return newline;
	}

	static public ModelInstance createLine(float fromX, float fromY, float width,
			float tooX, float tooY,float atZ, Color col) {
		
		Vector2 corner2 = new Vector2(fromX,fromY);
		Vector2 corner3 = new Vector2(tooX,tooY);

		corner2.sub(corner3);      

		//Log.info("angle="+corner2.angle());
		// Log.info("length="+corner2.len());

		float ang = corner2.angle()+90;

		ModelInstance newline = createRectangleAt(fromX,fromY,width,corner2.len(),atZ,col);
		
		//newline.materials.get(0).set(new BlendingAttribute(0.25f));

		Matrix4 newmatrix = new Matrix4();
		newmatrix.setToRotation(0, 0, 1, ang);

		newline.transform.mul(newmatrix);
		//newlinetop.transform.mul(newmatrix);

		Gdx.app.log(logstag,"x="+fromX+"y="+fromY);

		return newline;
	}



	private static ModelInstance createRectangleAt(float x,float y,float width,float height,float z, Color MColor) {

		//Color MColor = Color.RED;
		Model newractangle =  createGlowingRectangle(0,height,width,0,0,MColor);

		ModelInstance newinstance = new ModelInstance(newractangle); 

		newinstance.transform.setToTranslation(x,y,z);

		

		return newinstance;
	}


	public static Model createGlowingRectangle(float x1,float y1,float x2,float y2,float z,Color MColor) {
		//x1 =0
		//y1 =height
		//x2 =width
		//y2 =0
		
		float width = Math.abs(x1-x2);
		//
		float vertdisp = width/2;
		
		
		//(-10f,500f,10f,-500f,0f);
		//(x,,,y                      )
		Vector3 corner1 = new Vector3(x1-vertdisp,y1,z);
		Vector3 corner2 = new Vector3(x1-vertdisp,y2,z);
		Vector3 corner3 = new Vector3(x2-vertdisp,y2,z);
		Vector3 corner4 = new Vector3(x2-vertdisp,y1,z);	


		Model  model3 =  glowingRectangle(corner1, corner2, corner3, corner4,MColor);


		return model3;
	}

	
	private static Model glowingRectangle(Vector3 corner1,
			Vector3 corner2, Vector3 corner3, Vector3 corner4, Color MColor ) {

		//MColor = Color.WHITE;
		
		Material lowmaterial = new Material(ColorAttribute.createDiffuse(MColor), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(0.3f), 
				FloatAttribute.createShininess(16f));

		
        FileHandle imageFileHandle = Gdx.files.internal("data/beam_low.png"); 
        Texture lowtexture = new Texture(imageFileHandle);
        		
        lowmaterial.set(TextureAttribute.createDiffuse(lowtexture));
		
		Material uppermaterial = new Material(ColorAttribute.createDiffuse(Color.WHITE), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(1f), 
				FloatAttribute.createShininess(16f));

		
		BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE);

        FileHandle imageFileHandle2 = Gdx.files.internal("data/beam_top.png"); 
        Texture texture = new Texture(imageFileHandle2);
        		
        lowmaterial.set(blendingAttribute);	
        uppermaterial.set(blendingAttribute);		
        uppermaterial.set(TextureAttribute.createDiffuse(texture));
        //-------------------------
        

        FileHandle imageFileHandle3 = Gdx.files.internal("data/beam_blob.png"); 
        Texture blobtexture = new Texture(imageFileHandle3);
        
        Material blob = new Material(ColorAttribute.createDiffuse(Color.PINK), 
				ColorAttribute.createSpecular(Color.WHITE),new BlendingAttribute(0.7f), 
				FloatAttribute.createShininess(16f));

        blob.set(TextureAttribute.createDiffuse(blobtexture));
        blob.set(blendingAttribute);		
        
		/*
		modelBuilder.end();
		modelBuilder.begin();
		modelBuilder.node().id = "bottom";
		modelBuilder.createRect(
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
		
		Model model = modelBuilder.end();
		*/
		
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;
		
		meshBuilder = modelBuilder.part("bottom", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates, lowmaterial);
		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
	 
		Node node = modelBuilder.node();
		node.translation.set(0,0,5);		
		
		meshBuilder = modelBuilder.part("part2", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,uppermaterial );
			
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		Node node3 = modelBuilder.node();
		node3.translation.set(0,0,6);
		
		//now the glow start blob
		meshBuilder = modelBuilder.part("startblob", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,blob );
		
		float hwidth = (corner3.x-corner3.y)+10;	//width of ends is 10 more then the rest	
		float hheight = (float) (hwidth *1.3); //height slightly longer
		
		Gdx.app.log(logstag,"hwidth="+hwidth);
		//Gdx.app.log(logstag,"width=="+(corner4.y-corner4.x));
		
		//hwidth used to be 30
		meshBuilder.rect(
				-hwidth,
				-hwidth, 
				0,

				hwidth,
				-hwidth, 
				0,

				hwidth,
				hheight, 
				0,

				-hwidth,
				hheight, 
				0,   		   			

				0,
				1,	
				0);
		
		Node node4 = modelBuilder.node();
		node4.translation.set(0,corner1.y,7);
		
		meshBuilder = modelBuilder.part("endblob", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,blob );

		//now the glow end blob (bit smaller)
		meshBuilder.rect(
				-hwidth,
				-hheight, 
				0,

				hwidth,
				-hheight, 
				0,

				hwidth,
				hheight, 
				0,

				-hwidth,
				hheight, 
				0,   		   			

				0,
				1,
				0);
		
		
		
		//corner3.y
		/*
		VertexInfo blob1 = new VertexInfo();
		blob1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo blob2 = new VertexInfo();
		blob2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo blob3 = new VertexInfo();
		blob3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo blob4 = new VertexInfo();
		blob4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(blob1, blob2, blob3, blob4);
		//meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
		
		meshBuilder.rect(blob1, blob2, blob3, blob4);

		
		meshBuilder.rect(
				-50,
				-50, 
				corner1.z,

				-50,
				50, 
				corner2.z,

				50,
				50, 
				corner3.z,

				50,
				-50, 
				corner4.z,   		   			

				0,
				1,
				0);
		*/
		//meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);

		Model model = modelBuilder.end();
		
		
		return model;
	}


	public void dispose() {


		modelBatch.dispose();
		model.dispose();

	}

	
	private static void sortTransparentObjects(final Camera camera){
		/*
		
		instances.sort(new Comparator<ModelInstance>() {
			
			private final Vector3 tmpV1 = new Vector3();
			private final Vector3 tmpV2 = new Vector3();
			
			@Override
			public int compare(ModelInstance o1, ModelInstance o2) {
				
				
				//Rather then single bits look for any;
												
			
				//OMLY TESTS FIRST MATERIAL NODE OF EACH OBJECT (should test all)
				final boolean b1 = o1.materials.get(0).has(BlendingAttribute.Type) && ((BlendingAttribute)o1.materials.get(0).get(BlendingAttribute.Type)).blended;
				final boolean b2 = o2.materials.get(0).has(BlendingAttribute.Type) && ((BlendingAttribute)o2.materials.get(0).get(BlendingAttribute.Type)).blended;
				
				if (b1 != b2) return b1 ? 1 : -1;
				
				
				// FIXME implement better sorting algorithm
				// final boolean same = o1.shader == o2.shader && o1.mesh == o2.mesh && (o1.lights == null) == (o2.lights == null) &&
				// o1.material.equals(o2.material);
				
				o1.transform.getTranslation(tmpV1);				
				o2.transform.getTranslation(tmpV2);
				
				//o1.worldTransform.getTranslation(tmpV1);
				//o2.worldTransform.getTranslation(tmpV2);
				
				final float dst = (int)(1000f * camera.position.dst2(tmpV1)) - (int)(1000f * camera.position.dst2(tmpV2));
				final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
				
				return b1 ? -result : result;
				
			}
		});
		*/
		
	}
	private static Model createNoiseRectangle(float x1,float y1,float x2,float y2,float z,Color MColor) {
		

       // BlendingAttribute blendingAttribute = new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA,0.1f);

		
		BlendingAttribute blendingAttribute2 = new BlendingAttribute(true,GL20.GL_SRC_ALPHA, GL20.GL_ONE,0.2f);
		
    Texture texture = new Texture(createNoiseImage(300,300));
    //ColorAttribute.createDiffuse(MColor), new BlendingAttribute(0.5f), 
        Material mat = new Material(
				ColorAttribute.createSpecular(Color.WHITE),
				FloatAttribute.createShininess(16f));
        
        mat.set(TextureAttribute.createDiffuse(texture));
        mat.set(blendingAttribute2);
     
     
        
        return createRectangle( x1, y1, x2, y2, z, MColor, mat );
        
	}
	
	
	
	
	static private Model createRectangle(float x1,float y1,float x2,float y2,float z,Color MColor,Material mat ) {
		//x1 =0
		//y1 =height
		//x2 =width
		//y2 =0
		
	//	float width = Math.abs(x1-x2);
		//
	//	float vertdisp = width/2;
		
		
		//(-10f,500f,10f,-500f,0f);
		//(x,,,y                      )
		
		Vector3 corner1 = new Vector3(x1,y1,z);
		Vector3 corner2 = new Vector3(x2,y1,z);
		Vector3 corner3 = new Vector3(x2,y2,z);
		Vector3 corner4 = new Vector3(x1,y2,z);	
		
		/*
		Vector3 corner1 = new Vector3(0,0,0);
		Vector3 corner2 = new Vector3(444,0,0);
		Vector3 corner3 = new Vector3(444,444,0);
		Vector3 corner4 = new Vector3(0,444,0);	
		 */

     // FileHandle imageFileHandle3 = Gdx.files.internal("data/beam_blob.png"); 
        //Texture blobtexture = new Texture(imageFileHandle3);
    
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		MeshPartBuilder meshBuilder;

		//Node node = modelBuilder.node();
		//node.translation.set(11,11,5);		
		
		meshBuilder = modelBuilder.part("bit", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal|Usage.TextureCoordinates, mat);

		
		//meshBuilder.cone(5, 5, 5, 10);
		
		VertexInfo newtest1 = new VertexInfo();
		Vector3 testnorm=new Vector3(0,1,0);
		newtest1.set(corner1, testnorm, Color.WHITE, new Vector2(0f,0f));

		VertexInfo newtest2 = new VertexInfo();
		newtest2.set(corner2, testnorm, Color.WHITE, new Vector2(0f,1f));

		VertexInfo newtest3 = new VertexInfo();
		newtest3.set(corner3, testnorm, Color.WHITE, new Vector2(1f,1f));
		
		VertexInfo newtest4 = new VertexInfo();
		newtest4.set(corner4, testnorm, Color.WHITE, new Vector2(1f,0f));
		
		meshBuilder.rect(newtest1, newtest2, newtest3, newtest4);
	 
		

		Model model = modelBuilder.end();
		
		

		return model;
	}

	
	public static Pixmap createNoiseImage(int w, int h){
		
		
		pixmap = new Pixmap(w, h, Format.RGBA8888);
		pixmap.setColor(22f, 22f, 22f, 0f);
		
		pixmap.fill();
		
		 temp = pixmap.getPixels();
				
	for (int i = 0; i < temp.limit() ; i++) {
		//byte val = temp.get(i);
		
		byte val = (byte) (Math.random()*200);
		temp.put(val);
		
	}
	temp.flip();
	//40000
	
		return pixmap;
		
	}
	
	
}
