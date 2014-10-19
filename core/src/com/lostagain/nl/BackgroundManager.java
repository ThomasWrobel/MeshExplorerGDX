package com.lostagain.nl;

import java.util.logging.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
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


	public ModelInstance addConnectingLine(LocationContainer From,LocationContainer To){

		Log.info("___________AddConnectingLine:"+From.getWidth());
		Log.info("___________AddConnectingLine:"+From.isVisible());
		Log.info("___________AddConnectingLine:"+From.getCenterX()+","+From.getCenterY()+" to "+To.getCenterX()+","+To.getCenterY());
		Log.info("___________AddConnectingLine From:"+From.LocationsNode.getPLabel());
		Log.info("___________AddConnectingLine To:"+To.LocationsNode.getPLabel());
		
		float x = From.getCenterX();
		float y = From.getCenterY();

		float width = 30;
		float height = 500;

		
		//get angle
		float x2 = To.getCenterX();
		float y2 = To.getCenterY();

		Vector2 corner2 = new Vector2(x,y);
		Vector2 corner3 = new Vector2(x2,y2);

		corner2.sub(corner3);      

		//Log.info("angle="+corner2.angle());
		// Log.info("length="+corner2.len());

		float ang = corner2.angle()+90;

		ModelInstance newline = createRectangleAt(x,y,width,corner2.len(),-20);
		
		//newline.materials.get(0).set(new BlendingAttribute(0.25f));

		Matrix4 newmatrix = new Matrix4();
		newmatrix.setToRotation(0, 0, 1, ang);

		newline.transform.mul(newmatrix);
		//newlinetop.transform.mul(newmatrix);
				
		lines.add(newline);
		instances.add(newline);

		//lines.add(newlinetop);
		//instances.add(newlinetop);
		

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

	private Model glowingRectangle(Vector3 corner1,
			Vector3 corner2, Vector3 corner3, Vector3 corner4,Color MColor ) {

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

		meshBuilder.rect(
				-30,
				-30, 
				0,

				30,
				-30, 
				0,

				30,
				50, 
				0,

				-30,
				50, 
				0,   		   			

				0,
				1,	
				0);
		
		Node node4 = modelBuilder.node();
		node4.translation.set(0,corner1.y,7);
		
		meshBuilder = modelBuilder.part("endblob", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal | Usage.TextureCoordinates,blob );

		meshBuilder.rect(
				-20,
				-20, 
				0,

				20,
				-20, 
				0,

				20,
				40, 
				0,

				-20,
				40, 
				0,   		   			

				0,
				1,
				0);
		//now the glow end blob (bit smaller)
		
		
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

}
